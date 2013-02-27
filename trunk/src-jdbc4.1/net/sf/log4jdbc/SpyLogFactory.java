/**
 * Copyright 2007-2012 Arthur Blake
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.log4jdbc;

import net.sf.log4jdbc.log4j2.Log4j2SpyLogDelegator;

/**
 * A provider for a SpyLogDelegator.  This allows a single switch point to abstract
 * away which logging system to use for spying on JDBC calls.
 * 
 * * The SLF4J logging facade is used, which is a very good general purpose facade for plugging into
 * numerous java logging systems, simply and easily.
 * <p>
 * Modifications for log4j2: 
 * <ul>
 * <li>addition of the <code>defineSpyLogDelegator()</code> method 
 * to choose between the standard <code>Slf4jSpyLogDelegator</code>, 
 * or the custom <code>Log4j2SpyLogDelegator</code>. 
 * This method use <code>DriverSpy#isUseLog4j2()</code> to determine which 
 * logger to return. 
 * <li>Use of this method to set the <code>logger</code> attribute.
 * </ul>
 * <p>
 * UPDATE: actually, all the previous modifications have been commented. 
 * This is because of a decision change, 
 * of not allowing to use the standard <code>Slf4jSpyLogDelegator</code>, 
 * otherwise the users will have to both install Log4j2 AND slf4j. 
 * They could just use the standard log4jdbc instead. 
 *
 * <p>
 * UPDATE 
 * <ul>
 * <li>Removal of commented and unused code</li>
 * <li>Selection of the <code>SpyLogDelegator</code> through <code>setSpyLogDelegator</code>. Default value is a <code>Log4j2SpyLogDelegator</code>
 * </ul>
 * <p> 
 *
 * @author Arthur Blake
 * @author Frederic Bastian
 * @author Tim Azzopardi from log4jdbc-remix
 * @author Mathieu Seppey
 */
public class SpyLogFactory
{
  /**
   * Do not allow instantiation.  Access is through static method.
   */
  private SpyLogFactory() {}

  /**
   * The logging system of choice.
   * Default value is Log4j2SpyLogDelegator
   */
  private static SpyLogDelegator logger ;
  
  /**
   * Return the appropriate <code>SpyLogDelegator</code> 
   * depending on the <code>Properties</code> <code>useLog4j2</code> attribute. 
   * If <code>useLog4j2</code> is <code>true</code>, return a <code>Log4j2SpyLogDelegator</code>, 
   * otherwise, return a <code>Slf4jSpyLogDelegator</code>. 
   * 
   * @return 	A <code>SpyLogDelegator</code>: a <code>Log4j2SpyLogDelegator</code> 
   * 			if the <code>useLog4j2</code> attribute of <code>DriverSpy</code> is <code>true</code>, 
   * 			a <code>Slf4jSpyLogDelegator</code> otherwise.
   * @see Slf4jSpyLogDelegator
   * @see net.sf.log4jdbc.log4j2.Log4j2SpyLogDelegator
   * @see DriverSpy#useLog4j2
   */  
  public static SpyLogDelegator getSpyLogDelegator()
  {  
    if(net.sf.log4jdbc.log4j2.Properties.isUseLog4j2()){
      return getLog4j2SpyLogDelegator();
    }
    else {
      return getSlf4jSpySpyLogDelegator();
    }
  }  
  
  /**
   * Get a new Log4j2SpyLogDelegator
   * @return Log4j2SpyLogDelegator
   */ 
  private static SpyLogDelegator getLog4j2SpyLogDelegator()
  {
	  return new Log4j2SpyLogDelegator();
  }
  
  /**
   * Get a new Slf4jSpyLogDelegator
   * @return Slf4jSpyLogDelegator
   */ 
  private static SpyLogDelegator getSlf4jSpySpyLogDelegator()
  {
    return new Slf4jSpyLogDelegator();
  }  
    
  /**
   * @param logDelegator the log delegator responsible for actually logging
   * JDBC events.
   */
  public static void setSpyLogDelegator(SpyLogDelegator logDelegator) {
    if (logDelegator == null) {
      throw new IllegalArgumentException("log4jdbc: logDelegator cannot be null.");
    }
    logger = logDelegator;
  }  
  
}

