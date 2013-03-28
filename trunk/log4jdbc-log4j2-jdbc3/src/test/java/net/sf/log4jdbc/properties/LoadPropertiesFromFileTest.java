package net.sf.log4jdbc.properties;

import static org.junit.Assert.assertEquals;

import net.sf.log4jdbc.Properties;
import net.sf.log4jdbc.TestAncestor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Class testing the loading of the {@link net.sf.log4jdbc.Properties Properties} 
 * from a property file. This has to be done in a separate class, 
 * as the properties are loaded only at class loading (static initializer), 
 * so only once for a given <code>ClassLoader</code>.
 * <p>
 * See {@link LoadPropertiesFromSysPropsTest} for a class testing the loading of the properties 
 * from the System properties.
 * 
 * @author Frederic Bastian
 * @version 1.1
 * @since 1.1
 */
public class LoadPropertiesFromFileTest extends TestAncestor
{
	private final static Logger log = LogManager.getLogger(LoadPropertiesFromFileTest.class.getName());
	/**
	 * Default constructor.
	 */
	public LoadPropertiesFromFileTest()
	{
		super();
	}
	@Override
	protected Logger getLogger() {
		return log;
	}

	/**
	 * Try to load the properties from a properties file. 
	 */
	@Test
	public void shouldLoadPropertiesFromFile()
	{
		//set a system properties to provide the name of the properties file 
		//(default is log4jdbc.properties, but we want to use a test file)
		System.setProperty("log4jdbc.log4j2.properties.file", "/test.properties");
		
		//check if the properties correspond to values in the test file
		//(this is not the default value)
		assertEquals("Incorrect property SpyLogDelegatorName", 
				"net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator", 
				Properties.getSpyLogDelegatorName());
		
		//clear the System properties
		System.clearProperty("log4jdbc.log4j2.properties.file");
	}
}
