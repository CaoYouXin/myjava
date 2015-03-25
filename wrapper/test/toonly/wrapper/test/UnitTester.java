package toonly.wrapper.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.wrapper.StringWrapper;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class UnitTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitTester.class);

    @Test
    public void lambdaTest() {
        LOGGER.info(new StringWrapper("abc").md5Len16().val());
        LOGGER.info(new StringWrapper("abc").md5Len32().val());
    }

    @Test
    public void iminusminus() {
        int i = 1;
        LOGGER.info("{}", i-- > 0);
        LOGGER.info("{}", i-- > 0);
    }

}
