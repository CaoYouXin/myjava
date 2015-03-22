package toonly.configer;

import toonly.wrapper.StringWrapper;

import java.io.File;

/**
 * Created by caoyouxin on 15-2-23.
 * 工具
 */
public interface FileTool {

    /**
     * 首先查找[myJava]/configs/[relativePath]
     * 然后按照resourcePath查找
     */
    default File getFile(String relativePath) {
        String fileSep = System.getProperty("file.separator");
        return new File(getConfigsPath() + fileSep + relativePath);
    }

    public static String getConfigsPath() {
        String path = new File("").getAbsolutePath();
        String fileSep = System.getProperty("file.separator");
        String configs = "configs";

        String name = String.format("%s%s%s", path.substring(0, path.lastIndexOf(fileSep))
                , fileSep, configs);
        System.out.println(String.format("getting config of %s", name));
        File file = new File(name);
        if (file.exists())
            return name;

        Class<FileTool> iConfigerClass = FileTool.class;
        String pathname = iConfigerClass.getResource(
                new StringWrapper(iConfigerClass.getPackage().getName()).toUpPath().val()
                        + fileSep + configs).getFile();
        System.out.println(String.format("getting config of %s", pathname));
        return pathname;
    }

}
