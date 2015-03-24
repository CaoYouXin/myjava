package toonly.damanager.test;

import org.junit.Test;
import toonly.dbmanager.sqlbuilder.Equal;
import toonly.dbmanager.sqlbuilder.TableId;
import toonly.dbmanager.sqlbuilder.Update;
import toonly.dbmanager.sqlbuilder.Where;

/**
 * Created by cls on 15-3-24.
 */
public class BuilderTester {

    @Test
    public void lambdaBuilderTest() {
        TableId tableId = new TableId("shema", "table");
        Update update = new Update(tableId, new Equal(tableId, "c1", "v1", true))
                .set(new Equal(tableId, "c2"))
                .where(new Where(new Equal(tableId, "c3", "1", false))
                        .addExpression(true, new Equal(tableId, "c4")));
        System.out.println(update.toPreparedSql());
//        System.out.println(update.toSql());
    }

}
