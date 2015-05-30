package toonly.dbmanager.sqlbuilder;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class Equal implements Expression {

    private TableId tableId;
    private String column;
    private String expect;
    private boolean needWrap;

    public Equal(TableId tableId, String column, String expect, boolean needWrap) {
        this.tableId = tableId;
        this.column = column;
        this.expect = expect;
        this.needWrap = needWrap;
    }

    public Equal(TableId tableId, String column) {
        this.tableId = tableId;
        this.column = column;
    }

    @Override
    public String toSql() {
        if (null == this.expect) {
            throw new NullPointerException("inner state error.");
        }

        if (this.needWrap) {
            return String.format("%s.`%s` = '%s'", this.tableId.toSql(), this.column, this.expect);
        }
        return String.format("%s.`%s` = %s", this.tableId.toSql(), this.column, this.expect);
    }

    @Override
    public String toPreparedSql() {
        if (null != this.expect) {
            return this.toSql();
        }
        return String.format("%s.`%s` = ?", this.tableId.toSql(), this.column);
    }
}
