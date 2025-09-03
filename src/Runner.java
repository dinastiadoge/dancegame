import java.util.*;
import java.util.concurrent.*;

public class Runner {

    private boolean stop = false;
    private boolean start = true;
    public int points = 0;
    private final Random rd = new Random();
    private final Timer timer = new Timer();
    Scanner scanner = new Scanner(System.in);

    public void run() throws InterruptedException {
        startGame();
        if(!start) System.exit(0);
        do {
            Movement movement = side(createMovement());
            int value = getInput();
            setPoint(movement,value);
        } while (!stop);
            showScore();
            restart();
    }

    private void startGame(){
        System.out.println("---------------------");
        System.out.println("Bem vindo ao jogo da dança!!");
        System.out.println("Digite Y para jogar, N para sair");
        String input = scanner.nextLine();
        if (!input.equalsIgnoreCase("y")){
            start = false;
        };
        System.out.println("---------------------");
    }

    private void restart() throws InterruptedException {
        System.out.println("---------------------");
        System.out.println("Tentar novamente?!");
        System.out.println("Digite Y para jogar, N para sair");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("y")){
            run();
        };
    }

    private int getInput() throws InterruptedException {
        //todo nível de dificuldade
        long timeToHit = rd.nextInt(2, 4); // seconds
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {
            int val = scanner.nextInt();
            scanner.nextLine();
            return  val;
        };

        Future<Integer> future = executor.submit(task);

        try {
            return future.get(timeToHit, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            stop = true;
            return 0;
        } catch (ExecutionException e) {
            throw new RuntimeException("Erro ao ler input", e);
        } finally {
            executor.shutdownNow();
        }
    }

    public void showScore(){
        System.out.println("\nSua pontuação foi: "+ this.points);
    }

    private void setPoint(Movement movement, int input){
        if(movement.hit(input)){
            this.points += 1;
        }
    }

    private Movement side(Movement movement){
        System.out.print("|            ");
        System.out.print(movement.getValue());
        System.out.print("            |");
        System.out.print("    =>");
        return movement;
    }

    private Movement createMovement(){
        int movementValue = rd.nextInt(32)+1;
        return new Movement(movementValue);
    }
}
