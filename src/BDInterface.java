import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.lang.*;

// implements Runnable

public class BDInterface{
	private Connection connection = null;
	private final String USER = "bd";
	private final String PASS = "bd";

	private final String SERVER = "localhost";
	private final String PORT = "1521";

	//public Interface i = new Interface();

	public BDInterface() throws SQLException {
		connection = getConnection();
	}

	public Connection getConnection() throws SQLException {
		Properties connectionProps = new Properties();
		connectionProps.put("user",USER);
		connectionProps.put("password", PASS);

		Connection conn = DriverManager.getConnection("jdbc:oracle:thin://@" + SERVER + ":" + PORT + "/XE", connectionProps);
		conn.setAutoCommit(false);

		return conn;
	}

	/*@Override
	public void run() {
		try {
			while(true){
				Thread.sleep(3000);
				if (this.verifyNewMensage(i.getUser()).isEmpty()){
					System.out.println("nuuuul");
					System.out.println("ESTADO: " + i.state);
					System.out.println("UTILIZADOR:" + i.getUser());
				}
				else{
					System.out.println("nao sou null");
					System.out.println("UTILIZADOR:" + i.getUser());

				}
			}


		} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println(e);
		}
	}*/

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
	
	public int createBid(String auction_id, String user, float bid) {
		String query = "INSERT INTO licitacao"
				+ " VALUES (?,?,?,SYSDATE)";
		try(PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setLong(1, Long.parseLong(auction_id));
			stmt.setString(2, user);
			stmt.setFloat(3, bid);
			stmt.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -1;
		}
		return 0;
	}

	public ArrayList<String> searchAuctionsByCode(String code) {
		String query = "SELECT leilao.id_leilao id_leilao, titulo, NVL(MIN(montante), leilao.preco_maximo )montante_min"
				+ " FROM leilao, licitacao"
				+ " WHERE cod_artigo = ? AND leilao.id_leilao = licitacao.id_leilao(+)"
				+ " GROUP BY leilao.id_leilao, titulo, leilao.preco_maximo";

		ArrayList<String> result = new ArrayList<String>();

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setLong(1, Long.parseLong(code));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id_leilao");
				String titulo = rs.getString("titulo");
				float montante_min = rs.getFloat("montante_min");
				result.add("Auction number: " + id + "\t\tTitle: " + titulo + "\t\tLowest Bid: " + montante_min);
				System.out.println("1");
			}
			return result;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}

	public String getAuctionDetails(String id) {
		String query = "SELECT leilao.id_leilao, leilao.username, cod_artigo, titulo, descricao, preco_maximo, TO_CHAR(deadline), NVL(MIN(montante), leilao.preco_maximo) preco_maximo " +
				"FROM leilao, licitacao " +
				"WHERE leilao.id_leilao = ? AND leilao.id_leilao = licitacao.id_leilao(+) " +
				"GROUP BY leilao.id_leilao, leilao.username, cod_artigo, titulo, descricao, preco_maximo, deadline, preco_maximo";


		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setLong(1, Long.parseLong(id));
			stmt.setLong(2, Long.parseLong(id));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				long id_leilao = rs.getLong("id_leilao");
				String username = rs.getString("username");
				String cod_artigo = rs.getString("cod_artigo");
				String titulo = rs.getString("titulo");
				String descricao = rs.getString("descricao");
				Float preco_maximo = rs.getFloat("preco_maximo");
				String deadline = rs.getString("TO_CHAR(deadline)");
				float montante = rs.getFloat("montante");
				//ArrayList<String> mensagem = mensagesAuction(id_leilao);
				return "ID_LEILAO: " + id_leilao + "\nUSERNAME: " + username + "\nCOD_ARTIGO: " + cod_artigo + "\nTITULO: " + titulo + "\nDESCRICAO: " 
					+ descricao + "\nPRECO_MAXIMO: " + preco_maximo + "\nDEADLINE: " + deadline
					+ "\n\n LAST BID: " + montante;

			}
			return "";
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public boolean sendMessage(int id, String emissor, String mensagem, String estado){
        String query = "INSERT INTO mensagens (id_leilao, username, mensagem, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setInt(1, id);
            stmt.setString(2, emissor);
            stmt.setString(3, mensagem);
            stmt.setString(4, estado);
            stmt.executeUpdate();
            connection.commit();
            System.out.println("Message sent successfully");
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }


	public ArrayList<String> mensagesAuction(int id){
		int n_mensage = 1;

		ArrayList<String> result = new ArrayList<String>();

		String query = "SELECT mensagem FROM mensagens WHERE id_leilao=?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String mensagem = rs.getString("mensagem");
				result.add("[" + n_mensage + "] - " + mensagem);
				n_mensage++;
			}
			return result;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}

	}


    public ArrayList<String> verifyNewMensage(String username){

        ArrayList<String> result = new ArrayList<String>();

        String query = "SELECT leilao.id_leilao, leilao.username, mensagens.mensagem "
                + " FROM leilao, mensagens"
                + " WHERE leilao.id_leilao = mensagens.id_leilao AND mensagens.estado = 'nao visto' AND leilao.username = ?";

        //result.add("YOUR MENSAGES:\n");

        try (PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int id_leilao = rs.getInt("id_leilao");
                String mensagem = rs.getString("mensagem");

                result.add("The auction number " + id_leilao + " has the mensage:\n" + mensagem +"\n");

                /*String query_aux = "UPDATE mensagens"
                        + "SET estado = 'visto'"
                        + "WHERE id_leilao = (SELECT leilao.id_leilao " +
                        "FROM leilao, mensagens " +
                        "WHERE leilao.id_leilao = mensagens.id_leilao AND mensagens.estado = 'nao visto' AND leilao.username = ?)";


                try (PreparedStatement st = connection.prepareStatement(query_aux)){
                    stmt.setString(1, "estado");
                    stmt.executeUpdate();
                    connection.commit();
                    System.out.println("Message sent successfully");
                }catch (SQLException e) {
                    System.out.println(e.getMessage());

                }*/

            }

            return result;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

