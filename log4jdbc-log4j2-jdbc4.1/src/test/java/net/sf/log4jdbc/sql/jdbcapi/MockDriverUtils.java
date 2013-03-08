package net.sf.log4jdbc.sql.jdbcapi;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.mockito.Mockito.*;

/**
 * At instantiation, this class automatically registers a mock <code>Driver</code> 
 * to the <code>DriverManager</code>, that accepts the URL {@link #MOCKURL}.
 * When the method <code>connect(String, Properties)</code> is called 
 * on this mock <code>Driver</code> , it returns a mock <code>Connection</code>, 
 * that can be obtained by calling {@link #getMockConnection()} (even before 
 * <code>Driver.connect(String, Properties)</code> is called). 
 * Any call to <code>Driver.connect(String, Properties)</code> 
 * (whatever the value of the parameters)
 * will always return the same mock <code>Connection</code> instance.
 * <p>
 * During unit testing, developers should then simply obtain 
 * this mock <code>Connection</code>, and define some mock methods. 
 * <p>
 * A convenient mock <code>PreparedStatement</code> can be obtained by calling 
 * {@link #getMockPreparedStatement()}. This <code>PreparedStatement</code> is the one 
 * returned when calling <code>preparedStatement(String)</code> 
 * on the mock <code>Connection</code>.
 * <p>
 * When done using this <code>MockDriverUtils</code> object, 
 * a call to {@link #deregister()} must be made.
 * 
 * @author Frederic Bastian
 * @version Bgee 13, Mar 2013
 * @since Bgee 13
 */
public class MockDriverUtils 
{
	/**
	 * A <code>String</code> representing the URL that the mock <code>Driver</code> 
	 * will accept.
	 */
	public final String MOCKURL = "jdbc:mock:test";
	/**
	 * The mock <code>Driver</code> that this class is responsible to register 
	 * to the <code>DriverManager</code>.
	 */
	private Driver mockDriver;
	/**
	 * The mock <code>Connection</code> returned by the mock Driver registered 
	 * by this class. Any call to <code>Driver.connect(String, Properties)</code> 
	 * will always return this same mock <code>Connection</code> instance 
	 * (whatever the value of the parameters).
	 */
	private Connection mockConnection;
	
	/**
	 * Constructor that create a mock <code>Driver</code> and register it 
	 * to the <code>DriverManager</code>. By default, this mock <code>Driver</code> 
	 * returns a mock <code>Connection</code> 
	 * when <code>Driver.connect(String, Properties)</code> is called 
	 * (always the same <code>Connection</code> instance, whatever the calls 
	 * to <code>connect</code>).
	 * <p>
	 * When you don't need to use this <code>MockDriverUtils</code> anymore, 
	 * you must call {@link #deregister()}.
	 * <p>
	 * The mock <code>Driver</code> can be obtained through the use of 
	 * {@link #getMockDriver()}.
	 * The mock <code>Connection</code> can be obtained through the use of 
	 * {@link #getMockConnection()} (even before any call to <code>connect</code> is made).
	 */
	public MockDriverUtils() 
    {
		try {
			//create the mock Driver
			Driver mockDriver = mock(Driver.class);
			when(mockDriver.acceptsURL(MOCKURL)).thenReturn(true);

			//will return a mock Connection, that unit tests will use.
			//all calls to the connect method will return the same mock Connection instance.
			this.setMockConnection(mock(Connection.class));
			when(mockDriver.connect(MOCKURL, any(Properties.class)))
			.thenReturn(this.getMockConnection());

			//register the mock Driver
			DriverManager.registerDriver(mockDriver);
			this.setMockDriver(mockDriver);
		} catch (SQLException e) {
			//do nothing. The only method that could throw an actual exception 
			//is DriverManager.registerDriver, and in that case, the Driver 
			//will not be available for the application anyway
		}
    }
	
	/**
	 * Deregister this mock <code>Driver</code> from the <code>DriverManager</code>.
	 */
	public void deregister() 
	{
    	try {
			DriverManager.deregisterDriver(this.getMockDriver());
		} catch (SQLException e) {
			//I don't think we should care about this during unit testing. 
		}
	}

	/**
	 * Return the mock <code>Connection</code> provided by the mock <code>Driver</code>.
	 * Any call to <code>Driver.connect(String, Properties)</code> 
     * (whatever the value of the parameters)
     * will always return the same mock <code>Connection</code> instance, 
     * that can be obtain by this getter (even before 
     * <code>Driver.connect(String, Properties)</code> is called).
     * 
	 * @return the {@link #mockConnection}
	 */
	public Connection getMockConnection() {
		return this.mockConnection;
	}
	/**
	 * @param mockConnection A <code>Connection</code> to set {@link #mockConnection} 
	 */
	private void setMockConnection(Connection mockConnection) {
		this.mockConnection = mockConnection;
	}

	/**
	 * @return the {@link #mockDriver}
	 */
	public Driver getMockDriver() {
		return mockDriver;
	}
	/**
	 * @param mockDriver A <code>Driver</code> to set {@link #mockDriver} 
	 */
	private void setMockDriver(Driver mockDriver) {
		this.mockDriver = mockDriver;
	}
}
