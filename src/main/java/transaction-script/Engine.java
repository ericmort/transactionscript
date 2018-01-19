package transactionscript;

import com.zaxxer.hikari.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Engine  {
    HikariDataSource dataSource;
    HikariConfig config;
    Connection currentConn;

    public Engine(HikariConfig config) {
        this.config = config;
        this.dataSource =  new HikariDataSource(config);
    }

    public Engine() {}

    public void begin() throws SQLException {
        this.currentConn = this.dataSource.getConnection();
        //this.currentConn.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        System.out.println("Engine.commit()");
        this.currentConn.commit();
    }

    public void rollback() throws SQLException  {
        this.currentConn.rollback();
    }

    public void execute(TransactionStep step) throws SQLException {
        PreparedStatement s = this.currentConn.prepareStatement(step.action);
        for (int i = 0; i < step.arguments.length; i++) {
            Object o = step.arguments[i];
            Class<?> t = o.getClass();
            int pIndex = i+1;
            if (t == Integer.class) {
                s.setInt(pIndex, (Integer) o);
            } else if (t == Float.class) {
                s.setFloat(pIndex, (Float) o);
            } else if (t == String.class) {
                s.setString(pIndex, (String) o);
            } else if (t == Double.class) {
                s.setDouble(pIndex, (Double) o);
            } else if (t == Long.class) {
                s.setLong(pIndex, (Long) o);
            }
            
        }
        int rowsAffected = s.executeUpdate();
        step.setRowsAffected(rowsAffected);
    }

    private String formatSql(String sql, Object... arguments) {
            return sql;
    }
}
