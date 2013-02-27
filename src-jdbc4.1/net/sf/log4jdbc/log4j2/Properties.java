package net.sf.log4jdbc.log4j2;

import java.io.IOException;
import java.io.InputStream;

import net.sf.log4jdbc.SpyLogDelegator;
import net.sf.log4jdbc.SpyLogFactory;

/**
 * This class loads the properties for <code>log4jdbc-log4j2</code>. 
 * They are tried to be read first from a property file in the classpath 
 * (called "log4jdbc.log4j2.properties"), then from the <code>System</code> properties.
 * <p>
 * This class has been copied from <code>net.sf.log4jdbc.DriverSpy</code> 
 * developed by Arthur Blake.
 * 
 * @author Mathieu Seppey
 * @author Frederic Bastian
 * @author Arthur Blake
 * @version 0.1
 * @since 0.1
 */
public final class Properties 
{
  private static SpyLogDelegator log ;

  /**
   * A <code>boolean</code> added for this modified version, to define which logging system should be used: 
   * if <code>true</code>, log4j2 is used, 
   * if <code>false</code>, the standard logger is used, slf4. 
   * Default is <code>true</code> (use log4j2). This attribute is used by the <code>SpyLogFactory</code> 
   * to determine which <code>SpyLogDelegator</code> to return 
   * (either <code>Log4j2SpyLogDelegator</code>, or <code>Slf4jSpyLogDelegator</code>)
   * 
   * @see SpyLogFactory
   */
  static boolean useLog4j2;	  

  /**
   * Static initializer. 
   * 
   * Try first to load the properties from a easycache4jdbc properties file, 
   * then try to load them from the system properties. 
   */
  static 
  {
    init();
  }

  public static void init()
  {

    java.util.Properties props = new java.util.Properties(System.getProperties());
    //try to get the properties file.
    //default name is log4jdbc.log4j2.properties
    //check first if an alternative name has been provided in the System properties
    String propertyFile = getStringOption(props, "log4jdbc.log4j2.properties.file");
    //otherwise, set default
    if (propertyFile == null) {
      propertyFile = "/log4jdbc.log4j2.properties";
    }     

    InputStream propStream =
        Properties.class.getResourceAsStream(propertyFile);
    if (propStream != null) {
      try {
        props.load(propStream);
      } 
      catch (IOException e) {
          e.printStackTrace();
      }
      finally {
        try {
          propStream.close();
        } catch (IOException e) {
          e.printStackTrace();        }
      }
    }

    useLog4j2 = getBooleanOption(props,"log4jdbc.log4j2", true);
    log = SpyLogFactory.getSpyLogDelegator();
    
  }

  /**
   * Retrieve a property from <code>props</code> corresponding to <code>key</code>. 
   * Return <code>null</code> if the property is not defined or empty.
   *
   * @param props     <code>java.sql.Properties</code> to get the property from.
   * @param key     property key.
   *
   * @return      A <code>String</code> corresponding to the value
   *          for that property key. 
   *          Or <code>null</code> if not defined or empty.
   */
  private static String getStringOption(java.util.Properties props, String key)
  {

    String propValue = props.getProperty(key);
    if (propValue == null || propValue.length()==0) {
      propValue = null; // force to null, even if empty String
    } 
    return propValue;
  }    

  /**
   * Retrieve a property from <code>props</code> corresponding to <code>key</code>. 
   * Return <code>null</code> if the property is not defined or empty.
   *
   * @param props     <code>java.sql.Properties</code> to get the property from.
   * @param key     property key.
   *
   * @return      A <code>Boolean</code> corresponding to the value
   *          for that property key. 
   *          Or the default value if not defined or empty.
   */
  private static boolean getBooleanOption(java.util.Properties props, String key, boolean defaultValue)
  {

    String propValue = props.getProperty(key);
    boolean val;
    if (propValue == null)
    {
      return defaultValue;
    }
    else
    {
      propValue = propValue.trim().toLowerCase();
      if (propValue.length() == 0)
      {
        val = defaultValue;
      }
      else
      {
        val= "true".equals(propValue) ||
          "yes".equals(propValue) || "on".equals(propValue);
      }
    }
    return val;
  }   
 
  /**
   * Return the defined SpyLogDelegator
   * 
   * @return the SpyLogDelegator
   * @see #SpyLogDelegator
   */
  public static SpyLogDelegator getSpyLogDelegator()
  {
    return log;
  }

  /**
   * @return the useLog4j2
   * @see #useLog4j2
   */
  public static boolean isUseLog4j2() {
    return useLog4j2;
  }    

}
