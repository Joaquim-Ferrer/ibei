/**
 * Created by miff on 12/12/16.
 */

public class MessageThread extends Thread {

    private int seconds = 10;
    private BDInterface bd;

    public MessageThread(BDInterface bd, int seconds) {
        this.bd = bd;
        this.seconds = seconds;
    }

    public void run() {
        try {
            while (true) {
                System.out.println(bd.verifyNewMensage());
                Thread.sleep(this.seconds * 1000);
            }
        } catch (InterruptedException e) {

        }

    }
}