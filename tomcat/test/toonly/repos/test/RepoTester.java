
package toonly.repos.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.repos.Program;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cls on 15-3-14.
 */
public class RepoTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepoTester.class);
    
    @Test
    public void test() {
        List<String> databases = DB.instance().showDatabases();
        String schemaName = Program.INSTANCE.getSchemaName();
        databases.forEach(db -> LOGGER.info("{}", db.equals(schemaName)));
        LOGGER.info("{}", databases.contains(schemaName));
        boolean registered = Program.INSTANCE.isRegistered();
        LOGGER.info("{}", registered);
        LOGGER.info("{}", Program.INSTANCE.getStatus());
        if (!registered) {
            Program.INSTANCE.register();
        }
    }

    @Test
    public void nullTest() {
        Map<String, String> name2User = new HashMap<>();
        LOGGER.info("{}", name2User.get(null));
    }

}
