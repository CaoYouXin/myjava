
package toonly.repos.test;

import org.junit.Test;
import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.repos.Program;

import java.util.List;

/**
 * Created by cls on 15-3-14.
 */
public class RepoTester {

    @Test
    public void test() {
        List<String> databases = DB.instance().showDatabases();
        String schemaName = Program.instance.getSchemaName();
        databases.forEach((db) -> System.out.println(db.equals(schemaName)));
        System.out.println(databases.contains(schemaName));
//        DB.instance().debug(true);
//        Program.instance.reCreateDatabase();
//        Program.instance.createTable();
//        DB.instance().createDatabase("repodb");
        boolean registered = Program.instance.isRegistered();
        System.out.println(registered);
        System.out.println(Program.instance.getStatus());
        if (!registered) {
//            DB.instance().debug(true);
            Program.instance.register();
//            System.out.println(Program.instance.addForDuplicated());
        }
//        DB.instance().debug(false);
//        DB.instance().preparedExecute("INSERT INTO `repodb`.`program`(`name`, `version`) VALUES(?, ?)", 1, "storehouse", 0);
    }

}
