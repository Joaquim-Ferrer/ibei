public class OnlineThread extends Thread {
	
	private String user;
	private int seconds = 10; //Time between accesses to database
	private BDInterface bd;
	
	public OnlineThread(String user, BDInterface bd, int seconds) {
		this.user = user;
		this.bd = bd;
		this.seconds = seconds;
	}
	
	public void run() {
		try {
			while(true) {
				bd.updateUserOnline(user);
				Thread.sleep(this.seconds * 1000);
			}
		} catch (InterruptedException e) {
			
		}
	
	}	
}

