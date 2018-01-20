package transactionscript;

import java.util.*;
import java.sql.SQLException;
import com.zaxxer.hikari.*;

public class TransactionScript  {
    List<TransactionStep> steps = new ArrayList<TransactionStep>(); 
    Engine engine;

    public List<TransactionStep> getSteps() {
        return steps;
    }

    public void addStatement(String action, String sql, Object... args) {
        steps.add(new TransactionStep(action, sql, args));
    }

    public TransactionScript(Engine engine) {
        this.engine = engine;
    }

    public void execute() throws SQLException {
        engine.begin();
        try {
            ListIterator<TransactionStep> litr = steps.listIterator(); 
            while (litr.hasNext()) {
                TransactionStep s = litr.next();
                System.out.println(s.name + ":" + s.action);
                engine.execute(s);
            }
            engine.commit();
        } catch (SQLException e) {
            engine.rollback();
            e.printStackTrace();
            throw e;
        } 

    }
}
