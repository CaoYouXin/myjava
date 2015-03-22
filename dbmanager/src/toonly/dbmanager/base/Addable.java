package toonly.dbmanager.base;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.permission.P;
import toonly.dbmanager.permission.PofM;
import toonly.dbmanager.sqlbuilder.*;

import java.util.List;

/**
 * Created by caoyouxin on 15-2-19.
 */
public interface Addable extends Entity {

    @PofM(who = P.AB)
    default boolean add() {
        ECCalculator ecc = new ECCalculator(this);

        PreparedSQL insert = new Insert(new TableId(this.getSchemaName(), this.getTableName()) , ecc.getFields());
        return DB.instance().preparedExecute(insert.toPreparedSql(), 1, ecc.getValues());
    }

    @PofM(who = P.AB)
    default boolean addForDuplicated() {
        ECCalculator ecc = new ECCalculator(this);

        TableId tableId = new TableId(this.getSchemaName(), this.getTableName());
        List<String> fields = ecc.getFields();
        DuplicatedUpdate insert = new DuplicatedUpdate(tableId, fields);

        List<String> setFields = ecc.getDuplicatedFields();
        setFields.forEach((f) -> insert.set(new Equal(tableId, f)));
        setFields.addAll(0, fields);

        PreparedSQL pSQL = insert::toPreparedSql;
        return DB.instance().preparedExecute(pSQL.toPreparedSql(), 1, ecc.getValuesInOrder(setFields));
    }

}
