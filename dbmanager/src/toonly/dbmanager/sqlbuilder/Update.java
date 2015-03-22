package toonly.dbmanager.sqlbuilder;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoyouxin on 15-1-23.
 */
public class Update extends Delete implements SQL, PreparedSQL {

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
    public String toSql() {
        if (null == this.where)
            return String.format("UPDATE %s %s", this.tableId.toSql(), this._toSql());
        return String.format("UPDATE %s %s %s", this.tableId.toSql(), this._toSql(), this.where.toSql());
    }

    private String _toSql() {
        if (1 > this.sets.size()) {
            throw new RuntimeException("inner state error.");
        }

        StringBuilder sb = new StringBuilder(this.sets.get(0).toSql());
        for (int i = 1; i < this.sets.size(); i++) {
            sb.append(", ").append(this.sets.get(i).toSql());
        }

        return String.format("SET %s", sb.toString());
    }

    @Override
    public String toPreparedSql() {
        if (null == this.where)
            return String.format("UPDATE %s %s", this.tableId.toSql(), this._toPreparedSql());
        return String.format("UPDATE %s %s %s", this.tableId.toSql(), this._toPreparedSql(), this.where.toPreparedSql());
    }

    private String _toPreparedSql() {
        if (1 > this.sets.size()) {
            throw new RuntimeException("inner state error.");
        }

        StringBuilder sb = new StringBuilder(this.sets.get(0).toPreparedSql());
        for (int i = 1; i < this.sets.size(); i++) {
            sb.append(", ").append(this.sets.get(i).toPreparedSql());
        }

        return String.format("SET %s", sb.toString());
    }
}
