package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import main.Command;
import event.CommandReceivedEvent;
import event.IEventSource;

public class GameConnection extends Thread {

	private Socket socket;
	private int id;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private IEventSource taskSource;
	private boolean stop = false;
	private Command action;
	private BaseNetwork network;

	public GameConnection(Socket socket, BaseNetwork network) {
		super();
		this.socket = socket;
		try {
			out = new PrintWriter(this.socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
			this.network = network;
		} catch (IOException e) {
			System.err.println("Error establishing in/out streams");
		}
	}

	public void run() {
		try {
			System.out.println("Thread for client " + id + " started");
			String inputLine;
			while ((inputLine = in.readLine()) != null && !stop) {

				System.out.println("Message recieved " + inputLine);
				taskSource.firecommandReceivedEvent(new CommandReceivedEvent());
			}
			System.out.println("Thread for client stopped correctly");
		} catch (IOException e) {
			System.err.println("Thread for client stopped with an exception");
		}
		network.connectionEnded(this);
	}

	public void sendCommand(Object object) {
		out.println(object.toString());
	}

	public Command getCommand() {
		Command tmp = action;
		action = null;
		return tmp;
	}

	public void close() {
		try {
			stop = true;
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}