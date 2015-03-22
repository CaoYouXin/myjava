package toonly.dbmanager.sqlbuilder;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class Delete implements SQL, PreparedSQL {

    protected TableId tableId;
    protected Where where;

    public Delete(TableId tableId) {
        this.tableId = tableId;
    }

    public Delete where(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public String toSql() {
        if (null == this.where)
            return String.format("DELETE FROM %s", this.tableId.toSql());
        return String.format("DELETE FROM %s %s", this.tableId.toSql(), this.where.toSql());
    }

    @Override
    public String toPreparedSql() {
        if (null == this.where)
            return String.format("DELETE FROM %s", this.tableId.toSql());
        return String.format("DELETE FROM %s %s", this.tableId.toSql(), this.where.toPreparedSql());
    }
}
