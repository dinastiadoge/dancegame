import java.util.*;

public class Runner {

    private boolean stop = false;
    private boolean start = false;
    public int points = 0;
    private final Random rd = new Random();
    private final Timer timer = new Timer();
    Scanner scanner = new Scanner(System.in);

    public void run(){
        startGame();
        if(!start) System.exit(0);
        do {
            if(stop) {
                showScore();
                break;
            }
            Movement movement = side(createMovement());
            int value = getInput();
            setPoint(movement,value);
        } while (!stop);
    }

    private void startGame(){
        System.out.println("---------------------");
        System.out.println("Bem vindo ao jogo da dança!!");
        System.out.println("Digite Y para jogar, N para sair");
        String input = scanner.nextLine();
        setStart(!input.equalsIgnoreCase("y"));
        System.out.println("---------------------");
    }

    private int getInput(){
        //todo fazer um sistema de nível de dificuldade
        long timeToHit = rd.nextInt(2,4);
        EndGameTask endGameTask = new EndGameTask();
        this.timer.schedule(endGameTask, (timeToHit * 1000));
        int value = scanner.nextInt();
        if(endGameTask.complete){
            setStop(true);
            return 0;
        }else {
            endGameTask.cancel();
            return value;
        }
    }

    public void showScore(){
        System.out.println("Sua pontuação foi: "+ this.points);
    }

    private void setPoint(Movement movement, int input){
        if(movement.hit(input)){
            this.points += 1;
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    private Movement side(Movement movement){
        System.out.print("|            ");
        System.out.print(movement.getValue());
        System.out.print("            |");
        System.out.print("          =>");
        return movement;
    }

    private Movement createMovement(){
        int movementValue = rd.nextInt(32)+1;
        return new Movement(movementValue);
    }
}
