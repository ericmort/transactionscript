package transactionscript;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.sql.SQLException;
import com.zaxxer.hikari.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.ListIterator;
import java.sql.ResultSet;

public class TransactionScriptTest extends TestCase
{
    public TransactionScriptTest( String testName ) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( TransactionScriptTest.class );
    }


    public void testAddStatements() {
        HikariScriptConnection hsc = new HikariScriptConnection();
        TransactionScript ts = new TransactionScript(new Engine(hsc));

        ts.addStatement("test1", "create table a (x integer, y varchar(100), z float)");
        ts.addStatement("test2", "insert into a  values (1, 'lkjl', 2.3)");
        ts.addStatement("test3", "insert into a  values (2, 'lkj', ?)", 3.3);
        ts.addStatement("test4", "insert into a (x,y,z) values (?,?,?)", 3, "a string", 4.3);
        ts.addStatement("test5", "update a set y = ?, z = z + ? where x = 3",  "updated", 5);

        assertEquals(ts.steps.size(), 5);
        assertEquals(ts.steps.get(0).action, "create table a (x integer, y varchar(100), z float)");
        assertEquals(ts.steps.get(1).action, "insert into a  values (1, 'lkjl', 2.3)");
        assertEquals(ts.steps.get(2).action, "insert into a  values (2, 'lkj', ?)");
        assertEquals(ts.steps.get(3).action, "insert into a (x,y,z) values (?,?,?)");
        assertEquals(ts.steps.get(4).action, "update a set y = ?, z = z + ? where x = 3");

        assertEquals(ts.steps.get(0).arguments.length, 0);
        assertEquals(ts.steps.get(1).arguments.length, 0);
        assertEquals(ts.steps.get(2).arguments.length, 1);
        assertEquals(ts.steps.get(3).arguments.length, 3);
        assertEquals(ts.steps.get(4).arguments.length, 2);

        assertEquals(ts.steps.get(2).arguments[0], 3.3);

        assertEquals(ts.steps.get(3).arguments[0], 3);
        assertEquals(ts.steps.get(3).arguments[1], "a string");
        assertEquals(ts.steps.get(3).arguments[2], 4.3);

        assertEquals(ts.steps.get(4).arguments[0], "updated");
        assertEquals(ts.steps.get(4).arguments[1], 5);
        
        
        try {
            ts.execute();
            assertEquals(ts.steps.get(0).rowsAffected, 0);
            assertEquals(ts.steps.get(1).rowsAffected, 1);
            assertEquals(ts.steps.get(2).rowsAffected, 1);
            assertEquals(ts.steps.get(3).rowsAffected, 1);
            assertEquals(ts.steps.get(4).rowsAffected, 1);

            Connection conn = hsc.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("select x,y,z from a order by x");
            
            rs.next();
            assertEquals(1, rs.getInt(1));            
            assertEquals(2.3, rs.getDouble(3));            
            
            rs.next();
            assertEquals(2, rs.getInt(1));            
            assertEquals(3.3, rs.getDouble(3));            

            rs.next();
            assertEquals(3, rs.getInt(1));            
            assertEquals(9.3, rs.getDouble(3));            
            

        } catch(Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }

    }

    public void testError() {
        // throws exception
        // rolls back transaction
    }

    private class HikariScriptConnection implements ScriptConnection {
        HikariConfig config;
        HikariDataSource dataSource;

        public Connection getConnection() throws SQLException {
            return this.dataSource.getConnection();
        }
        
        public HikariScriptConnection() {
            this.dataSource = new HikariDataSource(hikariConfig());
        }

        private HikariConfig hikariConfig() {
            config = new HikariConfig();
            config.setJdbcUrl("jdbc:hsqldb:file:testdb");
            
            config.setUsername("sa");
            config.setPassword("");
            return config;
        }
    }
}

