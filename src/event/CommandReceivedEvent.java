package event;
import java.util.EventObject;

public class CommandReceivedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public CommandReceivedEvent() {
		super(null);
	}
}