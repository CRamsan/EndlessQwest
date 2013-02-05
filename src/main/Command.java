package main;

public class Command {

	public enum COMMAND {
		NEWUSER, EXIT, UP, DOWN, LEFT, RIGHT, INTERACT, SERVERFULL
	};

	public static final int NO_OWNER = -1;

	private COMMAND action;
	private int owner;

	public Command(COMMAND action, int owner) {
		this.action = action;
		this.owner = owner;
	}

	public Command(String message) {
		this.action = COMMAND.values()[Integer.parseInt(message.split(";")[0])];
		this.owner = Integer.parseInt(message.split(";")[1]);
	}

	public COMMAND getAction() {
		return action;
	}

	public void setAction(COMMAND action) {
		this.action = action;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(byte owner) {
		this.owner = owner;
	}

	public String toMessage() {
		return action.ordinal() + ";" + owner;
	}
}
