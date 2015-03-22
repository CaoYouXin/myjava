package toonly.wrapper.test;

import org.junit.Test;
import toonly.wrapper.StringWrapper;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class UnitTester {

    @Test
    public void lambdaTest() {
        System.out.println(new StringWrapper("abc").md5_16().val());
        System.out.println(new StringWrapper("abc").md5_32().val());
    }

}
