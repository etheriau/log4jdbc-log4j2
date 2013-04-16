package net.sf.log4jdbc.log;

import org.junit.Test;

/**
 * Class which tests that the absence of any logging system throws the right error
 * 
 * @author Mathieu Seppey
 * @version 1.0
 * @see net.sf.log4jdbc.log.SpyLogFactory
 */
public class TestErrorIfNoLogger
{

    /**
     * A test which expects a <code>NoClassDefFoundError</code> to be thrown
     */
    @Test(expected=NoClassDefFoundError.class)
    public void test()
    {
        SpyLogFactory.getSpyLogDelegator();
    }

}
