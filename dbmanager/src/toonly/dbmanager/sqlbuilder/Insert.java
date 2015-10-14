package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.NotNull;
import toonly.wrapper.StringWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class Insert implements PreparedSQL, SQL {

    protected TableId tableId;
    protected List<String> fields;
    protected List<List<String>> values = new ArrayList<>();

    public Insert(TableId tableId, String... fields) {
        this.tableId = tableId;
        this.fields = Arrays.asList(fields);
    }

    public Insert(TableId tableId, @NotNull List<String> fields) {
        this.tableId = tableId;
        this.fields = fields;
    }

    public Insert add(List<String> row) {
        this.values.add(row);
        return this;
    }

    @Override
    public String toPreparedSql() {
        return String.format("INSERT INTO %s(%s) VALUES(%s)"
                , this.tableId.toSql()
                , new StringWrapper().wrapJoin(this.fields, "`", ", ").val()
                , new StringWrapper().replaceJoin(this.fields, "?", ", ").val()
        );
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder(new StringWrapper(Arrays.toString(this.values.get(0).toArray())).unwrap().wrap("(", ")").val());
        for (int i = 1; i < this.values.size(); i++) {
            sb.append(',').append(new StringWrapper(Arrays.toString(this.values.get(i).toArray())).unwrap().wrap("(", ")").val());
        }
        return String.format("INSERT INTO %s(%s) VALUES%s"
                , this.tableId.toSql()
                , new StringWrapper().wrapJoin(this.fields, "`", ", ").val()
                , sb.toString()
        );
    }
}
