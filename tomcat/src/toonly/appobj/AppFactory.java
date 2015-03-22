package toonly.appobj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.boots.ServletUser;
import toonly.configer.watcher.ChangeWatcher;
import toonly.configer.PropsConfiger;
import toonly.configer.cache.UncachedException;
import toonly.dbmanager.permission.DMethod;
import toonly.dbmanager.permission.P;
import toonly.dbmanager.permission.PofC;
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

import static toonly.boots.ServletUser.*;

/**
 * Created by cls on 15-3-16.
 */
public class AppFactory implements ChangeWatcher.ChangeListener {

    private static final Logger log = LoggerFactory.getLogger(AppFactory.class);

    public static final AppFactory instance = new AppFactory();

    private final Map<String, Class<?>> map = new HashMap<>();

    private AppFactory() {
        this.putToMap();
    }

    private void putToMap() {
        this._config.forEach((key, whatever) -> this.map.put(key.toString(), this._getAppClass(key.toString())));
    }

    private static final String APP_CFG = "app.cfg";
    private final PropsConfiger propsConfiger = new PropsConfiger();
    private Properties _config = this.getConfig();

    private Properties getConfig() {
        this.propsConfiger.watch(APP_CFG).AddChangeListener(this);
        try {
            return propsConfiger.cache(APP_CFG);
        } catch (UncachedException e) {
            return propsConfiger.config(APP_CFG);
        }
    }

    public Class<?> getAppClass(String key) {
        Class<?> aClass = this.map.get(key);
        if (null != aClass) {
            return aClass;
        }

        return this._getAppClass(key);
    }

    private Class<?> _getAppClass(String key) {
        String appClassName = this._config.getProperty(key, "java.lang.Object");
        Debugger.debugRun(AppFactory.class, () -> log.info("app class name : {}", appClassName));
        try {
            return AppFactory.class.getClassLoader().loadClass(appClassName);
        } catch (ClassNotFoundException e) {
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
            return new Object();
        }
    }

    @Override
    public void onChange() {
        log.info("config [{}] has updated", APP_CFG);
        this._config = this.propsConfiger.config(APP_CFG);
        this.putToMap();
        ReposManager.getInstance().needCheck();
    }

    public void forEach(Consumer<Class<?>> classConsumer) {
        this.map.values().forEach(classConsumer);
    }

    public Object invokeMethod(String username, Object app, String methodName) {
        String userP = ServletUser._getPermission(username);
        Class<?> aClass = app.getClass();

        boolean permission = P.S.equals(userP);
        PofC pofC = aClass.getDeclaredAnnotation(PofC.class);
        if (!permission && null != pofC) {
            for (DMethod p : pofC.ps()) {
                if (p.name().equals(methodName)) {
                    permission = p.who().contains(userP);
                }
            }
        }
        try {
            Method method = aClass.getMethod(methodName);
            if (permission) {
                return method.invoke(app);
            } else {
                PofM pofM = method.getDeclaredAnnotation(PofM.class);
                permission = pofM.who().contains(userP);
                if (permission) {
                    return method.invoke(app);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            BugReporter.reportBug(this, "app["+ aClass.getName()+"] 在 执行调用 的时候，不幸遇难。", e);
        }
        if (!permission) {
            return new UnPermissioned();
        }
        return null;
    }
}
