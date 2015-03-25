package toonly.configer.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.configer.FileTool;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitTester.class);
    private static final String REDIRECT_PROP = "redirect.prop";

    @Test
    public void streamTest() {
        SimpleConfiger<Properties> configer = new PropsConfiger();
        Properties config = configer.config("test.prop");
        LOGGER.info("{} = {}", "a", config.getProperty("a"));
    }

    @Test
    public void detectorTest() {
        String line = "abs%ss%ssd%sdf%af";
        List<String> docs = new ArrayList<>();

        docs.addAll(Arrays.asList(line.split("%")));
        docs.add("\n");

        LOGGER.info("{}", docs);

        Map<String, String> reps = new HashMap<>();
        reps.put("ss", "中文");
        reps.put("sdf", "中文");

        StringBuilder sb = new StringBuilder();
        docs.stream()
                .map(doc -> reps.containsKey(doc) ? reps.get(doc) : doc)
                .forEach(sb::append);
        LOGGER.info(sb.toString());
    }

    @Test
    public void resourceTest() throws IOException {
        Class<UnitTester> iConfigerClass = UnitTester.class;
        String name = new StringWrapper(iConfigerClass.getPackage().getName()).toUpPath().val()
                + REDIRECT_PROP;
        LOGGER.info(name);
        InputStream aaa = iConfigerClass.getResourceAsStream(name);
        Properties properties = new Properties();
        properties.load(aaa);
    }

    @Test
    public void asListTest() {
        List<String> strings = Arrays.asList("a", "b", "a");
        strings.stream().map(str -> "a".equals(str) ? "c" : str).forEach(LOGGER::info);
    }

    @Test
    public void lineSepTest() {
        LOGGER.info("A" + FileTool.LINE_SEPARATOR + "A");
    }

    public static void main(String[] args) throws IOException {
        InputStream aaa = UnitTester.class.getResourceAsStream(REDIRECT_PROP);
        Properties properties = new Properties();
        properties.load(aaa);
    }

    @Test
    public void logTest() {
        Map<String, String[]> map = new HashMap<>();
        map.put("a", new String[]{ "a1", "a2" });
        map.put("b", new String[]{ "b1", "b2" });
        Map<String, List<String>> printMap = new HashMap();
        map.forEach((k, l) -> printMap.put(k, Arrays.asList(l)));
        LOGGER.info("{}", printMap);
    }

}
