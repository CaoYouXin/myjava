package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.Nullable;
import toonly.wrapper.StringWrapper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cls on 15-3-13.
 */
public class Select extends Delete implements SQL, PreparedSQL {

    @Nullable private List<String> targets;

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
    public String toPreparedSql() {
        if (null == this.where)
            return String.format("SELECT %s FROM %s", this.getTargets(), this.tableId.toSql());
        return String.format("SELECT %s  FROM %s %s", this.getTargets(), this.tableId.toSql(), this.where.toPreparedSql());
    }

    @Override
    public String toSql() {
        if (null == this.where)
            return String.format("SELECT %s FROM %s", this.getTargets(), this.tableId.toSql());
        return String.format("SELECT %s  FROM %s %s", this.getTargets(), this.tableId.toSql(), this.where.toSql());
    }

    private String getTargets() {
        return (null == this.targets) ? "*" : new StringWrapper().wrapJoin(this.targets, "`", ", ").val();
    }
}
