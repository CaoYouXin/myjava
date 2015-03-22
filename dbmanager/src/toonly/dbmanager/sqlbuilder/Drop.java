package toonly.dbmanager.sqlbuilder;

/**
 * Created by cls on 15-3-14.
 */
public class Drop implements SQL {

    protected TableId tableId;
    private boolean ifExist = false;

    public Drop(TableId tableId) {
        this.tableId = tableId;
    }

    public Drop ifExist() {
        this.ifExist = true;
        return this;
    }

    @Override
    public String toSql() {
        return String.format("DROP TABLE%s %s", this.ifExist ? " IF EXISTS" : "", this.tableId.toSql());
    }
}
