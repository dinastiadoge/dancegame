import java.util.TimerTask;

public class EndGameTask extends TimerTask {
    boolean complete = false;
    @Override
    public void run() {
        System.out.println("\n---------------------");
        System.out.println("O jogo acabou!");
        System.out.println("---------------------");
        complete = true;
    }
}
