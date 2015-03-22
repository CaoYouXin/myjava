package toonly.configer.cache;

/**
 * Created by caoyouxin on 15-2-22.
 * 对外高级接口
 */
public interface CachedConfiger<T> {

    T cache(String relativePath) throws UncachedException;

}
