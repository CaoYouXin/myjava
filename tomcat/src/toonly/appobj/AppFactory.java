package toonly.appobj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.boots.ServletUser;
import toonly.configer.PropsConfiger;
import toonly.configer.watcher.ChangeWatcher;
import toonly.dbmanager.permission.P;
import toonly.dbmanager.permission.PofC;
import toonly.dbmanager.permission.PofCs;
import toonly.dbmanager.permission.PofM;
import toonly.debugger.BugReporter;
import toonly.debugger.Debugger;
import toonly.repos.ReposManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Created by cls on 15-3-16.
 */
public class AppFactory implements ChangeWatcher.ChangeListener {

    public static final AppFactory INSTANCE = new AppFactory();

    private static final Logger LOGGER = LoggerFactory.getLogger(AppFactory.class);
    private static final String APP_CFG = "app.cfg";

    private final Map<String, Class<?>> map = new HashMap<>();
    private final PropsConfiger propsConfiger = new PropsConfiger();

    private Properties config = this.propsConfiger.watch(APP_CFG).addChangeListener(this).cache(APP_CFG);

    private AppFactory() {
        this.putToMap();
    }

    private void putToMap() {
        this.config.forEach((key, whatever) -> this.map.put(key.toString(), this.loadAppClass(key.toString())));
    }

    public Class<?> getAppClass(String key) {
        Class<?> aClass = this.map.get(key);
        if (null != aClass) {
            return aClass;
        }

        return this.loadAppClass(key);
    }

    private Class<?> loadAppClass(String key) {
        String appClassName = this.config.getProperty(key, "java.lang.Object");
        Debugger.debugRun(AppFactory.class, () -> LOGGER.info("app class name : {}", appClassName));
        try {
            return AppFactory.class.getClassLoader().loadClass(appClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.info("class not found : {}", appClassName);
            return Object.class;
        }
    }

    public Object getAppObject(String key) {
        return this.getAppObject(this.getAppClass(key));
    }

    public Object getAppObject(Class<?> aClass) {
        try {
            return aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.info("app create error : {}", aClass.getName());
            return new Object();
        }
    }

    @Override
    public void onChange() {
        LOGGER.info("config [{}] has updated", APP_CFG);
        this.config = this.propsConfiger.config(APP_CFG);
        this.putToMap();
        ReposManager.INSTANCE.needCheck();
    }

    public void forEach(Consumer<Class<?>> classConsumer) {
        this.map.values().forEach(classConsumer);
    }

    public Object invokeMethod(String username, Object app, String methodName) {
        String userP = ServletUser.getPermission(username);
        Class<?> aClass = app.getClass();

        CheckResult checkResult = checkClassPermission(methodName, userP, aClass);
        try {
            Method method = aClass.getMethod(methodName);
            if (checkResult.isChecked) {
                return this.useClassPermission(username, app, methodName, aClass, checkResult, method);
            } else {
                return this.useMethodPermission(username, app, methodName, userP, aClass, method);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            BugReporter.reportBug(this, "app[" + aClass.getName() + "] 在 执行调用[" + methodName + "] 的时候，不幸遇难。", e);
            return new InvokeAppError(aClass.getName(), methodName);
        }
    }

    private Object useClassPermission(String username, Object app, String methodName, Class<?> aClass, CheckResult checkResult, Method method) throws IllegalAccessException, InvocationTargetException {
        LOGGER.info("use class p : {}", checkResult.isPermitted);
        if (checkResult.isPermitted) {
            return method.invoke(app);
        } else {
            return new UnPermissioned(aClass.getName(), methodName, username);
        }
    }

    private Object useMethodPermission(String username, Object app, String methodName, String userP, Class<?> aClass, Method method) throws IllegalAccessException, InvocationTargetException {
        PofM pofM = method.getDeclaredAnnotation(PofM.class);
        boolean isPermitted = null == pofM || pofM.who().contains(userP);
        LOGGER.info("use method p : {}", isPermitted);
        if (isPermitted) {
            return method.invoke(app);
        } else {
            return new UnPermissioned(aClass.getName(), methodName, username);
        }
    }

    private CheckResult checkClassPermission(String methodName, String userP, Class<?> aClass) {
        boolean permission = P.S.equals(userP);

        PofCs pofCs = aClass.getDeclaredAnnotation(PofCs.class);
        if (!permission && null != pofCs) {
            for (PofC p : pofCs.value()) {
                if (p.method().val().equals(methodName)) {
                    return new CheckResult(true, p.who().contains(userP));
                }
            }
        }

        PofC pofC = aClass.getDeclaredAnnotation(PofC.class);
        if (!permission && null != pofC && pofC.method().val().equals(methodName)) {
            return new CheckResult(true, pofC.who().contains(userP));
        }

        if (permission) {
            return new CheckResult(true, true);
        } else {
            return new CheckResult(false);
        }
    }

    private static class CheckResult {
        private boolean isChecked;
        private boolean isPermitted;

        public CheckResult(boolean isChecked, boolean isPermitted) {
            this.isChecked = isChecked;
            this.isPermitted = isPermitted;
        }

        public CheckResult(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }

}
