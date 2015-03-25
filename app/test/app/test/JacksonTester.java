package app.test;

import app.entity.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cls on 15-3-4.
 */
public class JacksonTester {

    public static final Logger LOGGER = LoggerFactory.getLogger(JacksonTester.class);

    @Test
    public void fromJsonTest() {
        String json = "{\"id\":1,\"name\":\"fucker\"}";

        User user = new User();
        user.fromJson(json);
        LOGGER.info(user.toString());
    }

    @Test
    public void toJsonTest() {
        LOGGER.info(new User(1, "fucker").toJson());
    }

}
