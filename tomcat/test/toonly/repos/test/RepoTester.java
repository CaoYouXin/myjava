
package toonly.repos.test;

import org.junit.Test;
import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.repos.Program;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void nullTest() {
        Map<String, String> NAME_2_USER = new HashMap<>();
        System.out.println(NAME_2_USER.get(null));
    }

}
