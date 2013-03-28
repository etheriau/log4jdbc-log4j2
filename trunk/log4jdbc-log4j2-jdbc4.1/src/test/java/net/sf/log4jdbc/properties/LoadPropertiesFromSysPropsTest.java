package net.sf.log4jdbc.properties;

import static org.junit.Assert.assertEquals;

import net.sf.log4jdbc.Properties;
import net.sf.log4jdbc.TestAncestor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Class testing the loading of the {@link net.sf.log4jdbc.Properties Properties} 
 * from the System Properties. This has to be done in a separate class, 
 * as the properties are loaded only at class loading (static initializer), 
 * so only once for a given <code>ClassLoader</code>.
 * <p>
 * See {@link LoadPropertiesFromFileTest} for a class testing the loading of the properties 
 * from a property file.
 * 
 * @author Frederic Bastian
 * @version 1.1
 * @since 1.1
 */
public class LoadPropertiesFromSysPropsTest extends TestAncestor
{
	private final static Logger log = LogManager.getLogger(LoadPropertiesFromSysPropsTest.class.getName());
	/**
	 * Default constructor.
	 */
	public LoadPropertiesFromSysPropsTest()
	{
		super();
	}
	@Override
	protected Logger getLogger() {
		return log;
	}

	/**
	 * Try to load the properties from the System properties. 
	 */
	@Test
	public void shouldLoadPropertiesFromSysProps()
	{
		//set the property to test
		//(this is not the default value)
		System.setProperty("log4jdbc.spylogdelegator.name", 
				"net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator");
		//set the properties file to an non-existing file, 
		//so that System properties are used 
		System.setProperty("log4jdbc.log4j2.properties.file", "/none");
		
		//check if the properties correspond to values set
		assertEquals("Incorrect property SpyLogDelegatorName", 
				"net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator", 
				Properties.getSpyLogDelegatorName());
		
		//clear the System properties
		System.clearProperty("log4jdbc.log4j2.properties.file");
		System.clearProperty("log4jdbc.spylogdelegator.name");
	}
}
