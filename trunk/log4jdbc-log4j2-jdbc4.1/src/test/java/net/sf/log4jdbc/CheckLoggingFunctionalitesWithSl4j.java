package net.sf.log4jdbc;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Class which run all tests provided by <code>CheckLoggingFunctionalites</code>
 * with <code>Slf4jSpyLogDelegator</code> instead of <code>Log4j2SpyLogDelegator</code>
 * 
 * @author Mathieu Seppey
 * @version 1.0
 */
public class CheckLoggingFunctionalitesWithSl4j extends TestAncestor
{

    private final static Logger log = LogManager.getLogger(CheckLoggingFunctionalites.class.getName());

    /**
     * @param args
     * @throws IOException 
     */
    @Test
    public void main() throws IOException
    {

        try{
            Class.forName("org.slf4j.Logger");
            System.setProperty("log4jdbc.spylogdelegator.name",Slf4jSpyLogDelegator.class.getName());
            JUnitCore junit = new JUnitCore();
            Result result = junit.run(CheckLoggingFunctionalites.class);
            assertTrue(result.getFailureCount() + " test(s) failed",result.wasSuccessful());

        } catch (ClassNotFoundException e) {
            // Means that slf4j is not available for tests. Allow the test to pass anyway.
            assertTrue("",true);
        }

    }

    @Override
    protected Logger getLogger()
    {
        return log;
    }


}



