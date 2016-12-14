import java.util.ArrayList;

/**
 * Created by miff on 12/12/16.
 */

public class MessageThread extends Thread {

    private String user;
    private int seconds = 10; //Time between accesses to database
    private BDInterface bd;

    public MessageThread(String user, BDInterface bd, int seconds) {
        this.user = user;
        this.bd = bd;
        this.seconds = seconds;
    }

    public void run() {
        try {
            while (true) {
                System.out.println(bd.verifyNewMensage(user));
                //System.out.println(result);
                bd.modifyState(user);
                Thread.sleep(this.seconds * 1000);
            }
        } catch (InterruptedException e) {

        }
    }
}