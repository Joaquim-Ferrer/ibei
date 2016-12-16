import java.util.ArrayList;


public class AuctionResolver extends Thread{
	private BDInterface bd;
	private final int seconds = 5;
	
	public AuctionResolver(BDInterface bd) {
		this.bd = bd;
	}
	
	public void run() {
		try {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			ArrayList<String> users = new ArrayList<String>();
			while(true) {
				ids.clear();
				users.clear();
				bd.getUnresolvedAuctions(ids, users);
				if(ids.size() > 0){	
					for(int i = 0; i < ids.size(); i++) {
						bd.createNotifMessage(ids.get(i), users.get(i), "admin", "Your auction is finished!"
								+ "You can go check how much you'll have to pay in the auction page!");
					}
					bd.resolveAuctions();
				}
				Thread.sleep(this.seconds * 1000);
			}
		} catch (InterruptedException e) {
			
		}
	}
}
