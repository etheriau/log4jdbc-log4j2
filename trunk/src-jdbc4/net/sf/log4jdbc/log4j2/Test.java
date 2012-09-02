package net.sf.log4jdbc.log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import net.sf.log4jdbc.ConnectionSpy;
import net.sf.log4jdbc.SpyLogDelegator;
import net.sf.log4jdbc.SpyLogFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.SimpleMessage;


public class Test 
{
	  static final SpyLogDelegator log = SpyLogFactory.getSpyLogDelegator();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException 
	{
		Test fdfds = new Test();
		fdfds.execute();
		
		
	}

	private void execute() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Class.forName("net.sf.log4jdbc.DriverSpy").newInstance();
		SpyLogDelegator test = SpyLogFactory.getSpyLogDelegator();
		
		log.debug("... fdsfs...");
		Logger logger = LogManager.getLogger("log4jdbc.log4j2");
		logger.error(MarkerManager.getMarker("TEST"), new SimpleMessage("test1"), new Exception());
		logger.error(MarkerManager.getMarker("TEST"), "test2", new Exception());
		logger.error(new SimpleMessage("test3"), new Exception());
		logger.error(MarkerManager.getMarker("TEST"), new SimpleMessage("test4"));
		logger.debug(MarkerManager.getMarker("TEST"), new SimpleMessage("test5"), new Exception());
		logger.info(MarkerManager.getMarker("TEST"), new SimpleMessage("test6"), new Exception());
		logger.fatal(MarkerManager.getMarker("TEST"), new SimpleMessage("test7"), new Exception());
		//string modified for log4jdbc (see DriverSpy below)
		String connectionString = "jdbc:log4jdbc:mysql://127.0.0.1:3306/test?user=root";
		System.out.println("getclass");
		//DriverSpy allows to log all sql access
	    try {
			Class.forName("net.sf.log4jdbc.DriverSpy").newInstance();System.out.println("done, connect");
	    Connection conn = DriverManager.getConnection(connectionString);System.out.println("done");
	    PreparedStatement preparedStatement = conn.prepareStatement("show tables");
	    ResultSet resultSet = preparedStatement.executeQuery();
	    resultSet.next();
	    conn.close();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}
