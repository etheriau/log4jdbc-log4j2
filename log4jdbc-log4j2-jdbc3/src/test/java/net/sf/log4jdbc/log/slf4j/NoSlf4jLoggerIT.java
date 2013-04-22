package net.sf.log4jdbc.log.slf4j;

import static org.junit.Assert.assertEquals;

import net.sf.log4jdbc.log.SpyLogFactory;

import org.junit.Test;

/**
 * Class which tests that the absence of any logging system throws the right error
 * 
 * @author Mathieu Seppey
 * @version 1.0
 * @see net.sf.log4jdbc.log.SpyLogFactory
 */
public class NoSlf4jLoggerIT
{

    /**
     * A test which expects a <code>NoClassDefFoundError</code> to be thrown
     * and check its message content
     */
    @Test(expected=NoClassDefFoundError.class)
    public void test()
    {
    	System.setProperty("log4jdbc.spylogdelegator.name", 
        		Slf4jSpyLogDelegator.class.getName());
    	
    	try{
            // try to get a logger through the creation of a SpyLogDelegator
            SpyLogFactory.getSpyLogDelegator();
        } catch (NoClassDefFoundError e){
            // check the content of the error message
            assertEquals("Wrong error message attached to the NoClassDefFoundError",
                    e.getMessage(), "Cannot find a library corresponding to the property log4jdbc.spylogdelegator.name. " +
                		"Please provide a logging library and configure a valid spyLogDelegator name in the properties file.");
            // re throw it to pass the test
            throw(e);
        }
    }

}
