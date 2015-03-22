package toonly.configer;

import toonly.configer.cache.Cache;
import toonly.configer.cache.CachedConfiger;
import toonly.configer.cache.UncachedException;
import toonly.configer.watcher.ChangeWatcher;
import toonly.configer.watcher.ChangeWatcherSupport;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by caoyouxin on 15-2-22.
 */
public class PropsConfiger implements FileTool, CachedConfiger<Properties>, SimpleConfiger<Properties>, ChangeWatcher<PropsConfiger> {

    private final Cache<Properties> cache = Cache.get(Properties.class);

    @Override
    public Properties cache(String relativePath) throws UncachedException {
        return cache.cache(relativePath);
    }

    @Override
    public Properties config(String relativePath) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(this.getFile(relativePath)));
        } catch (IOException ex) {
            return null;
        }
        cache.store(relativePath, props);
        return props;
    }

    private ChangeWatcherSupport<PropsConfiger> support = new ChangeWatcherSupport<>(this);

    @Override
    public void AddChangeListener(ChangeListener listener) {
        this.support.AddChangeListener(listener);
    }

    @Override
    public PropsConfiger watch(String relativePath) {
        return this.support.watch(relativePath);
    }

    @Override
    public PropsConfiger stop() {
        return this.support.stop();
    }
}
