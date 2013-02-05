package event;

import java.util.EventListener;

public interface IEventReceivedListener extends EventListener {
	public void commandReceivedEventOccurred(CommandReceivedEvent evt);
}