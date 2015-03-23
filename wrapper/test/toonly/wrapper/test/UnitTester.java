package toonly.wrapper.test;

import org.junit.Test;
import toonly.wrapper.StringWrapper;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class UnitTester {

    @Test
    public void lambdaTest() {
        System.out.println(new StringWrapper("abc").md5Len16().val());
        System.out.println(new StringWrapper("abc").md5Len32().val());
    }

    @Test
    public void iminusminus() {
        int i = 1;
        System.out.println(i-- > 0);
        System.out.println(i-- > 0);
    }

}
