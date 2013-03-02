/**
 * Copyright 2010 Tim Azzopardi
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
 *
 */
package net.sf.log4jdbc.sql.jdbcapi;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import net.sf.log4jdbc.log.SpyLogDelegator;
import net.sf.log4jdbc.log.SpyLogFactory;
import net.sf.log4jdbc.sql.Spy;


/**
 * A proxy datasource that can be used to wrap a real data source allowing log4jdbc to do its work on the real
 * data source.
 * Inspired by http://groups.google.com/group/log4jdbc/browse_thread/thread/0706611d1b85e210
 *
 * This can be useful in a Spring context. Imagine your spring context includes this datasource definition
 *
 * <code><pre>
 *  <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
 *      <property name="driverClass" value="${datasource.driverClassName}"/>
 *       <property name="jdbcUrl" value="${datasource.url}"/>
 *       <property name="user" value="${datasource.username}"/>
 *       <property name="password" value="${datasource.password}"/>
 *       <property name="initialPoolSize" value="${datasource.initialPoolSize}" />
 *       <property name="minPoolSize" value="${datasource.minPoolSize}" />
 *       <property name="maxPoolSize" value="${datasource.maxPoolSize}" />
 *       <property name="maxStatements" value="${datasource.maxStatements}" />
 *   </bean>
 * </pre></code>
 *
 * You can get log4jdbc to work on this using the following config changes
 *
 * <code><pre>
 *  <bean id="dataSourceSpied" class="com.mchange.v2.c3p0.ComboPooledDataSource">
 *      <property name="driverClass" value="${datasource.driverClassName}"/>
 *       <property name="jdbcUrl" value="${datasource.url}"/>
 *       <property name="user" value="${datasource.username}"/>
 *       <property name="password" value="${datasource.password}"/>
 *       <property name="initialPoolSize" value="${datasource.initialPoolSize}" />
 *       <property name="minPoolSize" value="${datasource.minPoolSize}" />
 *       <property name="maxPoolSize" value="${datasource.maxPoolSize}" />
 *       <property name="maxStatements" value="${datasource.maxStatements}" />
 *   </bean>
 *
 *   <bean id="dataSource" class="net.sf.log4jdbc.Log4jdbcProxyDataSource">
 *     <constructor-arg ref="dataSourceSpied" />
 *   </bean>
 * </pre></code>
 *
 *
 * @author tim.azzopardi Log4jdbcProxyDataSource.java
 * @author Mathieu Seppey renamed and modified
 *
 */
public class DataSourceSpy implements DataSource, Spy {
	private DataSource realDataSource;
	private static final SpyLogDelegator log = SpyLogFactory.getSpyLogDelegator();

	public DataSourceSpy(DataSource realDataSource)
	{
		this.realDataSource = realDataSource;
	}

	protected void reportException(String methodCall, SQLException exception)
	{
		log.exceptionOccured(this, methodCall, exception, null, -1L);
	}    

	private Object reportReturn(String methodCall, Object value)
	{
		log.methodReturned(this, methodCall, "");
		return value;
	}    

	@Override
	public Connection getConnection() throws SQLException
	{      
		String methodCall = "getConnection()";
		long tstart = System.currentTimeMillis();
		try
		{
			final Connection connection = realDataSource.getConnection();
			return (Connection) reportReturn(methodCall, 
					new ConnectionSpy(connection, DriverSpy.getRdbmsSpecifics(connection), 
							          System.currentTimeMillis() - tstart));  
		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}     
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException
	{

		String methodCall = "getConnection("+ username +", password***)";
		long tstart = System.currentTimeMillis();      
		try
		{
			final Connection connection = realDataSource.getConnection(username, password);
			return (Connection) reportReturn(methodCall, 
					new ConnectionSpy(connection,DriverSpy.getRdbmsSpecifics(connection), 
							          System.currentTimeMillis() - tstart));  
		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}     

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		String methodCall = "getLoginTimeout()";
		try
		{
			return (Integer) reportReturn(methodCall,realDataSource.getLoginTimeout());
		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		String methodCall = "getLogWriter()";
		try
		{
			return (PrintWriter) reportReturn(methodCall,realDataSource.getLogWriter());
		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		String methodCall = "isWrapperFor("+ iface +")";
		try
		{
			return (Boolean) reportReturn(methodCall,realDataSource.isWrapperFor(iface));
		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}      
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		String methodCall = "setLoginTimeout("+ seconds +")";
		try
		{
			realDataSource.setLoginTimeout(seconds);  
		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		String methodCall = "setLogWriter("+ out +")";
		try
		{
			realDataSource.setLogWriter(out);
		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}     
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		String methodCall = "unwrap("+ iface +")";
		try
		{
			return (T) reportReturn(methodCall, realDataSource.unwrap(iface));

		}
		catch (SQLException s)
		{
			reportException(methodCall,s);
			throw s;      
		}    
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException
	{
		String methodCall = "getParentLogger()";
		try
		{
			return (Logger) reportReturn(methodCall, realDataSource.getParentLogger());
		}
		catch (SQLFeatureNotSupportedException s)
		{
			reportException(methodCall,s);
			throw s;      
		}
	}

	@Override
	public String getClassType()
	{
		return "DataSource";
	}

	@Override
	public Integer getConnectionNumber()
	{
		// No connection number in this case
		return null;
	}

}
