package toonly.configer.watcher;

import com.sun.istack.internal.NotNull;
import toonly.configer.FileTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cls on 15-3-16.
 */
public class ChangeWatcherSupport<T> implements ChangeWatcher<T>, FileTool {

    private WatchServiceWrapper serviceWrapper = WatchServiceWrapper.INSTANCE;

    private final T source;
    private String watchingPath;
    private List<ChangeListener> listenerList;

    public ChangeWatcherSupport(T source) {
        this.source = source;
    }

    public void fireChangeEvent() {
        if (null != this.listenerList) {
            this.listenerList.forEach(ChangeListener::onChange);
        }
    }

    @Override
    public T addChangeListener(@NotNull ChangeListener listener) {
        if (null == this.listenerList) {
            this.listenerList = new ArrayList<>();
        }
        this.listenerList.add(listener);
        return this.source;
    }

    @Override
    public T watch(@NotNull String relativePath) {
        this.watchingPath = relativePath;
        serviceWrapper.add(this);
        return this.source;
    }

    @Override
    public T stop() {
        serviceWrapper.remove(this);
        return this.source;
    }

    public String getWatchingPath() {
        return watchingPath;
    }
}
