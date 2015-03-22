package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.NotNull;
import toonly.wrapper.StringWrapper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class Insert implements PreparedSQL {

    protected TableId tableId;
    protected List<String> fields;

    public Insert(TableId tableId, String... fields) {
        this.tableId = tableId;
        this.fields = Arrays.asList(fields);
    }

    public Insert(TableId tableId, @NotNull List<String> fields) {
        this.tableId = tableId;
        this.fields = fields;
    }

    @Override
    public String toPreparedSql() {
        return String.format("INSERT INTO %s(%s) VALUES(%s)"
                , this.tableId.toSql()
                , new StringWrapper().wrapJoin(this.fields, "`", ", ").val()
                , new StringWrapper().replaceJoin(this.fields, "?", ", ").val()
        );
    }
}
