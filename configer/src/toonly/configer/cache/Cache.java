package toonly.configer.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cls on 15-3-14.
 */
public class Cache<T> implements CachedConfiger<T> {

    private static final Map<Class<?>, Cache<?>> CACHE_CACHE = new HashMap<>();

    private final Map<String, T> map = new ConcurrentHashMap<>();

    private Cache() {
    }

    public static synchronized <R> Cache<R> get(Class<R> rClass) {
        Cache<?> cache = CACHE_CACHE.get(rClass);
        if (null == cache) {
            cache = new Cache<R>();
        }
        CACHE_CACHE.put(rClass, cache);
        return (Cache<R>) cache;
    }

    @Override
    public T cache(String relativePath) throws UncachedException {
        T cache = map.get(relativePath);
        if (null == cache) {
            throw new UncachedException(relativePath);
        }
        return cache;
    }

    public boolean store(String relativePath, T obj) {
        map.put(relativePath, obj);
        return true;
    }
}
