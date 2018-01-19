package transactionscript;

public class TransactionStep  {
    String name;
    String action;
    Object[] arguments;
    int rowsAffected;

    public Object[] getArguments() {
        return arguments;
    }

    public int getRowsAffected() {
        return this.rowsAffected;
    }

    public void setRowsAffected(int affected) {
        this.rowsAffected = affected;
    }

    public TransactionStep(String name, String action, Object... arguments) {
        this.name = name;
        this.action = action;
        this.arguments = arguments;
    }

}

