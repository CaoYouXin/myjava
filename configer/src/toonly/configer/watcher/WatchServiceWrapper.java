package toonly.configer.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.configer.FileTool;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by cls on 15-3-22.
 */
public class WatchServiceWrapper implements FileTool {

    public static final WatchServiceWrapper instance = new WatchServiceWrapper();

    private static final Logger log = LoggerFactory.getLogger(WatchServiceWrapper.class);

    private WatchService watchService;
    private Set<WatchKey> watchKeys;
    private List<ChangeWatcherSupport> supports;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    private WatchServiceWrapper() {
        Path path = Paths.get(FileTool.getConfigsPath());
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            path.register(this.watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
//                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.watchKeys = new CopyOnWriteArraySet<>();

        this.executorService.schedule(() -> {
            while (true) {
                try {
                    this.watchKeys.add(watchService.take());
                } catch (InterruptedException e) {
                    log.info("watcher is interrupted.");
                    return;
                } catch (ClosedWatchServiceException e) {
                    log.info("maybe the watcher has stopped.");
                    return;
                }
            }
        }, 0, TimeUnit.SECONDS);

        this.supports = new CopyOnWriteArrayList<>();

        launchLoopThread();
    }

    private void launchLoopThread() {
        this.executorService.scheduleAtFixedRate(() -> {
            this.watchKeys.forEach(watchKey -> {
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                if (null != watchEvents) watchEvents.forEach((e) -> {
                    log.info("file : {} has been {}.", e.context(), e.kind());
                    supports.forEach((s) -> {
                        if (s.getWatchingPath().equals(e.context().toString()))
                            s.fireChangeEvent();
                    });
                });
            });
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void stopWatching() {
        executorService.shutdownNow();
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(ChangeWatcherSupport tChangeWatcherSupport) {
        this.supports.add(tChangeWatcherSupport);
    }

    public void remove(ChangeWatcherSupport tChangeWatcherSupport) {
        this.supports.remove(tChangeWatcherSupport);
    }
}
