package toonly.damanager.test;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cls on 15-3-23.
 */
public class DBTester {

    @Test
    public void listOfObjectTest() {
        List<Object> params = Arrays.asList("a", "b", 21);
        System.out.println(String.format("\tParams wa %s", params));
    }

}
