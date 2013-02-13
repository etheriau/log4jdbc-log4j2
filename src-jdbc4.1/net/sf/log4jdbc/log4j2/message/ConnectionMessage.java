package net.sf.log4jdbc.log4j2.message;

import net.sf.log4jdbc.ConnectionSpy;
import net.sf.log4jdbc.Spy;

import org.apache.logging.log4j.message.Message;

/**
 * <code>SqlMessage</code> related to connection events.
 * 
 * @author Frederic Bastian
 * @see net.sf.log4jdbc.log4j2.Log4j2SpyLogDelegator#connectionOpened(Spy, long)
 * @see net.sf.log4jdbc.log4j2.Log4j2SpyLogDelegator#connectionClosed(Spy, long)
 * @version 1.0
 * @since 1.0
 */
public class ConnectionMessage extends SqlMessage implements Message 
{
	private static final long serialVersionUID = 6278727380958233518L;
	/**
	 * An <code>int</code> to define the value of <code>operation</code> 
	 * when the action on the connection was to open it.
	 * @see operation
	 */
	public static final int OPENING = 1;
	/**
	 * An <code>int</code> to define the value of <code>operation</code> 
	 * when the action on the connection was to close it.
	 * @see operation
	 */
	public static final int CLOSING = 2;
	
	/**
     * <code>ConnectionSpy</code> that was opened or closed. 
     * Will be used to build the <code>message</code>, only when needed.
     * @see #message
     * @see #buildMessage()
     */
	private Spy spy;
	/**
     * an <code>int</code> to define if the operation was to open, or to close connection. 
	 * Should be equals to <code>OPENING</code> if the operation was to open the connection, 
	 * to <code>CLOSING</code> if the operation was to close the connection. 
     * Will be used to build the <code>message</code>, only when needed.
     * @see #OPENING
     * @see #CLOSING
     * @see #message
     * @see #buildMessage()
     */
	private int operation;
	/**
     * A <code>long</code> defining the time elapsed to open or close the connection in ms. 
     * Will be used to build the <code>message</code>, only when needed.
     * @see #message
     * @see #buildMessage()
     */
	private long execTime;
	
	/**
	 * Default constructor
	 */
	public ConnectionMessage()
	{
		this(null, -1L, 0, false);
	}
	
	/**
	 * 
	 * @param spy 			<code>ConnectionSpy</code> that was opened or closed.
	 * @param execTime 		A <code>long</code> defining the time elapsed to open or close the connection in ms
	   * 					Caller should pass -1 if not used
	 * @param operation 	an <code>int</code> to define if the operation was to open, or to close connection. 
	 * 						Should be equals to <code>OPENING</code> if the operation was to open the connection, 
	 * 						to <code>CLOSING</code> if the operation was to close the connection.
     * @param isDebugEnabled A <code>boolean</code> to define whether debugInfo should be displayed.
	 */
	public ConnectionMessage(Spy spy, long execTime, int operation, boolean isDebugEnabled)
	{
		super(isDebugEnabled);
		
		this.spy = spy;
		this.execTime = execTime;
		if (operation == OPENING || operation == CLOSING) {
			this.operation = operation;
		} else {
			this.operation = 0;
		}
	}

	@Override
	protected void buildMessage() 
	{
		StringBuffer buildMsg = new StringBuffer();
		
		if (this.isDebugEnabled()) {
			buildMsg.append(SqlMessage.getDebugInfo());
			buildMsg.append(SqlMessage.nl);
		}
		
		buildMsg.append(spy.getConnectionNumber()).append(". Connection ");
		if (this.operation == OPENING) {
			buildMsg.append("opened.");
		} else if (this.operation == CLOSING) {
			buildMsg.append("closed.");
		} else {
			buildMsg.append("opened or closed.");
		}
		if (this.execTime != -1) {
			buildMsg.append(" {executed in ").append(this.execTime).append("ms} ");
		}
		if (this.isDebugEnabled()) {
			buildMsg.append(SqlMessage.nl);
			buildMsg.append(ConnectionSpy.getOpenConnectionsDump());
		}
		
		this.setMessage(buildMsg.toString());
	}
}
