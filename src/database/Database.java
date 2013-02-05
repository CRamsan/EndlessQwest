package database;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Database {

	private static final String DRIVER_CLASS = "org.sqlite.JDBC";
	private static final String ENCRYPTION_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String DB_FILE = "jdbc:sqlite:data//data.db";

	private Connection dbConnection;
	private Random random;

	public Database() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER_CLASS);
		dbConnection = DriverManager.getConnection(DB_FILE);
		random = new Random();
	}

	public boolean createTable() {
		PreparedStatement createTable = null;
		String creation = "CREATE TABLE USERS(USERNAME CHARACTER(20) UNIQUE, PASSWORD CHARACTER(20), EMAIL CHARACTER(30) UNIQUE, SALT CHARACTER(16), UID INTEGER PRIMARY KEY)";
		try {
			createTable = dbConnection.prepareStatement(creation);
			boolean result = createTable.execute();
			createTable.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createAccount(String username, String email, String password) {

		// salt and hash password
		// http://stackoverflow.com/questions/2860943/suggestions-for-library-to-hash-passwords-in-java
		// http://stackoverflow.com/questions/5499924/convert-java-string-to-byte-array
		String hashString = null, saltString = null;
		byte[] salt = new byte[16];
		random.nextBytes(salt);

		try {
			saltString = new String(salt, "ISO-8859-1");
			hashString = this.encrypt(password, saltString);

			PreparedStatement registerUser = null;
			String insertion = "INSERT INTO USERS (USERNAME, PASSWORD, AccountSalt, Email) "
					+ "VALUES (? , ?, ?, ?)";

			registerUser = dbConnection.prepareStatement(insertion);
			registerUser.setString(1, username);
			registerUser.setString(2, hashString);
			registerUser.setString(3, saltString);
			registerUser.setString(4, email);

			boolean result = registerUser.executeUpdate() == 1;

			registerUser.close();
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean confirmAccount(String username, String password) {

		PreparedStatement retrieveAccount = null;
		ResultSet rset = null;
		String query = "SELECT * " + "FROM USERS " + "WHERE USERNAME = ?";

		// retrieve user with given username
		try {
			retrieveAccount = dbConnection.prepareStatement(query);
			retrieveAccount.setString(1, username);

			// run the query, return a result set
			rset = retrieveAccount.executeQuery();
			if (rset.next()) {
				String hashRetrieved = rset.getString("PASSWORD");
				String saltRetrieved = rset.getString("SALT");
				String hashPass = this.encrypt(password, saltRetrieved);
				retrieveAccount.close();
				rset.close();
				if (hashRetrieved.equals(hashPass)) {
					return true;

				} else {
					return false;
				}

			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;

	}

	public boolean deleteAccount(String username, String password) {
		String query = "DELETE FROM USERS WHERE USERNAME = ?";
		PreparedStatement deleteUser = null;
		try {
			deleteUser = dbConnection.prepareStatement(query);
			deleteUser.setString(1, username);
			return deleteUser.executeUpdate() == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String encrypt(String input, String salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			UnsupportedEncodingException {
		byte[] salt2 = salt.getBytes("ISO-8859-1");
		KeySpec spec = new PBEKeySpec(input.toCharArray(), salt2, 2048, 160);
		SecretKeyFactory f = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
		return new String(f.generateSecret(spec).getEncoded());
	}

}
