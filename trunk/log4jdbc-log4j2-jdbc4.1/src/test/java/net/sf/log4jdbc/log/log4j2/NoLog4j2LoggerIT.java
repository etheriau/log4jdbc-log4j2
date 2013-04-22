package net.sf.log4jdbc.log.log4j2;

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
public class NoLog4j2LoggerIT
{

    /**
     * A test which expects a <code>NoClassDefFoundError</code> to be thrown
     * and check its message content
     */
    @Test(expected=NoClassDefFoundError.class)
    public void test()
    {
        try{
            // try to get a logger through the creation of a SpyLogDelegator
            SpyLogFactory.getSpyLogDelegator();
        } catch (NoClassDefFoundError e){
            // check the content of the error message
            assertEquals("Wrong error message attached to the NoClassDefFoundError",
                    e.getMessage(), "Unable to find Log4j2 as default logging library. " +
                    "Please provide a logging library and configure a valid spyLogDelegator name in the properties file.");
            // re throw it to pass the test
            throw(e);
        }
    }

}
