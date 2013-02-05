package auth;

import java.sql.SQLException;

import network.ServerNetwork;
import database.Database;
import event.CommandReceivedEvent;
import event.IEventReceivedListener;

public class Authenticator implements IEventReceivedListener {

	private Database source;
	private ServerNetwork network;

	public Authenticator(int maxConnections) {
		try {
			this.source = new Database();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Error initializing the database");
			e.printStackTrace();
		}
		this.network = new ServerNetwork(maxConnections);
		this.network.addCommandReceivedEventListener(this);
	}

	@Override
	public void commandReceivedEventOccurred(CommandReceivedEvent evt) {
		// TODO Auto-generated method stub
		
	}

	
	public static void main(String[] args) {

	}
}
