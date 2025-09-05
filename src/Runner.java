import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class Runner {

    private boolean stop = false;
    private boolean start = false;
    double totalPoints = 0;
    public int points = 0, errors = 0;
    private int origin = 8, bound = 10, movementOrigin = 1, movementBound = 9, errorsBackup = 0;
    private final Random rd = new Random();
    Scanner scanner = new Scanner(System.in);
    final String line ="+-------------------+\n";

    public void run() throws InterruptedException {
        startGame();
        if(start) {
            do {
                Movement movement = side(createMovement());
                int value = Integer.parseInt(getInput());
                setPoint(movement, value);
                setDifficulty(this.points);
            } while (!stop);
            showScore();
            saveScore();
        }
    }

    private void setDifficulty(int points) {
        if(points == 20){
            origin = 4;
            bound = 5;
            movementOrigin = 10;
            movementBound = 99;
        }
        if(points == 40){
            origin = 3;
        }
        if(points == 50){
            origin = 2;
            bound = 3;
            movementOrigin = 99;
            movementBound = 999;
        }
        if(points == 60){
            bound = 5;
            movementBound = 9999;
        }
        if(points == 70){
            origin = 1;
            bound = 3;
            movementBound = 99999;
        }
    }

    private void startGame(){
        System.out.println(line);
        System.out.println("Bem vindo ao jogo da dança!!");
        System.out.println("Digite Y para jogar, N para sair");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("y")){
            start = true;
        }
        System.out.println(line);
    }

    private String getInput() throws InterruptedException {
        long timeToHit = rd.nextInt(origin, bound);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<String> task = () -> {
            String input = scanner.nextLine().trim().replaceAll("[^0-9]", "");
            return input.isEmpty() ? "0" : input;
        };

        Future<String> future = executor.submit(task);

        try {
            return future.get(timeToHit, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException e) {
            scanner = new Scanner(System.in);
            stop = true;
            return "0";
        } finally {
            executor.shutdownNow();
        }
    }

    public void showScore() {
        System.out.println("\n+-------------------+");
        System.out.printf("| %-12s: %5.0f |\n", "Pontuação: ", this.totalPoints);
        System.out.printf("| %-12s: %5d |\n", "Movimentos", this.points + errors);
        System.out.printf("| %-12s: %5d |\n", "Acertos", this.points);
        System.out.printf("| %-12s: %5d |\n", "Erros", this.errors);
        System.out.print(line);
    }

    private void setPoint(Movement movement, int input){
        if(movement.hit(input)){
            this.errorsBackup = 0;
            this.points += 1;
            this.totalPoints += 10 * onStreak();
        }else {
            this.errors += 1;
            this.errorsBackup += 1;
        }
    }

    private double onStreak() {
        if(this.errorsBackup != 0) {
            return 0;
        }
        if(this.points <= 10){
            return 2;
        }
        if(this.points <= 50){
            return 5;
        }
        return 10;
    }

    private Movement side(Movement movement) {
        String value = String.valueOf(movement.getValue());
        int width = 19;
        Random random = new Random();

        int maxStart = width - value.length();
        int start = random.nextInt(Math.max(1, maxStart + 1));

        String formatted = String.format(
                "| %s%s%s | =>",
                " ".repeat(start),
                value,
                " ".repeat(maxStart - start)
        );

        System.out.print(formatted);
        return movement;
    }

    private Movement createMovement(){
        int movementValue = rd.nextInt(movementOrigin, movementBound);
        return new Movement(movementValue);
    }

    private void saveScore() {
        try {
            String name;
            do {
                System.out.print("\nDigite seu nome (3 letras): ");
                name = scanner.nextLine().toUpperCase();
                if (!name.isEmpty()) {
                    break;
                }
            } while (name.length() != 3);

            File file = new File("scores.txt");
            List<String> lines = new ArrayList<>();

            if (file.exists()) {
                lines = new ArrayList<>(Files.readAllLines(file.toPath()));
            }

            // lista de scores (nome, pontos)
            List<Score> scores = new ArrayList<>();
            for (String line : lines) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String player = parts[0];
                    double points = Double.parseDouble(parts[1]);
                    scores.add(new Score(player, points));
                }
            }

            scores.add(new Score(name, totalPoints));
            scores.sort((a, b) -> Double.compare(b.points, a.points));

            if (scores.size() > 3) {
                scores = scores.subList(0, 3);
            }
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Score s : scores) {
                    writer.println(s.name + ";" + s.points);
                }
            }
            System.out.println("\n=== PÓDIO ===");
            for (int i = 0; i < scores.size(); i++) {
                Score s = scores.get(i);
                System.out.printf("%dº %s - %.0f pontos\n", i + 1, s.name, s.points);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
