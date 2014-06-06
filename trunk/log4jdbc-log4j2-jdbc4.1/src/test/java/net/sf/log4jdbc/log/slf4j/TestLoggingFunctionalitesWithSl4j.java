package net.sf.log4jdbc.log.slf4j;

import net.sf.log4jdbc.log.CheckLoggingFunctionalites;
import net.sf.log4jdbc.log.SpyLogFactory;
import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Class which run all tests provided by <code>CheckLoggingFunctionalites</code>
 * with <code>Slf4jSpyLogDelegator</code> instead of <code>Log4j2SpyLogDelegator</code>
 * 
 * @author Mathieu Seppey
 * @version 1.0
 */
public class TestLoggingFunctionalitesWithSl4j extends CheckLoggingFunctionalites
{

    private final static Logger log = LogManager.getLogger(TestLoggingFunctionalitesWithSl4j.class.getName());

   /**
    * Default constructor. 
    */
    public TestLoggingFunctionalitesWithSl4j()
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
        CheckLoggingFunctionalites.initProperties();
        System.setProperty("log4jdbc.spylogdelegator.name", 
        		Slf4jSpyLogDelegator.class.getName());
        TestSpyLogDelegator = SpyLogFactory.getSpyLogDelegator(); 
    }
    /**
     * Reinit the properties after the tests, 
     * so that the properties are reset to be used for other test classes.
     */
    @AfterClass
    public static void reinitProperties()
    {
        CheckLoggingFunctionalites.reinitProperties();
        System.clearProperty("log4jdbc.spylogdelegator.name");
    }

}



