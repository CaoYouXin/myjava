package toonly.dbmanager.base;

import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.permission.P;
import toonly.dbmanager.permission.PofM;
import toonly.dbmanager.sqlbuilder.*;

import java.util.List;

/**
 * Created by cls on 15-3-13.
 */
public interface Selable extends Entity {

    @PofM(who = P.AB)
    default RS select() {
        ECCalculator ecc = new ECCalculator(this);

        SQL select = new Select(new TableId(this.getSchemaName(), this.getTableName()), ecc.getFields());
        return DB.instance().simpleQuery(select.toSql());
    }

    @PofM(who = P.AB)
    default RS keySelect() {
        ECCalculator ecc = new ECCalculator(this);

        TableId tableId = new TableId(this.getSchemaName(), this.getTableName());
        List<String> whereFields = ecc.getKeyFields();
        Where where = new Where(new Equal(tableId, whereFields.get(0)));
        whereFields.subList(1, whereFields.size()).forEach((f) -> where.addExpression(true, new Equal(tableId, f)));

        PreparedSQL select = new Select(tableId, ecc.getFields()).where(where);
        return DB.instance().preparedQuery(select.toPreparedSql(), ecc.getValuesInOrder(whereFields));
    }

    @PofM(who = P.AB)
    default RS filterSelect() {
        ECCalculator ecc = new ECCalculator(this);

        TableId tableId = new TableId(this.getSchemaName(), this.getTableName());
        List<String> whereFields = ecc.getNotNullFields();
        Where where = new Where(new Equal(tableId, whereFields.get(0)));
        whereFields.subList(1, whereFields.size()).forEach((f) -> where.addExpression(true, new Equal(tableId, f)));

        PreparedSQL select = new Select(tableId, ecc.getFields()).where(where);
        return DB.instance().preparedQuery(select.toPreparedSql(), ecc.getValuesInOrder(whereFields));
    }

}
