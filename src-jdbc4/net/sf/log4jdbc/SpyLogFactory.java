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
 * The SLF4J logging facade is used, which is a very good general purpose facade for plugging into
 * numerous java logging systems, simply and easily.
 * <p>
 * Modifications for log4j2: 
 * <ul>
 * <li>addition of the <code>defineSpyLogDelegator()</code> method 
 * to choose between the standard <code>Slf4jSpyLogDelegator</code>, 
 * or the custom <code>Log4j2SpyLogDelegator</code>.
 * <li>Use of this method to set the <code>logger</code> attribute.
 * </ul>
 *
 * @author Arthur Blake
 * @author Frederic Bastian
 */
public class SpyLogFactory
{
  /**
   * Do not allow instantiation.  Access is through static method.
   */
  private SpyLogFactory() {}

  /**
   * The logging system of choice.
   */
  private static final SpyLogDelegator logger = defineSpyLogDelegator();
  //new Log4jSpyLogDelegator();

  /**
   * Get the default SpyLogDelegator for logging to the logger.
   *
   * @return the default SpyLogDelegator for logging to the logger.
   */
  public static SpyLogDelegator getSpyLogDelegator()
  {
    return logger;
  }
  
  /**
   * Return the appropriate <code>SpyLogDelegator</code> 
   * depending on the <code>DriverSpy</code> <code>useLog4j2</code> attribute. 
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
  private static SpyLogDelegator defineSpyLogDelegator() 
  {
	if (DriverSpy.isUseLog4j2()) {
		return getLog4j2SpyLogDelegator();
	}
	return getSlf4jSpyLogDelegator();
  }
  
  public static SpyLogDelegator getLog4j2SpyLogDelegator()
  {
	  return new Log4j2SpyLogDelegator();
  }
  
  public static SpyLogDelegator getSlf4jSpyLogDelegator()
  {
	  return new Slf4jSpyLogDelegator();
  }
}

