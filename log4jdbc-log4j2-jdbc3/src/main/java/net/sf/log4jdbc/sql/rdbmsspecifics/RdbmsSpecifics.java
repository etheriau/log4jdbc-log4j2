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
package net.sf.log4jdbc.sql.rdbmsspecifics;

import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Encapsulate sql formatting details about a particular relational database management system so that
 * accurate, useable SQL can be composed for that RDMBS.
 * 
 * Modification for log4jdbc-log4j2: {@code Properties.isDumpBooleanAsTrueFalse()} is not used 
 * anymore, because the way a boolean value should be returned is DBMS specific, and as such, 
 * should be handled by different extensions of RdbmsSpecifics. This allows to remove 
 * the dependency to the {@code Properties} class. 
 *
 * @author Arthur Blake
 * @author Frederic Bastian
 */
public class RdbmsSpecifics
{
	/**
	 * Default constructor.
	 */
	public RdbmsSpecifics()
	{
	}

	protected static final String dateFormat = "MM/dd/yyyy HH:mm:ss.SSS";

	/**
	 * Format an Object that is being bound to a PreparedStatement parameter, for display. The goal is to reformat the
	 * object in a format that can be re-run against the native SQL client of the particular Rdbms being used.  This
	 * class should be extended to provide formatting instances that format objects correctly for different RDBMS
	 * types.
	 *
	 * @param object jdbc object to be formatted.
	 * @return formatted dump of the object.
	 */
	public String formatParameterObject(Object object)
	{
		if (object == null)
		{
			return "NULL";
		}

		if (object instanceof String)
		{
			return "'" + escapeString((String)object) + "'";
		}
		else if (object instanceof Date)
		{
			return "'" + new SimpleDateFormat(dateFormat).format(object) + "'";
		}
		else if (object instanceof Boolean)
		{
			//NOTE: as of log4jdbc-log4j2 1.17-SNAPSHOT, RdbmsSpecifics should not 
		    //use the Properties class anymore (because the Properties mechanism 
		    //is being updated). 
		    //The way a boolean value should be returned is DBMS specific, and as such, 
		    //should be handled by different extensions of RdbmsSpecifics 
		    //(as with, for instance, OracleRdbmsSpecifics, etc)
//		    return Properties.isDumpBooleanAsTrueFalse()?
//					((Boolean)object).booleanValue()?"true":"false"
//						:((Boolean)object).booleanValue()?"1":"0";
		    
		    //Default value of Properties.isDumpBooleanAsTrueFalse() was false
		    return ((Boolean)object).booleanValue() ? "1" : "0";
		}
		else
		{
			return object.toString();
		}
	}

	/**
	 * Make sure string is escaped properly so that it will run in a SQL query analyzer tool.
	 * At this time all we do is double any single tick marks.
	 * Do not call this with a null string or else an exception will occur.
	 *
	 * @return the input String, escaped.
	 */
	String escapeString(String in)
	{
		StringBuilder out = new StringBuilder();
		for (int i=0, j=in.length(); i < j; i++)
		{
			char c = in.charAt(i); 
			if (c == '\'')
			{
				out.append(c);
			}
			out.append(c);
		}
		return out.toString();
	}

}
