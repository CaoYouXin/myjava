package toonly.mapper.test;

import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * Created by cls on 15-3-15.
 */
public class ObjectTester {

    @Test
    public void grammarTest() {
        Object o = null;
        o = getObject();
    }

    private Object getObject() {
        return null;
    }

    @Test
    public void reflectTest() throws NoSuchMethodException {
        LoggerFactory.getLogger(ObjectTester.class).info("{}", A.class.getMethod("m"));
    }

    private static class A {
        public void m() {
            LoggerFactory.getLogger(ObjectTester.class).info("oh, im m.");
        }
    }

}
