package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import main.Command;
import main.Command.COMMAND;

public class ServerNetwork extends BaseNetwork {
	private ArrayList<GameConnection> connectionList;
	private Thread listeningThread;
	private int connectionCap;
	private int currentConnections;
	private boolean isListening;

	private ServerSocket listeningSocket;

	public ServerNetwork(int maxConnections) {
		this.connectionList = new ArrayList<GameConnection>();
		this.connectionCap = maxConnections;
		this.currentConnections = 0;
	}

	public void startListening() {
		if (isListening) {
			return;
		}
		this.listeningThread = new Thread(new Runnable() {
			@Override
			public void run() {
				listeningLoop();
			}
		});
		this.listeningThread.start();
	}

	public void stopListening() {
		if (isListening) {
			this.isListening = false;
			try {
				listeningSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void listeningLoop() {
		try {
			listeningSocket = new ServerSocket(4321);
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + LISTENING_PORT);
			System.exit(-1);
		}
		while (isListening) {
			Socket incoming = null;
			GameConnection connection = null;
			try {
				incoming = listeningSocket.accept();
				connection = new GameConnection(incoming, this);
				if (currentConnections < connectionCap) {
					connection.start();
					this.connectionList.add(connection);
					currentConnections++;
				} else {
					connection.sendCommand(new Command(COMMAND.SERVERFULL,
							Command.NO_OWNER));
					incoming.close();
				}

			} catch (IOException e) {
				System.out.println("Accept failed: " + LISTENING_PORT);
				isListening = false;
			}

		}
	}

	public void stopClient(int id) {
		System.out.println("Closing connection with client " + id);
		connectionList.get(id).close();
	}

	public void stopAllClients() {
		for (int i = 0; i < connectionList.size(); i++) {
			connectionList.get(i).close();
		}
	}

	@Override
	protected void connectionEnded(GameConnection connection) {
		this.connectionList.remove(connectionCap);
		this.currentConnections--;
	}

	public void sendCommandToClient(int id, Command action) {
		connectionList.get(id).sendCommand(action);
	}

	public void sendCommandToAllClient(Command action) {
		for (GameConnection coonection : connectionList) {
			coonection.sendCommand(action);
		}
	}
}
