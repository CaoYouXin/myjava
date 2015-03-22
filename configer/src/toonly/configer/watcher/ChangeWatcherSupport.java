package toonly.configer.watcher;

import com.sun.istack.internal.NotNull;
import toonly.configer.FileTool;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by cls on 15-3-16.
 */
public class ChangeWatcherSupport<T> implements ChangeWatcher<T>, FileTool {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static final List<ChangeWatcherSupport> supports = new CopyOnWriteArrayList<>();
    private static WatchService watchService = null;

    static {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(FileTool.getConfigsPath());
            /* 注册监听器 */
            path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
//                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert watchService != null;
        final WatchService finalWatchService = watchService;
        executorService.execute(() -> {
            while (true) {
                List<WatchEvent<?>> watchEvents = null;
                try {
                    watchEvents = finalWatchService.take().pollEvents();
                } catch (InterruptedException e) {
                    break;
                } finally {
                    if (null != watchEvents) watchEvents.forEach((e) -> {
                        supports.forEach((s) -> {
                            if (s.watchingPath.equals(e.context().toString()))
                                s.fireChangeEvent();
                        });
                    });
                }
            }
        });
    }

    public static void stopWatching() {
        executorService.shutdownNow();
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ChangeListener> listenerList;

    private final T source;
    private String watchingPath;

    public ChangeWatcherSupport(T source) {
        this.source = source;
    }

    public void fireChangeEvent() {
        if (null != this.listenerList) {
            this.listenerList.forEach(ChangeListener::onChange);
        }
    }

    @Override
    public void AddChangeListener(@NotNull ChangeListener listener) {
        if (null == this.listenerList) {
            this.listenerList = new ArrayList<>();
        }
        this.listenerList.add(listener);
    }

    @Override
    public T watch(@NotNull String relativePath) {
        this.watchingPath = relativePath;
        supports.add(this);
        return this.source;
    }

    @Override
    public T stop() {
        supports.remove(this);
        return this.source;
    }

}
