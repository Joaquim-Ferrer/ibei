import java.sql.SQLException;
import java.util.*;

public class Client {

	public static void main(String args[]) throws InterruptedException, SQLException {

		try {

			BDInterface bd = new BDInterface();
			
			Interface i = new Interface(bd);


		} catch (SQLException e) {
			System.out.println("Cant connect to database");
			return;
		}
    }
}