package net.sf.log4jdbc;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.log4jdbc.log.SpyLogDelegator;
import net.sf.log4jdbc.log.SpyLogFactory;
import net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator;
import net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import net.sf.log4jdbc.sql.jdbcapi.MockDriverUtils;
import net.sf.log4jdbc.sql.jdbcapi.PreparedStatementSpy;
import net.sf.log4jdbc.sql.jdbcapi.ResultSetSpy;
import net.sf.log4jdbc.sql.resultsetcollector.ResultSetCollector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class which tests all the logging actions of log4jdbc-log4j2 SpyDelegator implementation
 * The logger output is written in the file /test.out where it is checked
 * 
 * @author Mathieu Seppey
 * @version 1.0
 */
public class CheckLoggingFunctionalites extends TestAncestor
{

    private final static Logger log = LogManager.getLogger(CheckLoggingFunctionalites.class.getName());

    private static SpyLogDelegator TestSpyLogDelegator ;

    /**
     * Default constructor.
     */
    public CheckLoggingFunctionalites()
    {
        super();
    }

    @Override
    protected Logger getLogger()
    {
        return log;
    }

    /**
     * Init the properties before the tests, 
     */
    @BeforeClass
    public static void initProperties()
    {

        Properties.getSpyLogDelegatorName();

        TestSpyLogDelegator = SpyLogFactory.getSpyLogDelegator();

        log.info("========Start testing with " + TestSpyLogDelegator.getClass().getName() + " =========");    


    }
    /**
     * Reinit the properties after the tests, 
     * so that the properties are reset to be used for other test classes.
     */
    @AfterClass
    public static void reinitProperties()
    {
        log.info("========End testing=========");
        System.clearProperty("log4jdbc.log4j2.properties.file");
    }

    /**
     * Delete the test output file at the end of all tests
     */
    @AfterClass
    public static void removeLogFile()
    {
        File test = new File("test.out");
        test.delete();
    }  

    /**
     * A test which goes through the whole process => Connection, statement, ResultSet
     * @throws SQLException 
     * @throws IOException 
     */
    @Test
    public void testWithAConnection() throws SQLException, IOException
    {
        // Create all fake mock objects 
        MockDriverUtils mock = new MockDriverUtils();
        PreparedStatement mockPrep = mock(PreparedStatement.class);
        ResultSet mockResu = mock(ResultSet.class);
        ResultSetMetaData mockRsmd = mock(ResultSetMetaData.class);

        when(mock.getMockConnection().prepareStatement("SELECT * FROM Test"))
            .thenReturn(mockPrep);
        when(mockPrep.executeQuery()).thenReturn(mockResu);
        when(mockResu.getMetaData()).thenReturn(mockRsmd);
        when(mockRsmd.getColumnCount()).thenReturn(1);
        when(mockRsmd.getColumnName(1)).thenReturn("column 1");
        when(mockRsmd.getColumnLabel(1)).thenReturn("column 1 renamed");
        when(mockResu.next()).thenReturn(true);
        when(mockResu.getString(1)).thenReturn("Ok");
        
        // Instantiation and use of the most important classes with a log check.

        String outputValue = "";
        emptyLogFile();

        Connection conn = DriverManager.getConnection("jdbc:log4" + MockDriverUtils.MOCKURL);
        outputValue = CheckLoggingFunctionalites.readLogFile(-1);
        assertTrue("The log produced by the instanciation of a ConnectionSpy is not as expected",
        		outputValue.contains("Connection opened") 
                && outputValue.contains("Connection.new Connection returned"));
        emptyLogFile();
        //verify that the underlying connection has been opened
        verify(mock.getMockDriver()).connect(eq(MockDriverUtils.MOCKURL), 
        		any(java.util.Properties.class));

        
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Test");
        outputValue = CheckLoggingFunctionalites.readLogFile(-1);
        assertTrue("The log produced by the instanciation of a PreparedStatementSpy is not as expected",
        		outputValue.contains("PreparedStatement.new PreparedStatement returned"));
        emptyLogFile();
        //verify the the underlying connection returned a prepared statement
        verify(mock.getMockConnection()).prepareStatement(eq("SELECT * FROM Test"));

        ResultSet resu = ps.executeQuery();
        outputValue = CheckLoggingFunctionalites.readLogFile(-1);
        assertTrue("The log produced by PreparedStatement executeQuery() is not as expected",
        		outputValue.contains("ResultSet.new ResultSet returned") 
                && outputValue.contains("PreparedStatement.executeQuery() returned"));
        //verify that the underlying prepared statement has been called
        verify(mockPrep).executeQuery();

        resu.next();
        assertTrue("Wrong result returned by the getString() method of ResultSetSpy",
        		resu.getString(1) == "Ok");
        //verify that the underlying resultset has been called
        verify(mockResu).next();

        ps.close();
        verify(mockPrep).close();
        conn.close();
        verify(mock.getMockConnection()).close();

        // clean all mock objects
        mock.deregister();
    }

    /**
     * A test which goes through the whole process => DataSource, connection, statement, ResultSet
     * @throws Exception 
     */
    @Test
    public void testWithADataSource() throws Exception
    {

        // Create all fake mock objects and returns
        MockDriverUtils mock = new MockDriverUtils();

        Driver mockDriv = mock.getMockDriver();
        Connection mockConn = mockDriv.connect(MockDriverUtils.MOCKURL, null);
        DataSource mockData = mock(DataSource.class);
        PreparedStatement mockPrep = mock(PreparedStatement.class);
        ResultSet mockResu = mock(ResultSet.class);
        ResultSetMetaData mockRsmd = mock(ResultSetMetaData.class);
        ResultSetCollector mockRsc = mock(ResultSetCollector.class);

        doReturn(mockConn).when(mockData).getConnection();
        when(mockConn.prepareStatement("SELECT * FROM Test")).thenReturn(mockPrep);
        when(mockPrep.executeQuery()).thenReturn(mockResu);
        when(mockResu.getMetaData()).thenReturn(mockRsmd);
        when(mockRsmd.getColumnCount()).thenReturn(1);
        when(mockResu.next()).thenReturn(true);
        when(mockResu.getString(1)).thenReturn("Ok");

        // Instantiation and use of the most important classes with a log check.
        
        String outputValue = "";
        emptyLogFile();

        DataSourceSpy dss = new DataSourceSpy(mockData);
        Connection conn = dss.getConnection();
        outputValue = CheckLoggingFunctionalites.readLogFile(-1);
        assertTrue("The log produced when the DataSourceSpy returns a connection is not as expected",
        		outputValue.contains("Connection opened") 
                && outputValue.contains("Connection.new Connection returned") && 
                outputValue.contains("DataSource.getConnection() returned"));
        emptyLogFile();

        ConnectionSpy cs = new ConnectionSpy(conn);
        outputValue = CheckLoggingFunctionalites.readLogFile(-1);
        assertTrue("The log produced by the instanciation of a ConnectionSpy is not as expected",
        		outputValue.contains("Connection opened") 
                && outputValue.contains("Connection.new Connection returned"));
        emptyLogFile();

        PreparedStatementSpy ps = new PreparedStatementSpy("SELECT * FROM Test",cs,mockPrep);
        outputValue = CheckLoggingFunctionalites.readLogFile(-1);
        assertTrue("The log produced by the instanciation of a PreparedStatementSpy is not as expected",
        		outputValue.contains("PreparedStatement.new PreparedStatement returned"));
        emptyLogFile();

        ResultSetSpy resu = (ResultSetSpy) ps.executeQuery();
        outputValue = CheckLoggingFunctionalites.readLogFile(-1);
        assertTrue("The log produced by PreparedStatement executeQuery() is not as expected",
        		outputValue.contains("ResultSet.new ResultSet returned") 
                && outputValue.contains("PreparedStatement.executeQuery() returned"));

        //resu.setResultSetCollector(mockRsc);
        resu.next();
        assertTrue("Wrong result returned by the getString() method of ResultSetSpy",
        		resu.getString(1) == "Ok");

        ps.close();
        cs.close();

        // clean all mock objects
        mock.deregister();

    }      

    /**
     * Unit test for the method isJdbcLoggingEnabled
     */
    @Test
    public void checkIsJdbcLoggingEnabled()
    {
        assertTrue("isJdbcLoggingEnabled has to return true", 
        		TestSpyLogDelegator.isJdbcLoggingEnabled());
    }

    /**
     * Unit test for the method exceptionOccured
     * 
     * @throws IOException    
     */
    @Test
    public void checkExceptionOccured() throws IOException
    {   

        // Creation of fake parameters
        Exception e = new Exception("test");
        // Create a fake connection using Mockito
        ConnectionSpy cs = new ConnectionSpy(new MockDriverUtils().getMockConnection());

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.exceptionOccured(cs, "test()",e,"SELECT * FROM Test", 1000L);

        // Check the result

        assertTrue("The logging level used by exceptionOccured is not ERRROR as expected",
        		CheckLoggingFunctionalites.readLogFile(1).contains(" ERROR "));

        // Read the whole output file
        String outputValue = CheckLoggingFunctionalites.readLogFile(-1);

        assertTrue("Incorrect output written by exceptionOccured", outputValue.contains("SELECT * FROM Test")
                && outputValue.contains("FAILED after 1000"));

    }

    /**
     * Unit test for the method methodReturned
     * 
     * @throws IOException    
     */
    @Test
    public void checkMethodReturned() throws IOException
    {   
        // Create a fake connection using Mockito
        ConnectionSpy cs = new ConnectionSpy(new MockDriverUtils().getMockConnection());

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.methodReturned(cs, "test()", "TestMessage");

        // Check the result

        // Note, slf4j and log4j2 don't log this method with same the level. 
        if(TestSpyLogDelegator.getClass().getName() == Log4j2SpyLogDelegator.class.getName())
            // log4j2
            assertTrue("The logging level used by methodReturned is not INFO as expected",
            		CheckLoggingFunctionalites.readLogFile(1).contains(" INFO "));
        else
            // slf4j
            assertTrue("The logging level used by methodReturned is not DEBUG as expected",
            		CheckLoggingFunctionalites.readLogFile(1).contains(" DEBUG "));

        // Read the whole output file
        String outputValue = CheckLoggingFunctionalites.readLogFile(-1);

        assertTrue("Incorrect output written by methodReturned",
        		outputValue.contains("Connection.test() returned TestMessage"));

    } 

    /**
     * Unit test for the method sqlTimingOccurred
     * 
     * @throws IOException    
     */
    @Test
    public void checkSqlTimingOccurred() throws IOException
    {

        // Create a fake connection using Mockito
        ConnectionSpy cs = new ConnectionSpy(new MockDriverUtils().getMockConnection());

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.sqlTimingOccurred(cs,1000L,"test", "SELECT * FROM Test");

        // Check the result

        // Note, slf4j and log4j2 don't log this method with same the level. 
        if(TestSpyLogDelegator.getClass().getName() == Log4j2SpyLogDelegator.class.getName())
            // log4j2
            assertTrue("The logging level used by sqlTimingOccurred is not INFO as expected",
            		CheckLoggingFunctionalites.readLogFile(1).contains(" INFO "));
        else
            // slf4j
            assertTrue("The logging level used by sqlTimingOccurred is not DEBUG as expected",
            		CheckLoggingFunctionalites.readLogFile(1).contains(" DEBUG "));

        // Read the whole output file
        String outputValue = CheckLoggingFunctionalites.readLogFile(-1);

        assertTrue("Incorrect output written by sqlTimingOccurred", 
        		outputValue.contains("SELECT * FROM Test") 
                && outputValue.contains("{executed in 1000"));


    }   

    /**
     * Unit test for the method connectionOpened
     * 
     * @throws IOException    
     */
    @Test
    public void checkConnectionOpened() throws IOException
    {

        // Create a fake connection using Mockito
        ConnectionSpy cs = new ConnectionSpy(new MockDriverUtils().getMockConnection());

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.connectionOpened(cs,1000L);

        // Check the result
        assertTrue("The logging level used by connectionOpened is not INFO as expected",
        		CheckLoggingFunctionalites.readLogFile(1).contains(" INFO "));
        assertTrue("Incorrect output written by connectionOpenend",
        		CheckLoggingFunctionalites.readLogFile(-1).contains("Connection opened"));

    } 

    /**
     * Unit test for the method connectionClosed
     * 
     * @throws IOException    
     */
    @Test
    public void checkConnectionClosed() throws IOException
    {

        // Create a fake connection using Mockito
        ConnectionSpy cs = new ConnectionSpy(new MockDriverUtils().getMockConnection());

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.connectionClosed(cs,1000L);

        // Check the result
        assertTrue("The logging level used by connectionClosed is not INFO as expected",
        		CheckLoggingFunctionalites.readLogFile(1).contains(" INFO "));
        assertTrue("Incorrect output written by connectionClosed",
        		CheckLoggingFunctionalites.readLogFile(-1).contains("Connection closed"));

    } 

    /**
     * Unit test for the method connectionAborted
     * 
     * @throws IOException    
     */
    @Test
    public void checkConnectionAborted() throws IOException
    {


        // Create a fake connection using Mockito
        ConnectionSpy cs = new ConnectionSpy(new MockDriverUtils().getMockConnection());

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.connectionAborted(cs,1000L);

        // Check the result
        assertTrue("The logging level used by connectionAborted is not INFO as expected",
        		CheckLoggingFunctionalites.readLogFile(1).contains(" INFO "));
        assertTrue("Incorrect output written by connectionAborted",
        		CheckLoggingFunctionalites.readLogFile(-1).contains("Connection aborted"));

    } 

    /**
     * Unit test for the method isJdbcLoggingEnabled
     */
    @Test
    public void checkIsResultSetCollectionEnabled()
    {
        assertTrue("isResultSetCollectionEnabled has to return true", 
        		TestSpyLogDelegator.isJdbcLoggingEnabled());
    }

    /**
     * Unit test for the method isResultSetCollectionEnabledWithUnreadValueFillIn
     */
    @Test
    public void checkIsResultSetCollectionEnabledWithUnreadValueFillIn()
    {
        assertTrue("isResultSetCollectionEnabledWithUnreadValueFillIn has to return true", 
        		TestSpyLogDelegator.isResultSetCollectionEnabledWithUnreadValueFillIn());
    } 

    /**
     * Unit test for the method resultSetCollected
     * 
     * @throws IOException    
     */
    @Test
    public void checkResultSetCollected() throws IOException
    {

        // Creation of fake parameters
        ResultSetCollector rsc = mock(ResultSetCollector.class);
        List<Object> row1 = new ArrayList<Object>();
        List<Object> row2 = new ArrayList<Object>(); 
        List<List<Object>> rows = new ArrayList<List<Object>>();
        row1.add("a");
        row1.add("b");
        row2.add("c");
        row2.add("d");
        rows.add(row1);
        rows.add(row2);
        when(rsc.getRows()).thenReturn(rows);
        when(rsc.getColumnCount()).thenReturn(2);
        when(rsc.getColumnName(1)).thenReturn("Colonne1");
        when(rsc.getColumnName(2)).thenReturn("Colonne2");

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.resultSetCollected(rsc);

        // Check the result
        assertTrue("The logging level used by resultSetCollected is not INFO as expected",
        		CheckLoggingFunctionalites.readLogFile(1).contains(" INFO "));

        // Read the whole output file
        String outputValue = CheckLoggingFunctionalites.readLogFile(-1);

        assertTrue("Incorrect column header written by resultSetCollected",
        		outputValue.contains("|Colonne1 |Colonne2 |"));    
        assertTrue("Incorrect 1st line written by resultSetCollected",
        		outputValue.contains("|a        |b        |"));    
        assertTrue("Incorrect 2nd line written by resultSetCollected",
        		outputValue.contains("|c        |d        |"));    

    }  

    /**
     * Unit test for the method debug
     * 
     * @throws IOException 
     *  
     */
    @Test
    public void checkDebug() throws IOException {

        // Run the method after ensuring the log file is empty 
        emptyLogFile();
        TestSpyLogDelegator.debug("DEBUGMESSAGE");

        // Check the result
        assertTrue("The logging level used by debug is not DEBUG as expected",
        		CheckLoggingFunctionalites.readLogFile(1).contains(" DEBUG "));
        assertTrue("Incorrect output line written by debug",
        		CheckLoggingFunctionalites.readLogFile(-1).contains("DEBUGMESSAGE"));


    }

    /**
     * Read and return the required line from the file test.out
     * 
     * @param int lineNumber : The number of the required line. -1 return the whole file
     */
    private static String readLogFile(int lineNumber) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("test.out"));
        String ret = "" ;

        if(lineNumber > 0){
            for(int i=1;i<=lineNumber;i++)
                ret = br.readLine();
        }
        else
            try{
                for(int i=1;i<=1000;i++)
                    ret = ret.concat(br.readLine()); 
            } catch(NullPointerException npe){
                // Means that the end of file is reached, do nothing
            }

        br.close();

        return ret;
    }

    /**
     * Clear the test output file
     */
    private void emptyLogFile() throws IOException{
        FileOutputStream logCleaner = new FileOutputStream("test.out");
        logCleaner.write(1);
        logCleaner.close();
    }    

}
