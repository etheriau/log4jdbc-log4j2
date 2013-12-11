package net.sf.log4jdbc.log;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import net.sf.log4jdbc.TestAncestor;
import net.sf.log4jdbc.sql.jdbcapi.MockDriverUtils;

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
     * @throws SQLException 
     */
    @Test
    public void testEmptyResultSet() throws SQLException {
        MockDriverUtils mock = new MockDriverUtils();
        PreparedStatement mockPrep = mock(PreparedStatement.class);
        ResultSet mockResu = mock(ResultSet.class);
        String query = "SELECT * FROM Test";

        when(mock.getMockConnection().prepareStatement(query))
        .thenReturn(mockPrep);
        when(mockPrep.executeQuery()).thenReturn(mockResu);
        when(mockResu.getMetaData()).thenReturn(null);
        
        Connection conn = DriverManager.getConnection("jdbc:log4" + MockDriverUtils.MOCKURL);
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet resu = ps.executeQuery();
        
        when(mockResu.next()).thenReturn(true);
        resu.next();
        resu.getString(1);
        
        when(mockResu.next()).thenReturn(false);
        //here, the next should trigger the print
        resu.next();

        mock.deregister();
        removeLogFile();
    }
}
