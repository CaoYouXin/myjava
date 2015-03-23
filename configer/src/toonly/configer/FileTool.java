package toonly.configer;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.wrapper.StringWrapper;

import java.io.File;
import java.util.function.Function;

/**
 * Created by caoyouxin on 15-2-23.
 * 工具
 */
public interface FileTool {

    public static Logger LOGGER = LoggerFactory.getLogger(FileTool.class);

    public static String FILE_SEPARATOR = "file.separator";

    public static String getConfigsPath() {
        return getPath(t -> t);
    }

    public static String getConfigFilePath(@NotNull String relativePath) {
        String fileSep = System.getProperty(FILE_SEPARATOR);
        return getPath(t -> t + fileSep + relativePath);
    }

    public static String getPath(@NotNull Function<String, String> fn) {
        String path = new File("").getAbsolutePath();
        String fileSep = System.getProperty(FILE_SEPARATOR);
        String configs = "configs";

        String name = String.format("%s%s%s", path.substring(0, path.lastIndexOf(fileSep))
                , fileSep, configs);
        File file = new File(name);
        if (file.exists()) {
            return fn.apply(name);
        }

        Class<FileTool> iConfigerClass = FileTool.class;
        String pathname = iConfigerClass.getResource(
                new StringWrapper(iConfigerClass.getPackage().getName()).toUpPath().val()
                        + fileSep + configs).getFile();
        return fn.apply(pathname);
    }

    /**
     * 首先查找[myJava]/configs/[relativePath]
     * 然后按照resourcePath查找
     */
    public default File getFile(@NotNull String relativePath) {
        String configFilePath = getConfigFilePath(relativePath);
        LOGGER.info("reading config[{}]", configFilePath);
        return new File(configFilePath);
    }

}
