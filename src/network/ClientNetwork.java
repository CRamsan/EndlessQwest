package network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import main.Command;

public class ClientNetwork extends BaseNetwork {

	private GameConnection serverConnection;

	public ClientNetwork() {

		serverConnection = null;

	}

	public boolean connect(String ipAddress) {
		try {
			serverConnection = new GameConnection(new Socket(ipAddress,
					LISTENING_PORT), this);
			serverConnection.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void disconnect() {
		System.out.println("Closing connection with server");
		serverConnection.close();
	}

	public void sendCommand(Command action) {
		serverConnection.sendCommand(action);
	}

	@Override
	protected void connectionEnded(GameConnection connection) {
		serverConnection = null;
	}
}
