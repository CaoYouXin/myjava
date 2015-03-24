package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.Nullable;
import toonly.wrapper.StringWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by cls on 15-3-13.
 */
public class Select extends Delete implements SQL, PreparedSQL {

    private static final String BASIC_SELECT = "SELECT %s FROM %s";
    private static final String SOMETHING = " %s";

    @Nullable
    private List<String> targets;

    public Select(TableId tableId) {
        super(tableId);
    }

    public Select(TableId tableId, String... fields) {
        this(tableId);
        this.targets = Arrays.asList(fields);
    }

    public Select(TableId tableId, List<String> fields) {
        this(tableId);
        this.targets = fields;
    }

    @Override
    public Select where(Where where) {
        return (Select) super.where(where);
    }

    @Override
    public String toPreparedSql() {
        return this.sql(PreparedSQL::toPreparedSql);
    }

    @Override
    public String toSql() {
        return this.sql(SQL::toSql);
    }

    private String sql(Function<Where, String> fn) {
        if (null == this.where) {
            return String.format(BASIC_SELECT, this.getTargets(), this.tableId.toSql());
        }
        return String.format(BASIC_SELECT + SOMETHING, this.getTargets(), this.tableId.toSql(), fn.apply(this.where));
    }

    private String getTargets() {
        return (null == this.targets) ? "*" : new StringWrapper().wrapJoin(this.targets, "`", ", ").val();
    }
}
