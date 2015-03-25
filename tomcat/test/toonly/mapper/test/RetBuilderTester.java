package toonly.mapper.test;

import org.junit.Test;
import org.slf4j.LoggerFactory;
import toonly.mapper.ret.RB;
import toonly.mapper.ret.RBArray;

import java.io.IOException;

/**
 * Created by cls on 15-3-5.
 */
public class RetBuilderTester {

    @Test
    public void builderToJsonTest() throws IOException {
        RB builder = new RB();

        builder.put("a", "你好");
        builder.put("c", "你好");
        builder.put("d", 365);
        builder.put("b", new RB().put("nested", "世界").put("b", new RB().put("nested", "世界")));
        builder.put("e", new RBArray().add(new RB().put("java", "good")).add(new RB().put("java", "very good")));

        LoggerFactory.getLogger(RetBuilderTester.class).info(builder.toJson());
    }

}
