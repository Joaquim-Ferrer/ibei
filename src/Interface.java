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
	private String auction; //

	public Interface(BDInterface bd) {
		state = "INITIAL";
		reader = new Scanner(System.in);
		this.bd = bd;
		user = null;
		auction = null;
		state_machine();
	}

	/*public Interface(){
		state = "INITIAL";
		user = null;
		auction = null;
	}*/

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
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
				case "SEND_MENSAGE":
					sendMensage();
					break;
				case "AUCTION_MENSAGES":
					seeMensage();
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
			setUser(username);
			this.state = "USER_MENU";
			System.out.println("Welcome to the most beautiful reversed auctions app ever!");
			System.out.println("\n\n" + bd.verifyNewMensage(user) + "\n");
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
		System.out.println("4-Send mensage to auction mural");
		System.out.println("5-See auction messages");
		
		boolean badInput = true;
		while(badInput) {
			int option = Integer.parseInt(reader.nextLine());
			switch(option) {
				case 1:
					badInput = false;
					this.state = "REGISTER_AUCTION";
					break;
				case 2:
					badInput = false;
					this.state = "SEARCH_AUCTIONS";
					break;
				case 3:
					badInput = false;
					this.state = "GET_AUCTION";
					break;
				case 4:
					badInput = false;
					this.state = "SEND_MENSAGE";
					break;
				case 5:
					badInput = false;
					this.state = "AUCTION_MENSAGES";
					break;
				case 0:
					badInput = false;
					logOut();
					this.state = "INITIAL";
					break;
				default:
					errorInput();
			}
		}
	}

	private void seeMensage(){
		System.out.println("Auction ID:");
		int id = Integer.parseInt(reader.nextLine());

		ArrayList<String> mensages = bd.mensagesAuction(id);

		if (mensages == null){
			errorDB();
			state = "USER_MENU";
			return;
		}

		for (String mensage: mensages){
			System.out.println(mensage);
		}

		state = "USER_MENU";
		return;

	}

	private void sendMensage(){
		String estado = "nao visto";
		System.out.println("Auction ID: ");
		int id = Integer.parseInt(reader.nextLine());
		System.out.println("Write your message:");
		String message = reader.nextLine();

		bd.sendMessage(id, this.user, message, estado);

		state = "USER_MENU";
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
		float price = Float.parseFloat(reader.nextLine());
		System.out.println("Deadline (yyyy-MM-dd hh:mm:ss):");
		String deadline = reader.nextLine();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date deadlineDate;
		
		try {
			deadlineDate = format.parse(deadline);
		}
		catch(ParseException e) {
			System.out.println("Bad date input");
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
		if(auctions == null) {
			errorDB();
			state = "USER_MENU";
			return;
		}
		
		for(String auction : auctions) {
			System.out.println(auction);
		}
		state = "USER_MENU";
		return;
	}
	
	private void getAuctionMenu() {
		System.out.println("Auction id:");
		String id = reader.nextLine();
		
		String auction = bd.getAuctionDetails(id);
		System.out.println(auction);
		
		this.auction = id;
		state = "AUCTION_MENU";
	}
	
	private void auctionMenu() {
		System.out.println("1-Make a bid");
		System.out.println("0-Exit auction");
		
		while(true) {
			int option = Integer.parseInt(reader.nextLine());
			if(option == 1) {
				System.out.println("How much do you want to ask for?");
				float bid = Float.parseFloat(reader.nextLine());
				bd.createBid(auction, user, bid);
				break;
			}
			else if(option == 0) {
				auction = null;
				state = "USER_MENU";
				break;
			}
			else {
				errorInput();
			}
		}
			
	}
	
	private void initialMenu() {
		System.out.println("1-Register:");
		System.out.println("2-Log In:");
		System.out.println("0-Exit");
		
		while(true) {
			int option = Integer.parseInt(reader.nextLine());
			
			if(option == 1) {
				state = "REGISTER";
				break;
			}
			else if(option == 2) {
				state = "LOG_IN";
				break;
			}
			else if(option == 0) {
				state = "EXIT";
				break;
			}
			else {
				errorInput();
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
	}
	
	private void errorInput() {
		System.out.println("Option not allowed");
	}
	
	private void errorDB() {
		System.out.println("Database couldn't be reached. Try again later. You'll"
				+ "get by mail a free 0.01c coupon to use whenever you want for "
				+ "your trouble. Thank you!");
	}
}
