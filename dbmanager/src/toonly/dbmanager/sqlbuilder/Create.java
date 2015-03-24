package toonly.dbmanager.sqlbuilder;

import toonly.dbmanager.lowlevel.DT;
import toonly.wrapper.SW;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cls on 15-3-14.
 */
public class Create extends Drop implements SQL {

    private List<DBField> fields;

    public Create(TableId tableId, DBField field) {
        super(tableId);
        this.fields = new ArrayList<>();
        this.fields.add(field);
    }

    public Create(TableId tableId, List<DBField> fields) {
        super(tableId);
        this.fields = fields;
    }

    @Override
    public String toSql() {
        StringBuilder fieldDefines = new StringBuilder();
        StringBuilder keyDefines = new StringBuilder();
        SW<Boolean> bool = new SW<>(false);
        this.fields.forEach(field -> {
            if (field.isKey) {
                if (!bool.val()) {
                    bool.val(true);
                }
                keyDefines.append(", `").append(field.name).append('`');
            }
            fieldDefines.append(field.toSql()).append(", ");
        });
        return String.format("CREATE TABLE %s (%sPRIMARY KEY (%s)) ENGINE=InnoDB DEFAULT CHARSET='utf8'", this.tableId.toSql(), fieldDefines.toString(), keyDefines.substring(2));
    }

    public Create field(DBField field) {
        this.fields.add(field);
        return this;
    }

    public static class DBField implements SQL {
        private String name;
        private DT.Type type;
        private boolean isKey;

        public DBField(String name, DT.Type type, boolean isKey) {
            this.name = name;
            this.type = type;
            this.isKey = isKey;
        }

        @Override
        public String toSql() {
            return String.format("`%s` %s", this.name, type.getType());
        }
    }

}
