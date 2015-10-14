package main;

import com.lambdaworks.redis.*;

import java.io.*;
import java.util.Objects;

public class ConnectToRedis {

    public static void main(String[] args) {
        RedisClient redisClient = new RedisClient(
                RedisURI.create("redis://youxin1991@redis-11953.redis.local:11953"));
        RedisConnection<String, String> connection = redisClient.connect();

        System.out.println("Connected to Redis");

        int count = 0;
        char[] cbuf = new char[5120];
        try (BufferedReader br = new BufferedReader(new FileReader("NAT_time.dbf"))) {
            int read = br.read(cbuf);
            while (read > 0) {
                String value = new String(cbuf, 0, read);

                System.out.println(value);

                connection.set("foo" + (count++), value);

                read = br.read(cbuf);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

//        for (String key : connection.keys("*")) {
//            System.out.println(String.format("%s :=> %s", key, connection.get(key)));
//            connection.del(key);
//        }

        connection.close();
        redisClient.shutdown();
    }
}