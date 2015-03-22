package app.test;

import app.entity.User;
import org.junit.Test;

/**
 * Created by cls on 15-3-4.
 */
public class JacksonTester {

    @Test
    public void fromJsonTest() {
        String json = "{\"id\":1,\"name\":\"fucker\"}";

        User user = new User();
        user.fromJson(json);
        System.out.println(user.toString());
    }

    @Test
    public void toJsonTest() {
        System.out.println(new User(1, "fucker").toJson());
    }

}
