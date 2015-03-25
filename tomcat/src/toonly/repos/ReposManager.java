package toonly.repos;

import toonly.appobj.AppFactory;
import toonly.dbmanager.repos.Program;
import toonly.dbmanager.repos.Updatable;
import toonly.debugger.BugReporter;
import toonly.wrapper.SW;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * Created by cls on 15-3-18.
 */
public class ReposManager {

    public static final ReposManager INSTANCE = new ReposManager();

    private static final String MSG = "孤正在检查软件库的时候，不能创建app对象";

    private final AtomicBoolean isUpToDate = new AtomicBoolean(false);

    private ReposManager() {
    }

    public boolean isUpToDate() throws Exception {
        if (this.isUpToDate.get()) {
            return true;
        }

        return isUpToDateSynCheck();
    }

    private synchronized boolean isUpToDateSynCheck() throws Exception {
        if (this.isUpToDate.get()) {
            return true;
        }

        if (!Program.INSTANCE.isRegistered()) {
            return false;
        }

        SW<Boolean> bool = new SW<>(true);
        SW<Exception> e = new SW<>();
        AppFactory.INSTANCE.forEach(appClass -> {
            if (!bool.val()) {
                return;
            }

            Object app = AppFactory.INSTANCE.getAppObject(appClass);
            if (app instanceof Updatable) {
                Object ret = AppFactory.INSTANCE.invokeMethod(null, app, "needUpdateDDL");
                this.handleRetInner(bool, e, ret);
            }
        });

        return this.handleRetOuter(e, () -> returnByBool(bool));
    }

    private boolean returnByBool(SW<Boolean> bool) {
        if (bool.val()) {
            this.isUpToDate.set(true);
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean makeUpToDate(String username) throws Exception {
        if (this.isUpToDate.get()) {
            return true;
        }

        SW<Boolean> bool = new SW<>(true);
        if (!Program.INSTANCE.isRegistered()) {
            bool.val(Program.INSTANCE.register());
        }

        if (!bool.val()) {
            return false;
        }

        SW<Exception> e = new SW<>();
        AppFactory.INSTANCE.forEach(appClass -> {
            if (!bool.val()) {
                return;
            }

            Object app = AppFactory.INSTANCE.getAppObject(appClass);
            if (app instanceof Updatable) {
                Object ret = AppFactory.INSTANCE.invokeMethod(username, app, "updateDDL");
                this.handleRetInner(bool, e, ret);
            }
        });

        return handleRetOuter(e, bool::val);
    }

    private void handleRetInner(SW<Boolean> bool, SW<Exception> e, Object ret) {
        if (ret instanceof Boolean) {
            bool.val((Boolean) ret);
        } else {
            e.val((Exception) ret);
            bool.val(false);
        }
    }

    private boolean handleRetOuter(SW<Exception> e, Supplier<Boolean> fn) throws Exception {
        if (e.isNull()) {
            return fn.get();
        } else {
            BugReporter.reportBug(this, MSG, e.val());
            throw e.val();
        }
    }

    public void needCheck() {
        this.isUpToDate.set(false);
    }
}
