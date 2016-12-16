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
			while(true) {
				ids = bd.resolveAuctions();
				Thread.sleep(this.seconds * 1000);
				for(int auction_id : ids) {
					
				}
			}
		} catch (InterruptedException e) {
			
		}
	}
	
}
