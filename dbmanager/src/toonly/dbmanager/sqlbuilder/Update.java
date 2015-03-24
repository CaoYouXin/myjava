package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by caoyouxin on 15-1-23.
 */
public class Update extends Delete implements SQL, PreparedSQL {

    private static final String INNER_STATE_ERROR = "inner state error.";
    private static final String BASIC = "UPDATE %s SET %s";
    private static final String SOMETHING = " %s";

    private List<Equal> sets;

    public Update(TableId tableId, Equal equal) {
        super(tableId);
        this.set(equal);
    }

    public Update set(@NotNull Equal equal) {
        if (null == this.sets) {
            this.sets = new ArrayList<>();
        }
        this.sets.add(equal);
        return this;
    }

    @Override
    public Update where(Where where) {
        return (Update) super.where(where);
    }

    @Override
    public String toSql() {
        return this.sql(SQL::toSql, SQL::toSql);
    }

    @Override
    public String toPreparedSql() {
        return this.sql(PreparedSQL::toPreparedSql, PreparedSQL::toPreparedSql);
    }

    private String sql(Function<Equal, String> fn1, Function<Where, String> fn2) {
        if (null == this.where) {
            return String.format(BASIC, this.tableId.toSql(), this.setsToSQL(fn1));
        }
        return String.format(BASIC + SOMETHING, this.tableId.toSql(), this.setsToSQL(fn1), fn2.apply(this.where));
    }

    private String setsToSQL(Function<Equal, String> fn) {
        if (this.sets.isEmpty()) {
            throw new BuildException(INNER_STATE_ERROR);
        }

        StringBuilder sb = new StringBuilder(fn.apply(this.sets.get(0)));
        for (int i = 1; i < this.sets.size(); i++) {
            sb.append(", ").append(fn.apply(this.sets.get(i)));
        }

        return sb.toString();
    }
}
