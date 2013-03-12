package net.sf.log4jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.log4jdbc.log.SpyLogDelegator;
import net.sf.log4jdbc.log.SpyLogFactory;
import net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy;
import net.sf.log4jdbc.sql.resultsetcollector.ResultSetCollector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class which tests all the logging actions of log4jdbc-log4j2 SpyDelegator implementation
 * The logger output is written in the file /test.out where it is checked
 * 
 * TODO ? : pour l'instant les tests sont fonctionells avec Log4j2SpyDelegator uniquement, et non Slf4j
 * 
 * @author Mathieu Seppey
 * @version 1.0
 */
public class LoggingTest extends TestAncestor
{

  private final static Logger log = LogManager.getLogger(LoggingTest.class.getName());

  private static SpyLogDelegator TestSpyLogDelegator ;

  /**
   * Default constructor.
   */
  public LoggingTest()
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

    //set a system properties to provide the name of the properties file 
    //(default is log4jdbc.properties, but we want to use a test file)
    System.setProperty("log4jdbc.log4j2.properties.file", "/test.properties");
    //Properties are set in a static initializer, only called once by a same ClassLoader.
    Properties.init();

    //this will trigger the static initializer
    Properties.getSpyLogDelegatorName();

    log.info("========Start testing=========");    
    TestSpyLogDelegator = SpyLogFactory.getSpyLogDelegator();

  }
  /**
   * Reinit the properties after the tests, 
   * so that the properties are reset to be used for other test classes.
   */
  @AfterClass
  public static void reinitProperties()
  {
    log.info("========End testing=========");
    Properties.init();
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
   * Clear the test output file before each single test
   */
  @Before
  public void emptyLogFile() throws IOException{
    FileOutputStream logCleaner = new FileOutputStream("test.out");
    logCleaner.write(1);
    logCleaner.close();
  }    

  /**
   * Unit test for the method isJdbcLoggingEnabled
   */
  @Test
  public void checkIsJdbcLoggingEnabled()
  {
    assertTrue("isJdbcLoggingEnabled has to return true", TestSpyLogDelegator.isJdbcLoggingEnabled());
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
    ConnectionSpy cs = mock(ConnectionSpy.class);
    when(cs.getClassType()).thenReturn("net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy");

    // Run the method
    TestSpyLogDelegator.exceptionOccured(cs, "test()",e,"SELECT * FROM Test", 1L);

    // Check the result

    // log4j2
    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator"){
      assertEquals("Incorrect output written by exceptionOccured", 
          "0. SELECT * FROM Test  {FAILED after 1 ms}java.lang.Exception: test",LoggingTest.readLine(2).concat(LoggingTest.readLine(3)));
    }
    else{
      assertEquals("Incorrect output written by exceptionOccured", 
          "0. net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy.test() SELECT * FROM Test  ",LoggingTest.readLine(2).concat(LoggingTest.readLine(3)));      
    }

  }

  /**
   * Unit test for the method methodReturned
   * 
   * @throws IOException    
   */
  @Test
  public void checkMethodReturned() throws IOException
  {   
    // Creation of fake parameters
    ConnectionSpy cs = mock(ConnectionSpy.class);
    when(cs.getClassType()).thenReturn("net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy");

    // Run the method
    TestSpyLogDelegator.methodReturned(cs, "test()", "TestMessage");

    // Check the result

    // Note, slf4j and log4j2 don't use the level in this case... => change it ?

    // log4j2
    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator"){
      assertTrue("The logging level used by methodReturned is not INFO as expected",LoggingTest.readLine(1).contains(" INFO "));
      assertEquals("Incorrect output written by methodReturned", 
          "0. net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy.test() returned TestMessage",LoggingTest.readLine(2));
    }  
    else {
      // slf4j
      assertTrue("The logging level used by methodReturned is not DEBUG as expected",LoggingTest.readLine(1).contains(" DEBUG "));
      assertEquals("Incorrect output written by methodReturned", 
          "0. net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy.test() returned TestMessage  sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2) ",LoggingTest.readLine(2));
    }  


  } 

  /**
   * Unit test for the method sqlTimingOccurred
   * 
   * @throws IOException    
   */
  @Test
  public void checkSqlTimingOccurred() throws IOException
  {

    // Creation of fake parameters
    ConnectionSpy cs = mock(ConnectionSpy.class);
    when(cs.getClassType()).thenReturn("net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy");

    // Run the method
    TestSpyLogDelegator.sqlTimingOccurred(cs,1000L,"test", "SELECT * FROM test");

    // Check the result
    // Note, slf4j and log4j2 don't use the level in this case... => change it ?

    // log4j2

    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator"){
      assertTrue("The logging level used by sqlTimingOccurred is not INFO as expected",LoggingTest.readLine(1).contains(" INFO "));
      assertEquals("Incorrect output written by methodReturned", 
          "0. SELECT * FROM test  {executed in 1000 ms}",LoggingTest.readLine(2));
    }  
    else {
      // slf4j
      assertTrue("The logging level used by sqlTimingOccurred is not DEBUG as expected",LoggingTest.readLine(1).contains(" DEBUG "));
      assertEquals("Incorrect output written by methodReturned", 
          "0. SELECT * FROM test ",LoggingTest.readLine(3));    
      assertEquals("Incorrect output written by methodReturned", 
          " {executed in 1000 msec} ",LoggingTest.readLine(4));   
    }
  }   

  /**
   * Unit test for the method connectionOpened
   * 
   * @throws IOException    
   */
  @Test
  public void checkConnectionOpened() throws IOException
  {

    // Creation of fake parameters
    ConnectionSpy cs = mock(ConnectionSpy.class);
    when(cs.getClassType()).thenReturn("net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy");

    // Run the method
    TestSpyLogDelegator.connectionOpened(cs,1000L);

    // Check the result
    assertTrue("The logging level used by connectionOpened is not INFO as expected",LoggingTest.readLine(1).contains(" INFO "));

    // log4j2
    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator")
      assertEquals("Incorrect output written by connectionOpenend", 
          "0. Connection opened. {executed in 1000ms} ",LoggingTest.readLine(2));
    else 
      // slf4j
      assertEquals("Incorrect output written by connectionOpened", 
          "0. Connection opened  sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2) ",LoggingTest.readLine(2));


  } 

  /**
   * Unit test for the method connectionClosed
   * 
   * @throws IOException    
   */
  @Test
  public void checkConnectionClosed() throws IOException
  {

    // Creation of fake parameters
    ConnectionSpy cs = mock(ConnectionSpy.class);
    when(cs.getClassType()).thenReturn("net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy");

    // Run the method
    TestSpyLogDelegator.connectionClosed(cs,1000L);

    // Check the result
    assertTrue("The logging level used by connectionClosed is not INFO as expected",LoggingTest.readLine(1).contains(" INFO "));

    // log4j2
    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator")
      assertEquals("Incorrect output written by connectionClosed", 
          "0. Connection closed. {executed in 1000ms} ",LoggingTest.readLine(2));
    else 
      // slf4j
      assertEquals("Incorrect output written by connectionClosed", 
          "0. Connection closed  sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2) ",LoggingTest.readLine(2));


  } 

  /**
   * Unit test for the method connectionAborted
   * 
   * @throws IOException    
   */
  @Test
  public void checkConnectionAborted() throws IOException
  {

    // Creation of fake parameters
    ConnectionSpy cs = mock(ConnectionSpy.class);
    when(cs.getClassType()).thenReturn("net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy");

    // Run the method
    TestSpyLogDelegator.connectionAborted(cs,1000L);

    // Check the result
    assertTrue("The logging level used by connectionAborted is not INFO as expected",LoggingTest.readLine(1).contains(" INFO "));

    // log4j2
    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator")
      assertEquals("Incorrect output written by connectionAborted", 
          "0. Connection aborted. {executed in 1000ms} ",LoggingTest.readLine(2));
    else 
      // slf4j
      assertEquals("Incorrect output written by connectionAborted", 
          "0. Connection aborted  sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-2) ",LoggingTest.readLine(2));

  } 

  /**
   * Unit test for the method isJdbcLoggingEnabled
   */
  @Test
  public void checkIsResultSetCollectionEnabled()
  {
    assertTrue("isResultSetCollectionEnabled has to return true", TestSpyLogDelegator.isJdbcLoggingEnabled());
  }

  /**
   * Unit test for the method isResultSetCollectionEnabledWithUnreadValueFillIn
   */
  @Test
  public void checkIsResultSetCollectionEnabledWithUnreadValueFillIn()
  {
    assertTrue("isResultSetCollectionEnabledWithUnreadValueFillIn has to return true", TestSpyLogDelegator.isResultSetCollectionEnabledWithUnreadValueFillIn());
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

    // Run the method
    TestSpyLogDelegator.resultSetCollected(rsc);

    // Check the result

    // log4j2
    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator"){
      assertTrue("The logging level used by resultSetCollected is not INFO as expected",LoggingTest.readLine(1).contains(" INFO "));
      assertTrue("Incorrect 1st output line written by resultSetCollected",LoggingTest.readLine(1).contains("|---------|---------|"));
      assertTrue("Incorrect 2nd output line written by resultSetCollected",LoggingTest.readLine(3).contains("|Colonne1 |Colonne2 |"));    
      assertTrue("Incorrect 3rd output line written by resultSetCollected",LoggingTest.readLine(5).contains("|---------|---------|"));    
      assertTrue("Incorrect 4th output line written by resultSetCollected",LoggingTest.readLine(7).contains("|a        |b        |"));    
      assertTrue("Incorrect 5th output line written by resultSetCollected",LoggingTest.readLine(9).contains("|c        |d        |"));    
      assertTrue("Incorrect 6th output line written by resultSetCollected",LoggingTest.readLine(11).contains("|---------|---------|"));       
    }
    else {
      assertTrue("The logging level used by resultSetCollected is not INFO as expected",LoggingTest.readLine(1).contains(" INFO "));
      assertTrue("Incorrect 1st output line written by resultSetCollected",LoggingTest.readLine(2).contains("|---------|---------|"));
      assertTrue("Incorrect 2nd output line written by resultSetCollected",LoggingTest.readLine(5).contains("|Colonne1 |Colonne2 |"));    
      assertTrue("Incorrect 3rd output line written by resultSetCollected",LoggingTest.readLine(8).contains("|---------|---------|"));    
      assertTrue("Incorrect 4th output line written by resultSetCollected",LoggingTest.readLine(11).contains("|a        |b        |"));    
      assertTrue("Incorrect 5th output line written by resultSetCollected",LoggingTest.readLine(14).contains("|c        |d        |"));    
      assertTrue("Incorrect 6th output line written by resultSetCollected",LoggingTest.readLine(17).contains("|---------|---------|"));  
    }

  }  

  /**
   * Unit test for the method debug
   * 
   * @throws IOException 
   *  
   */
  @Test
  public void checkDebug() throws IOException {
    // Run the method
    TestSpyLogDelegator.debug("DEBUGMESSAGE");
    // Check the result
    assertTrue("The logging level used by debug is not DEBUG as expected",LoggingTest.readLine(1).contains(" DEBUG "));

    // log4j2
    if(TestSpyLogDelegator.getClass().getName() == "net.sf.log4jdbc.log.log4j2.Log4j2SpyLogDelegator")
      assertTrue("Incorrect output line written by debug",LoggingTest.readLine(1).contains("DEBUGMESSAGE"));

    else 
      // slf4j
      assertTrue("Incorrect output line written by debug",LoggingTest.readLine(2).contains("DEBUGMESSAGE"));



  }

  /**
   * read and return the required line from the file test.out
   * 
   * @param int lineNumber 
   */
  private static String readLine(int lineNumber) throws IOException
  {
    BufferedReader br = new BufferedReader(new FileReader("test.out"));
    String ret = "" ;
    for(int i=1;i<=lineNumber;i++)
      ret = br.readLine();
    br.close();

    return ret;
  }
}
