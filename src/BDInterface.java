import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class BDInterface {
	private Connection connection = null;
	private final String USER = "bd";
	private final String PASS = "bd";

	private final String SERVER = "localhost";
	private final String PORT = "1521";

	public BDInterface() throws SQLException {
		connection = getConnection();
	}

	public Connection getConnection() throws SQLException {
		Properties connectionProps = new Properties();
		connectionProps.put("user", USER);
		connectionProps.put("password", PASS);

		Connection conn = DriverManager.getConnection("jdbc:oracle:thin://@" + SERVER + ":" + PORT + "/XE", connectionProps);
		conn.setAutoCommit(false);

		return conn;
	}

	public int createUser(String username, String password) {
		String query = "INSERT INTO utilizador VALUES (?, ?)";
		System.out.println(query);

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.executeUpdate();
			connection.commit();
			System.out.println("User created successfully");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}

		return 0;
	}

	public boolean authenticateUser(String username, String password) {

		boolean isAuthenticated = false;

		String query = "SELECT count(*) USER_EXISTS "
				+ "FROM utilizador "
				+ "WHERE USERNAME = ? AND password = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet result = stmt.executeQuery();
			result.next(); //Move to the first row
			if (result.getInt("USER_EXISTS") == 1) {
				isAuthenticated = true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}

		return isAuthenticated;
	}

	public int createAuction(String username, String code, String title,
							 String description, float price, java.util.Date deadlineDate) {

		String query = "INSERT INTO leilao VALUES (?, LEILAO_ID.nextval, ?, ?, ?, ?, ?)";
		Date sqlDate = new Date(deadlineDate.getTime());


		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, code);
			stmt.setString(3, title);
			stmt.setString(4, description);
			stmt.setFloat(5, price);
			stmt.setDate(6, sqlDate);
			stmt.executeUpdate();
			connection.commit();
			System.out.println("Auction created successfully");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
		return 0;
	}

	public ArrayList<String> searchAuctionsByCode(String code) {
		String query = "SELECT id_leilao, titulo"
				+ " FROM leilao"
				+ " WHERE cod_artigo = ?";

		ArrayList<String> result = new ArrayList<String>();

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setLong(1, Long.parseLong(code));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id_leilao");
				String titulo = rs.getString("titulo");
				result.add("Auction number: " + id + "\t\tTitle: " + titulo);
				System.out.println("1");
			}
			return result;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}

	public String getAuctionDetails(String id) {
		String query = "SELECT id_leilao, username, cod_artigo, titulo, descricao, preco_maximo, TO_CHAR(deadline)"
				+ " FROM leilao"
				+ " WHERE id_leilao = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setLong(1, Long.parseLong(id));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				long id_leilao = rs.getLong("id_leilao");
				String username = rs.getString("username");
				String cod_artigo = rs.getString("cod_artigo");
				String titulo = rs.getString("titulo");
				String descricao = rs.getString("descricao");
				Float preco_maximo = rs.getFloat("preco_maximo");
				String deadline = rs.getString("TO_CHAR(deadline)");

				return "ID_LEILAO: " + id_leilao + "\nUSERNAME: " + username + "\nCOD_ARTIGO: " + cod_artigo + "\nTITULO: " + titulo + "\nDESCRICAO: " + descricao + "\nPRECO_MAXIMO: " + preco_maximo + "\nDEADLINE: " + deadline;

			}
			return "";
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public boolean sendMessage(int id, String emissor, String mensagem){
		String query = "INSERT INTO mensagens (id_leilao, username, mensagem) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)){
			stmt.setInt(1, id);
			stmt.setString(2, emissor);
			stmt.setString(3, mensagem);
			stmt.executeUpdate();
			connection.commit();
			System.out.println("Message sent successfully");
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
}

