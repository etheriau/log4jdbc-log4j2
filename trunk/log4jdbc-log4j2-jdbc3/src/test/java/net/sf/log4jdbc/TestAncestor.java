package net.sf.log4jdbc;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public abstract class TestAncestor 
{
    protected static final String testOutputFile = "test.out";
    
	/**
	 * Default Constructor. 
	 */
	public TestAncestor()
	{
		
	}
	/**
	 * A <code>TestWatcher</code> to log starting, succeeded and failed tests. 
	 */
	@Rule
	public TestWatcher watchman = new TestWatcher() {
	    @Override
	    protected void starting(Description description) {
	    	getLogger().info("Starting test: {}", description);
	    }
	    @Override
	    protected void failed(Throwable e, Description description) {
	    	if (getLogger().isErrorEnabled()) {
	    		getLogger().error("Test failed: " + description, e);
	    	}
	    }
	    @Override
	    protected void succeeded(Description description) {
	    	getLogger().info("Test succeeded: {}", description);
	    }
	};
	
	/**
	 * Return the logger of the class. 
	 * @return 	A <code>Logger</code>
	 */
	protected abstract Logger getLogger();
	

    /**
     * Delete the test output file at the end of all tests
     */
    @AfterClass
    public static void removeLogFile()
    {
        File test = new File(testOutputFile);
        test.delete();
    }
}
