package toonly.configer.cache;

/**
 * Created by cls on 15-3-14.
 */
public class UncachedException extends Exception {
    public UncachedException(String relativePath) {
        super(String.format("file[%s] not cached.", relativePath));
    }
}
