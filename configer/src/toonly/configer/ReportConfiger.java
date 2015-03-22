package toonly.configer;

import toonly.configer.cache.Cache;
import toonly.configer.cache.CachedConfiger;
import toonly.configer.cache.UncachedException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class ReportConfiger implements FileTool, CachedConfiger<ReportConfiger>, SimpleConfiger<ReportConfiger> {

    private static class MyList extends ArrayList<String> {}

    private Cache<MyList> cache = Cache.get(MyList.class);

    private MyList _docs = new MyList();
    private Map<String, String> _reps = new HashMap<>();

    public ReportConfiger() {
    }

    public ReportConfiger report(String key, String value) {
        this._reps.put(key, value);
        return this;
    }

    @Override
    public ReportConfiger config(String relativePath) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.getFile(relativePath))));
        } catch (FileNotFoundException e) {
        }

        if (null == bufferedReader) {
            return null;
        }

        bufferedReader.lines().forEach((line) -> {
            int i = 0;
            while (true) {
                int j = line.indexOf('%', i);

                if (-1 == j) {
                    // no more replacements
                    this._docs.add(line.substring(i, line.length()));
                    break;
                } else {
                    this._docs.add(line.substring(i, j));
                }

                int k = line.indexOf('%', j + 1);

                this._docs.add(line.substring(j + 1, k));

                i = k + 1;
            }

            this._docs.add("\n");
        });
        cache.store(relativePath, this._docs);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this._docs.stream()
                .map((doc) -> this._reps.containsKey(doc) ? this._reps.get(doc) : doc)
                .forEach(sb::append);
        return sb.toString();
    }

    @Override
    public ReportConfiger cache(String relativePath) throws UncachedException {
        ReportConfiger reportConfiger = new ReportConfiger();
        reportConfiger._docs = cache.cache(relativePath);
        return reportConfiger;
    }
}
