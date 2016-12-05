import java.sql.SQLException;

public class Client {

	public static void main(String args[]){
		try {
			BDInterface bd = new BDInterface();
			Interface i = new Interface(bd);
		} catch (SQLException e) {
			System.out.println("Cant connect to database");
			return;
		}
	}
}