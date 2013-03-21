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


package net.sf.log4jdbc.sql.resultsetcollector;

import java.util.ArrayList;
import java.util.List;

/***
 * @author Tim Azzopardi
 * @author Mathieu Seppey 
 * 
 * Update : changed printResultSet into getResultSetToPrint
 * 
 */

public class ResultSetCollectorPrinter {

    /**
     * A list which contains a String for each line to print 
     */
    private List<String> result ;

    /**
     * A <code>StringBuffer</code> used to build a single line
     */
    private StringBuffer sb = new StringBuffer();

    /**
     * Default constructor
     */
    public ResultSetCollectorPrinter() {

    }

    /***
     * Generate and return all lines to be printed by a logger,
     * based on the content of the provided resultSetCollector
     * 
     * @param resultSetCollector the ResultSetCollector which has collected the data we want to print
     * @return A <code>List</code> which contains a <code>String</code> for each line to print 
     */
    public List<String> getResultSetToPrint(ResultSetCollector resultSetCollector) {

        this.result = new ArrayList<String>();

        int columnCount = resultSetCollector.getColumnCount();
        int maxLength[] = new int[columnCount];

        for (int column = 1; column <= columnCount; column++) {
            maxLength[column - 1] = resultSetCollector.getColumnName(column)
                    .length();
        }
        if (resultSetCollector.getRows() != null) {
            for (List<Object> printRow : resultSetCollector.getRows()) {
                int colIndex = 0;
                for (Object v : printRow) {
                    if (v != null) {
                        int length = v.toString().length();
                        if (length > maxLength[colIndex]) {
                            maxLength[colIndex] = length;
                        }
                    }
                    colIndex++;
                }
            }
        }
        for (int column = 1; column <= columnCount; column++) {
            maxLength[column - 1] = maxLength[column - 1] + 1;
        }

        sb.append("|");
        for (int column = 1; column <= columnCount; column++) {
            sb.append(padRight("-", maxLength[column - 1]).replaceAll(" ", "-")
                    + "|");
        }
        this.result.add(sb.toString());
        sb.setLength(0);
        sb.append("|");
        for (int column = 1; column <= columnCount; column++) {
            sb.append(padRight(resultSetCollector.getColumnName(column),
                    maxLength[column - 1])
                    + "|");
        }
        this.result.add(sb.toString());
        sb.setLength(0);
        sb.append("|");
        for (int column = 1; column <= columnCount; column++) {
            sb.append(padRight("-", maxLength[column - 1]).replaceAll(" ", "-")
                    + "|");
        }
        this.result.add(sb.toString());
        sb.setLength(0);
        if (resultSetCollector.getRows() != null) {
            for (List<Object> printRow : resultSetCollector.getRows()) {
                int colIndex = 0;
                sb.append("|");
                for (Object v : printRow) {
                    sb.append(padRight(v == null ? "null" : v.toString(),
                            maxLength[colIndex])
                            + "|");
                    colIndex++;
                }
                this.result.add(sb.toString());
                sb.setLength(0);
            }
        }
        sb.append("|");
        for (int column = 1; column <= columnCount; column++) {
            sb.append(padRight("-", maxLength[column - 1]).replaceAll(" ", "-")
                    + "|");
        }

        this.result.add(sb.toString());
        sb.setLength(0);

        resultSetCollector.reset();
        
        return this.result ;

    }

    /***
     * Add space to the provided <code>String</code> to match the provided width
     * @param s the <code>String</code> we want to adjust
     * @param n the width of the returned <code>String</code>
     * @return a <code>String</code> matching the provided width
     */
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

}
