package toonly.configer.watcher;

/**
 * Created by cls on 15-3-16.
 */
public interface ChangeWatcher<T> {

    public static interface ChangeListener {
        public void onChange();
    }

    public T addChangeListener(ChangeListener listener);

    public T watch(String relativePath);

    public T stop();
}
