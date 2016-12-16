import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

public class Interface  {
	
	private String state;
	private Scanner reader;
	private BDInterface bd;
	private String user; //currently logged in user
	private int auction; //
	private final int secondsBetweenPings = 20;
	private OnlineThread onlineThread;
	private MessageThread messageThread;

	public Interface(BDInterface bd) {
		state = "INITIAL";
		reader = new Scanner(System.in);
		this.bd = bd;
		user = null;
		auction = 0;
		state_machine();
	}

	public void state_machine() {
		while(state != "EXIT") {
			switch(state){
				case "INITIAL":
					initialMenu();
					break;
				case "REGISTER":
					registerMenu();
					break;
				case "LOG_IN":
					logInMenu();
					break;
				case "USER_MENU":
					userMenu();
					break;
				case "REGISTER_AUCTION":
					registerAuctionMenu();
					break;
				case "SEARCH_AUCTIONS":
					searchAuctionsMenu();
					break;
				case "GET_AUCTION":
					getAuctionMenu();
					break;
				case "AUCTION_MENU":
					auctionMenu();
					break;
				case "GET_MY_ACTIVITIES_AUCTIONS":
					getMyActivitiesAuctions();
					break;
				case "EXIT":
					break;
				default:
					System.out.println("Invalid State:  " + state);
					state = "EXIT";
			}
		}
	}

	private void logInMenu() {
		System.out.println("Username:");
		String username = reader.nextLine();
		System.out.println("Password:");
		String password = reader.nextLine();
		
		if(bd.authenticateUser(username, password)) {
			this.user = username;
			this.state = "USER_MENU";
			onlineThread = new OnlineThread(user, bd, secondsBetweenPings);
			onlineThread.start();
			messageThread = new MessageThread(user, bd, secondsBetweenPings);
			messageThread.start();
			System.out.println("Welcome to the most beautiful reversed auctions app ever!");
		}
		else {
			System.out.println("I'm sorry but that account doesn't exist");
			this.state = "INITIAL";
		}
	}

	private void userMenu() {
		System.out.println("0-Log out");
		System.out.println("1-Create Auction");
		System.out.println("2-Search for auctions by its code EAN");
		System.out.println("3-Access auction by its id");
		System.out.println("4-Get auctions that I have activity in:");

		int option;
		try{

			option = Integer.parseInt(reader.nextLine());

		}catch(Exception e) {
			errorInput();
			state = "USER_MENU";
			return;
		}

		switch(option) {
			case 1:
				this.state = "REGISTER_AUCTION";
				break;
			case 2:
				this.state = "SEARCH_AUCTIONS";
				break;
			case 3:
				this.state = "GET_AUCTION";
				break;
			case 4:
				this.state = "GET_MY_ACTIVITIES_AUCTIONS";
				break;
			case 0:
				logOut();
				this.state = "INITIAL";
				break;
			default:
				errorInput();
				this.state = "USER_MENU";
				break;
		}

	}

	private void sendMessage() {
        System.out.println("Write your message:");
        String message = reader.nextLine();
        bd.sendMessage(auction, this.user, message);
        insertMessageNotification(message);
    }
	
    private void insertMessageNotification(String message) {
        ArrayList<String> usersThatMessaged = bd.getMessagers(auction);
        for(String u : usersThatMessaged) {
            if(!u.equals(user)) {
                bd.createNotifMessage(auction, u, user, message);
            }
        }
    }

	private void registerAuctionMenu() {
		System.out.println("Product Code:");
		String code = reader.nextLine();
		if(code.length() == 10) {
			System.out.println("Is your code an ISBN? y/n");
			String option = reader.nextLine();
			if(option == "y") {
				option = "978" + option; //Conversion from old ISBN to new that is compatible with EAN
			}
		}
		System.out.println("Title:");
		String title = reader.nextLine();
		System.out.println("Description:");
		String description = reader.nextLine();
		System.out.println("Maximum price:");
		float price;
		try {
			price = Float.parseFloat(reader.nextLine());
		}
		catch(Exception e) {
			errorInput();
			state = "USER_MENU";
			return;
		}

		System.out.println("Deadline (yyyy-MM-dd hh:mm:ss):");
		String deadline = reader.nextLine();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date deadlineDate;
		
		try {
			deadlineDate = format.parse(deadline);
		}
		catch(ParseException e) {
			errorInput();
			state = "USER_MENU";
			return;
		}
		
		bd.createAuction(user, code, title, description, price, deadlineDate);
		state = "USER_MENU";
		
	}
	
	private void searchAuctionsMenu() {
		System.out.println("Code to search:");
		String code = reader.nextLine();
		
		if(code.length() == 10) {
			System.out.println("Is your code an ISBN? y/n");
			String option = reader.nextLine();
			if(option == "y") {
				code = "978" + code; //Conversion from old ISBN to new that is compatible with EAN
			}
		}
		
		ArrayList<String> auctions = bd.searchAuctionsByCode(code);
		if(auctions.isEmpty()) {
			System.out.println("There are no auctions with that code. But come back tomorrow! Hundreds of people are joining everyday!");
		}
		for(String auction : auctions) {
			System.out.println(auction);
		}
		state = "USER_MENU";
		return;
	}
	
	private void getAuctionMenu() {
		System.out.println("Auction id:");
		int id;
		try{
			id = Integer.parseInt(reader.nextLine());
		}catch(Exception e){
			errorInput();
			this.state = "USER_MENU";
			return;
		}
		String auction = bd.getAuctionDetails(id);
		if(auction == "") {
			System.out.println("There's no such auction");
			this.state = "USER_MENU";
			return;
		}
		System.out.println(auction);
		
		this.auction = id;
		state = "AUCTION_MENU";
	}
	
    private float makeBid() throws NumberFormatException {
        System.out.println("How much do you want to ask for?");
        float bid = Float.parseFloat(reader.nextLine());
        if(bd.createBid(auction, user, bid)) {
            return bid;
        }
        return -1;
    }
    
    private void insertBidNotifications(float bid) {
        ArrayList<String> usersThatBidded = bd.getBidders(auction);
        for(String u : usersThatBidded) {
            if(!u.equals(user)) {
                bd.createNotifAuction(auction, u, bid);
            }
        }
    }
	
	private void auctionMenu() {
		System.out.println("1-Make a bid");
		System.out.println("2-Update auction");
		System.out.println("3-See messages on the auction wall");
		System.out.println("4-Send message to auction wall");
		System.out.println("0-Exit auction");
		
		int option;
		try{
			option = Integer.parseInt(reader.nextLine());
		}catch(Exception e){
			errorInput();
			this.state = "USER_MENU";
			this.auction = 0;
			return;
		}
		
		if(option == 1) {
			float bid;
			try{
				bid = makeBid();
			}catch(Exception e){
				errorInput();
				this.state = "USER_MENU";
				this.auction = 0;
				return;
			}
			if(bid != -1) {
				insertBidNotifications(bid);
			}
		}
		else if(option == 2) {
			editAuction();
		}
		else if(option == 3) {
			ArrayList<String> messages = bd.getAuctionMessages(auction);
			for(String m : messages) {
				System.out.println(m);
				
			}
		}
		else if(option == 4) {
			sendMessage();
		}
		else if(option == 0) {
			auction = 0;
			state = "USER_MENU";
		}
		else {
			errorInput();
			state = "USER_MENU";
		}	
	}

	private void initialMenu() {
		System.out.println("1-Register:");
		System.out.println("2-Log In:");
		System.out.println("3-Check online users");
		System.out.println("0-Exit");

		while(true) {

			int option;
			try{
				option = Integer.parseInt(reader.nextLine());
			}catch(Exception e){
				errorInput();
				this.state = "INITIAL";
				return;
			}

			if(option == 1) {
				state = "REGISTER";
				break;
			}
			else if(option == 2) {
				state = "LOG_IN";
				break;
			}
			else if(option == 3) {
				getOnlineUsers();
				break;
			}
			else if(option == 0) {
				state = "EXIT";
				break;
			}
			else {
				errorInput();
				this.state = "INITIAL";
				break;
			}

		}
	}
	
	private void registerMenu() {
		System.out.println("Username:");
		String username = reader.nextLine();
		System.out.println("Password:");
		String password = reader.nextLine();
		
		bd.createUser(username, password);
		state = "INITIAL";
		
	}
	
	private void logOut() {
		this.user = null;
		this.auction = 0;
		onlineThread.interrupt();
		messageThread.interrupt();
		onlineThread = null;
		messageThread = null;
	}
	
	private void errorInput() {
		System.out.println("Option not allowed");
	}
	
	private void getOnlineUsers() {
		String onlineUsers = bd.getOnlineUsers();
		System.out.println(onlineUsers);
	}

	private void getMyActivitiesAuctions() {
		String myActivitiesAuctions = bd.getUserActivityAuctions(user);
		System.out.println(myActivitiesAuctions);
		state = "USER_MENU";
	}

	private void editAuction() {
		
		System.out.println("New Title:<leave empty if you want to keep the same>");
		String new_title = reader.nextLine();
		System.out.println("New description:<leave empy if you want to keep the same>");
		String new_description = reader.nextLine();

		boolean success1 = true;
		boolean success2 = true;


		if (!new_title.trim().equals("") && !new_description.trim().equals("")){
			bd.selectOldAuction(auction, false);
			success1 = bd.updateAuctionTitle(user, new_title, this.auction, false);
			success2 = bd.updateAuctionDescription(user, new_description, this.auction, false);
		}
		else if(!new_title.trim().equals("")) {
			bd.selectOldAuction(auction, false);
			success1 = bd.updateAuctionTitle(user, new_title, this.auction, false);
		}
		else if(!new_description.trim().equals("")) {
			bd.selectOldAuction(auction, false);
			success2 = bd.updateAuctionDescription(user, new_description, this.auction, false);
		}

		if(success1 && success2) {
			bd.doCommit();
		}
		else{
			bd.doRollback();
		}

		String auction = bd.getAuctionDetails(this.auction);
		System.out.println(auction);
		
	}
}
