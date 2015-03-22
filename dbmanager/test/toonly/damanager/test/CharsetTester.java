package toonly.damanager.test;

import org.junit.Test;
import toonly.dbmanager.lowlevel.DB;

/**
 * Created by cls on 15-3-19.
 */
public class CharsetTester {

    @Test
    public void test() {
        DB.instance().simpleExecute("insert into `storehouse`.`goods`(`code`,`color`,`size`,`name`) values('啊','啊','啊','啊');");
    }

}
