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
                int value = getInput();
                setPoint(movement, value);
                setDifficulty(this.points);
            } while (!stop);
            showScore();
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
        };
        System.out.println(line);
    }

    private int getInput() throws InterruptedException {
        long timeToHit = rd.nextInt(origin, bound);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> scanner.nextInt();

        Future<Integer> future = executor.submit(task);

        try {
            return future.get(timeToHit, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException e) {
            stop = true;
            return 0;
        } finally {
            executor.shutdownNow();
        }
    }

    public void showScore() {
        System.out.println("\n+-------------------+");
        System.out.printf("| %-10s: %5.0f |\n", "Pontuação: ", this.totalPoints);
        System.out.printf("| %-10s: %5d |\n", "Movimentos", this.points + errors);
        System.out.printf("| %-10s: %5d |\n", "Acertos", this.points);
        System.out.printf("| %-10s: %5d |\n", "Erros", this.errors);
        System.out.print(line);
    }

    private void setPoint(Movement movement, int input){
        if(movement.hit(input)){
            this.points += 1;
            this.totalPoints += input * onStreak(errors);
        }else {
            this.errors += 1;
        }
    }

    private double onStreak(int errors) {
        if(errorsBackup == 0) errorsBackup = errors;
        if(errors != errorsBackup){
            errorsBackup = errors;
            return 1;
        }else {
            return points / 10.0;
        }
    }

    private Movement side(Movement movement) {
        String value = String.valueOf(movement.getValue());
        int width = 20;
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
}
