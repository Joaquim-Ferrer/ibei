import java.util.ArrayList;

/**
 * Created by miff on 12/12/16.
 */

public class MessageThread extends Thread {

    private String user;
    private int seconds = 10; //Time between accesses to database
    private BDInterface bd;
    private final String msgGreetingText = "DLING DLING DLING NEW MESSAGE!!!\n";
    private final String bidGreetingText = "DLING DLING DLING NEW BID!!!\n";
    
    public MessageThread(String user, BDInterface bd, int seconds) {
        this.user = user;
        this.bd = bd;
        this.seconds = seconds;
    }

    public void run() {
        try {
            while (true) {
                ArrayList<String> messages = bd.verifyNewMessages(user);
                for(String m : messages) {
                    System.out.println(this.msgGreetingText + m + "\n");
                }
                ArrayList<String> bids = bd.verifyNewBids(user);
                for(String b : bids) {
                    System.out.println(this.bidGreetingText + b + "\n");
                }
          
                bd.modifyNotifsState(user);
                Thread.sleep(this.seconds * 1000);
            }
        } catch (InterruptedException e) {

        }
    }
}