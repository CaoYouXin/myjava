package toonly.repos;

import toonly.appobj.AppFactory;
import toonly.dbmanager.repos.Program;
import toonly.dbmanager.repos.Updatable;
import toonly.debugger.BugReporter;
import toonly.wrapper.SW;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by cls on 15-3-18.
 */
public class ReposManager {
    private static ReposManager ourInstance = new ReposManager();

    public static ReposManager getInstance() {
        return ourInstance;
    }

    private ReposManager() {
    }

    private final AtomicBoolean isUpToDate = new AtomicBoolean(false);

    public boolean isUpToDate() {
        if (this.isUpToDate.get())
            return true;

        synchronized (this) {
            if (this.isUpToDate.get())
                return true;

            if (!Program.INSTANCE.isRegistered())
                return false;

            SW<Boolean> bool = new SW<>(true);
            AppFactory.instance.forEach((appClass) -> {
                if (!bool.val()) {
                    return;
                }

                Object app = null;
                try {
                    app = appClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    BugReporter.reportBug(this, "在检查软件库的时候，不能创建app对象", e);
                }
                if (app instanceof Updatable) {
                    Updatable updatable = (Updatable) app;
                    if (updatable.needUpdateDDL()) {
                        bool.val(false);
                    }
                }
            });

            if (bool.val()) {
                this.isUpToDate.set(true);
                return true;
            }
            return false;
        }
    }

    public synchronized boolean makeUpToDate() {
        if (this.isUpToDate.get())
            return true;

        SW<Boolean> bool = new SW<>(true);
        if (!Program.INSTANCE.isRegistered())
            bool.val(Program.INSTANCE.register());

        if (!bool.val())
            return false;

        AppFactory.instance.forEach((appClass) -> {
            if (!bool.val()) {
                return;
            }

            Object app = null;
            try {
                app = appClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                BugReporter.reportBug(this, "在检查软件库的时候，不能创建app对象", e);
            }
            if (app instanceof Updatable) {
                Updatable updatable = (Updatable) app;
                bool.val(updatable.updateDDL());
            }
        });
        return bool.val();
    }

    public void needCheck() {
        this.isUpToDate.set(false);
    }
}
