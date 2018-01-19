package transactionscript;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.sql.SQLException;
import com.zaxxer.hikari.*;
import java.sql.DriverManager;
import java.util.ListIterator;

public class TransactionScriptTest extends TestCase
{
    public TransactionScriptTest( String testName ) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( TransactionScriptTest.class );
    }

    private HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:hsqldb:file:testdb");
        //config.setJdbcUrl("jdbc:hsqldb:hsql://localhost/testdb");
        
        config.setUsername("sa");
        config.setPassword("");
        return config;
    }

    public void testAddStatements() {
        HikariConfig config = hikariConfig();
        TransactionScript ts = new TransactionScript(config);

        ts.addStatement("test1", "create table a (x integer, y varchar(100), z float)");
        ts.addStatement("test2", "insert into a  values (1, 'lkjl', 2.3)");
        ts.addStatement("test3", "insert into a  values (2, 'lkj', ?)", 2.3);
        ts.addStatement("test4", "insert into a (x,y,z) values (?,?,?)", 1, "a string", 2.3);
        
        assertEquals(ts.steps.size(), 4);
        assertEquals(ts.steps.get(0).action, "create table a (x integer, y varchar(100), z float)");
        assertEquals(ts.steps.get(1).action, "insert into a  values (1, 'lkjl', 2.3)");
        assertEquals(ts.steps.get(2).action, "insert into a  values (2, 'lkj', ?)");
        assertEquals(ts.steps.get(3).action, "insert into a (x,y,z) values (?,?,?)");

        assertEquals(ts.steps.get(0).arguments.length, 0);
        assertEquals(ts.steps.get(1).arguments.length, 0);
        assertEquals(ts.steps.get(2).arguments.length, 1);
        assertEquals(ts.steps.get(3).arguments.length, 3);

        assertEquals(ts.steps.get(2).arguments[0], 2.3);

        assertEquals(ts.steps.get(3).arguments[0], 1);
        assertEquals(ts.steps.get(3).arguments[1], "a string");
        assertEquals(ts.steps.get(3).arguments[2], 2.3);

        
        // test updates as well
        // test deletes     
        
        try {
            ts.execute();
            assertEquals(ts.steps.get(0).rowsAffected, 0);
            assertEquals(ts.steps.get(1).rowsAffected, 1);
            assertEquals(ts.steps.get(2).rowsAffected, 1);
            assertEquals(ts.steps.get(3).rowsAffected, 1);

            // select * from a; should return three rows

        } catch(Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }

    }

    public void testError() {
        // throws exception
        // rolls back transaction
    }
}

