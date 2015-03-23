
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
        String schemaName = Program.INSTANCE.getSchemaName();
        databases.forEach((db) -> System.out.println(db.equals(schemaName)));
        System.out.println(databases.contains(schemaName));
        boolean registered = Program.INSTANCE.isRegistered();
        System.out.println(registered);
        System.out.println(Program.INSTANCE.getStatus());
        if (!registered) {
            Program.INSTANCE.register();
        }
    }

}
