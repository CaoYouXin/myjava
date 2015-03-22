package toonly.mapper.test;

import org.junit.Test;

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
    public void reflectTest() {
        try {
            System.out.println(A.class.getMethod("m"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static class A {
        public void m() {

        }
    }

}
