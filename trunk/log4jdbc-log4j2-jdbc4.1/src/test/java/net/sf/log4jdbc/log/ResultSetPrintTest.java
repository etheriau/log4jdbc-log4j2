package net.sf.log4jdbc.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

import net.sf.log4jdbc.TestAncestor;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;

public class ResultSetPrintTest extends TestAncestor {
    private final static Logger log = LogManager.getLogger(ResultSetPrintTest.class.getName());
    @Override
    protected Logger getLogger() {
        return log;
    }

    
    /**
     * Regression test for 
     * <a href='http://code.google.com/p/log4jdbc-log4j2/issues/detail?id=9'>
     * issue #9</a>.
     * @throws ClassNotFoundException 
     * @throws SQLException 
     */
    @Test
    public void testEmptyResultSet() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        JdbcDataSource realSource = new JdbcDataSource();
        realSource.setURL("jdbc:h2:mem:db1");
        Connection realCon = realSource.getConnection();
        Statement stmt = realCon.createStatement();
        stmt.executeUpdate("create table test (key varchar(10))");
        
        
        DataSource source = new DataSourceSpy(realSource);
        Connection con = source.getConnection();
        stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from test");
        rs.next();
        rs.close();
        
        removeLogFile();
    }
}
