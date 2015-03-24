package toonly.dbmanager.sqlbuilder;

import java.util.function.Function;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class Delete implements SQL, PreparedSQL {

    private static final String BASIC = "DELETE FROM %s";
    private static final String SOMETHING = " %s";

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
        return this.sql(SQL::toSql);
    }

    @Override
    public String toPreparedSql() {
        return this.sql(PreparedSQL::toPreparedSql);
    }

    private String sql(Function<Where, String> fn) {
        if (null == this.where) {
            return String.format(BASIC, this.tableId.toSql());
        }
        return String.format(BASIC + SOMETHING, this.tableId.toSql(), fn.apply(this.where));
    }

}
