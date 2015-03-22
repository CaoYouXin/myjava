package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.NotNull;
import toonly.wrapper.StringWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoyouxin on 15-2-12.
 */
public class DuplicatedUpdate extends Insert implements PreparedSQL {

    private List<Equal> sets;

    public DuplicatedUpdate(TableId tableId, String... fields) {
        super(tableId, fields);
    }

    public DuplicatedUpdate(TableId tableId, @NotNull List<String> fields) {
        super(tableId, fields);
    }

    public DuplicatedUpdate set(@NotNull Equal equal) {
        if (null == this.sets) {
            this.sets = new ArrayList<>();
        }
        this.sets.add(equal);
        return this;
    }

    @Override
    public String toPreparedSql() {
        if (null == this.sets) {
            return super.toPreparedSql();
        }

        StringBuilder sb = new StringBuilder(this.sets.get(0).toPreparedSql());
        for (int i = 1; i < this.sets.size(); i++) {
            sb.append(", ").append(this.sets.get(i).toPreparedSql());
        }

        return String.format("INSERT INTO %s(%s) VALUES(%s) ON DUPLICATE KEY UPDATE %s"
                , this.tableId.toSql()
                , new StringWrapper().wrapJoin(this.fields, "`", ", ").val()
                , new StringWrapper().replaceJoin(this.fields, "?", ", ").val()
                , sb.toString()
        );
    }
}
