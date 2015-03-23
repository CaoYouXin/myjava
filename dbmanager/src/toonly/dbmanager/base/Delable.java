package toonly.dbmanager.base;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.permission.P;
import toonly.dbmanager.permission.PofM;
import toonly.dbmanager.sqlbuilder.*;

import java.util.List;

/**
 * Created by caoyouxin on 15-2-19.
 */
public interface Delable extends Entity {

    @PofM(who = P.A)
    default boolean delete() {
        ECCalculator ecc = new ECCalculator(this);

        TableId tableId = new TableId(this.getSchemaName(), this.getTableName());
        List<String> whereFields = ecc.getKeyFields();
        Where where = new Where(new Equal(tableId, whereFields.get(0)));
        whereFields.subList(1, whereFields.size()).forEach(f -> where.addExpression(true, new Equal(tableId, f)));

        PreparedSQL delete = new Delete(tableId).where(where);
        return DB.instance().preparedExecute(delete.toPreparedSql(), 1, ecc.getValuesInOrder(whereFields));
    }

}
