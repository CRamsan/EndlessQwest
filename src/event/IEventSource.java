package event;


public interface IEventSource {
	public void addCommandReceivedEventListener(IEventReceivedListener listener);

	public void removeCommandReceivedEventListener(
			IEventReceivedListener listener);

	public void firecommandReceivedEvent(CommandReceivedEvent evt);
}