package toonly.configer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsConfiger.class);

    private final Cache<Properties> cache = Cache.get(Properties.class);
    private ChangeWatcherSupport<PropsConfiger> support = new ChangeWatcherSupport<>(this);

    @Override
    public Properties cache(String relativePath) {
        try {
            return cache.cache(relativePath);
        } catch (UncachedException e) {
            LOGGER.info(e.getLocalizedMessage());
            return this.config(relativePath);
        }
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

    @Override
    public PropsConfiger addChangeListener(ChangeListener listener) {
        return this.support.addChangeListener(listener);
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
