package toonly.configer.test;

import org.junit.Test;
import toonly.configer.PropsConfiger;
import toonly.configer.SimpleConfiger;
import toonly.wrapper.StringWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by caoyouxin on 15-2-22.
 */
public class UnitTester {

    @Test
    public void streamTest() {
        SimpleConfiger<Properties> configer = new PropsConfiger();
        Properties config = configer.config("test.prop");
        System.out.println(String.format("%s = %s", "a", config.getProperty("a")));
    }

    @Test
    public void detectorTest() {
        String line = "abs%ss%ssd%sdf%af";
        List<String> docs = new ArrayList<>();

        int i = 0;
        while (true) {
            int j = line.indexOf('%', i);

            if (-1 == j) {
                // no more replacements
                docs.add(line.substring(i, line.length()));
                break;
            } else {
                docs.add(line.substring(i, j));
            }

            int k = line.indexOf('%', j + 1);

            docs.add(line.substring(j + 1, k));

            i = k + 1;
        }

        System.out.println(docs);

        Map<String, String> reps = new HashMap<>();
        reps.put("ss", "中文");
        reps.put("sdf", "中文");

        StringBuilder sb = new StringBuilder();
        docs.stream()
                .map((doc) -> reps.containsKey(doc) ? reps.get(doc) : doc)
                .forEach((doc) -> sb.append(doc));
        System.out.println(sb.toString());
    }

    @Test
    public void resourceTest() {
        Class<UnitTester> iConfigerClass = UnitTester.class;
        String name = new StringWrapper(iConfigerClass.getPackage().getName()).toUpPath().val()
                + "redirect.prop";
        System.out.println(name);
        InputStream aaa = iConfigerClass.getResourceAsStream(name);
        Properties properties = new Properties();
        try {
            properties.load(aaa);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        InputStream aaa = UnitTester.class.getResourceAsStream("redirect.prop");
        Properties properties = new Properties();
        try {
            properties.load(aaa);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
