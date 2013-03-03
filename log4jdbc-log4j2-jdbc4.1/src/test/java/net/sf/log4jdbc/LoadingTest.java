package net.sf.log4jdbc;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class testing the loading of log4jdbc-log4j2, 
 * through the use of a {@link net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy ConnectionSpy} 
 * or a {@link net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy DataSourceSpy}.
 * It also tests loading of properties by 
 * {@link net.sf.log4jdbc.Properties Properties}.
 * 
 * @author Frederic Bastian
 * @version 1.1
 * @since 1.1
 */
public class LoadingTest extends TestAncestor
{
	private final static Logger log = LogManager.getLogger(LoadingTest.class.getName());

	/**
	 * Default constructor.
	 */
	public LoadingTest()
	{
		super();
	}
	
	@Override
	protected Logger getLogger() {
		return log;
	}
	
	/**
	 * Init the properties before the tests, 
	 * so that the logs for initialization do not pollute the logs of the test.
	 */
	@BeforeClass
	public static void initProperties()
	{
		//this will trigger the static initializer
		Properties.getSpyLogDelegatorName();
		log.info("========Start testing=========");
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
	 * Try to load the properties from a easycache4jdbc properties file. 
	 */
	@Test
	public void shouldLoadPropertiesFromFile()
	{
		//set a system properties to provide the name of the properties file 
		//(default is log4jdbc.properties, but we want to use a test file)
		System.setProperty("log4jdbc.log4j2.properties.file", "/test.properties");
		//Properties are set in a static initializer, only called once by a same ClassLoader.
		//Need to reinit the properties for the test, as we don't know which test is run first. 
		Properties.init();
		
		//check if the properties correspond to values in the test file
		assertEquals("Incorrect property SpyLogDelegatorName", 
				"net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator", 
				Properties.getSpyLogDelegatorName());
		
		//clear the System properties
		System.clearProperty("log4jdbc.log4j2.properties.file");
	}
}
