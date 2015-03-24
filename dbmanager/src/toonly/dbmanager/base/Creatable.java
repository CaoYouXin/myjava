package toonly.dbmanager.base;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.sqlbuilder.Create;
import toonly.dbmanager.sqlbuilder.Drop;
import toonly.dbmanager.sqlbuilder.SQL;
import toonly.dbmanager.sqlbuilder.TableId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cls on 15-3-13.
 */
public interface Creatable extends Entity {

    public static SQL getCreate(ECCalculator ecc, TableId tableId) {
        List<Create.DBField> fields = new ArrayList<>();
        List<String> keyFields = ecc.getKeyFields();

        ecc.dtForEach((name, type) -> fields.add(new Create.DBField(name, type.type(), keyFields.contains(name))));

        return new Create(tableId, fields);
    }

    default public boolean isDatabaseExist() {
        List<String> databases = DB.instance().showDatabases();
        return databases.contains(this.getSchemaName());
    }

    default public boolean createDatabase() {
        return DB.instance().createDatabase(this.getSchemaName());
    }

    default public boolean reCreateDatabase() {
        return DB.instance().dropDatabase(this.getSchemaName())
                && DB.instance().createDatabase(this.getSchemaName());
    }

    default public boolean isTableExist() {
        List<String> tables = DB.instance().showTables(this.getSchemaName());
        return tables.contains(this.getTableName());
    }

    default public boolean createTable() {
        ECCalculator ecc = new ECCalculator(this);

        TableId tableId = new TableId(this.getSchemaName(), this.getTableName());

        SQL create = getCreate(ecc, tableId);
        return DB.instance().simpleExecute(create.toSql());
    }

    default public boolean reCreateTable() {
        ECCalculator ecc = new ECCalculator(this);

        TableId tableId = new TableId(this.getSchemaName(), this.getTableName());

        SQL drop = new Drop(tableId).ifExist();
        SQL create = getCreate(ecc, tableId);
        return DB.instance().simpleExecute(drop.toSql()) && DB.instance().simpleExecute(create.toSql());
    }

}
