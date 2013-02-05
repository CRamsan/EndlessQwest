package network;

import javax.swing.event.EventListenerList;

import event.CommandReceivedEvent;
import event.IEventReceivedListener;
import event.IEventSource;

public abstract class BaseNetwork implements IEventSource {
	public static final short LISTENING_PORT = 4321;

	private EventListenerList listenerList = new EventListenerList();

	@Override
	public void addCommandReceivedEventListener(IEventReceivedListener listener) {
		listenerList.add(IEventReceivedListener.class, listener);
	}

	@Override
	public void removeCommandReceivedEventListener(
			IEventReceivedListener listener) {
		listenerList.remove(IEventReceivedListener.class, listener);
	}

	@Override
	public void firecommandReceivedEvent(CommandReceivedEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == IEventReceivedListener.class) {
				((IEventReceivedListener) listeners[i + 1])
						.commandReceivedEventOccurred(evt);
			}
		}
	}

	protected abstract void connectionEnded(GameConnection connection);
}
