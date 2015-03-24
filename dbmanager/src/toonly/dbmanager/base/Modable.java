package toonly.dbmanager.base;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.permission.P;
import toonly.dbmanager.permission.PofM;
import toonly.dbmanager.sqlbuilder.*;

import java.util.List;

/**
 * Created by caoyouxin on 15-2-19.
 */
public interface Modable extends Entity {

    @PofM(who = P.A)
    default public boolean modify() {
        ECCalculator ecc = new ECCalculator(this);

        TableId tableId = new TableId(this.getSchemaName(), this.getTableName());
        List<String> updateFields = ecc.getFieldsExclude(ecc.getKeyFields());
        Update update = new Update(tableId, new Equal(tableId, updateFields.get(0)));
        updateFields.subList(1, updateFields.size()).forEach(f -> update.set(new Equal(tableId, f)));

        List<String> whereFields = ecc.getKeyFields();
        Where where = new Where(new Equal(tableId, whereFields.get(0)));
        whereFields.subList(1, whereFields.size()).forEach(f -> where.addExpression(true, new Equal(tableId, f)));
        update.where(where);

        PreparedSQL pSQL = update::toPreparedSql;
        updateFields.addAll(whereFields);
        return DB.instance().preparedExecute(pSQL.toPreparedSql(), 1, ecc.getValuesInOrder(updateFields));
    }

}
