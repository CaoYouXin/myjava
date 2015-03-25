package toonly.damanager.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.dbmanager.lowlevel.DB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cls on 15-3-23.
 */
public class DBTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBTester.class);

    @Test
    public void listOfObjectTest() {
        List<Object> params = Arrays.asList("a", "b", 21);
        LOGGER.info("\tParams wa {}", params);
    }

    @Test
    public void parseLabelsTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method parseLabels = DB.class.getDeclaredMethod("parseLabels", String.class);
        boolean accessible = parseLabels.isAccessible();
        parseLabels.setAccessible(true);
        Object invoke = parseLabels.invoke(DB.instance(), "SELECT `name`, `version` FROM `repodb`.`program` WHERE `repodb`.`program`.`name` = ?");
        parseLabels.setAccessible(accessible);
        if (invoke instanceof String[]) {
            LOGGER.info("{}", Arrays.asList((String[]) invoke));
        }
        DB.instance().close();
    }

}
