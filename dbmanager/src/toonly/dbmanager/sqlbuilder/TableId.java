package toonly.dbmanager.sqlbuilder;

/**
 * Created by caoyouxin on 15-1-22.
 */
public class TableId implements SQL {

    String schema;
    String table;

    public TableId(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    @Override
    public String toSql() {
        return String.format("`%s`.`%s`", this.schema, this.table);
    }
}
