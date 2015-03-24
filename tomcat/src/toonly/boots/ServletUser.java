package toonly.boots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.dbmanager.permission.P;
import toonly.debugger.BugReporter;
import toonly.debugger.Debugger;
import toonly.wrapper.SW;

import javax.servlet.ServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by caoyouxin on 15-2-19.
 * 代表一次用户处理的对象模型
 */
public class ServletUser extends SW<ServletRequest> {

    private static final Map<String, LoginUser> NAME_2_USER = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ServletUser.class);
    private boolean isNormalRequest;
    private boolean needInit;
    private String username;

    public ServletUser(ServletRequest source) {
        super(source);
        init();
    }

    public static boolean _login(String username, String permission) {
        NAME_2_USER.put(username, new LoginUser(permission, LocalDateTime.now()));
        return true;
    }

    private static boolean _logout(String username) {
        return null != NAME_2_USER.remove(username);
    }

    public static String _getPermission(String username) {
        LoginUser loginUser = NAME_2_USER.get(username);
        if (null != loginUser) {
            return loginUser.permission;
        }
        return P.NULL;
    }

    private void init() {

        Object username = this.val().getParameter("un");
        if (null == username) {
            this.isNormalRequest = true;
            return;
        }
        this.isNormalRequest = false;
        this.username = username.toString();

    }

    boolean login() {
        Object password = this.val().getParameter("pwd");
        Debugger.debugRun(this, () -> log.info("un : {} ; pwd : {}", this.username, Objects.toString(password)));
        if (null == password) {
            NullPointerException e = new NullPointerException();
            BugReporter.reportBug(this, "登录不发密码……真是醉了", e);
            return false;
        }

        UserSelecter.Ret ret = UserSelecter.check(this.username, password.toString());
        if (ret.needInit) {
            this.needInit = true;
            return false;
        }

        if (ret.suc)
            return _login(this.username, ret.permission);
        else
            _logout(this.username);
        return false;
    }

    boolean logout() {
        return _logout(this.username);
    }

    boolean isNormalRequest() {
        return this.isNormalRequest;
    }

    boolean isLogin() {
        return this.is(loginUser -> true);
    }

    boolean isAdmin() {
        return this.is(loginUser -> P.S == loginUser.permission);
    }

    private boolean is(Function<LoginUser, Boolean> fn) {
        LoginUser loginUser = NAME_2_USER.get(this.username);
        if (null == loginUser) {
            Debugger.debugRun(this, () -> log.info("never login."));
            return false;
        }

        if (loginUser.update()) {
            return fn.apply(loginUser);
        } else {
            _logout(this.username);
            Debugger.debugRun(this, () -> log.info("has logout."));
            return false;
        }
    }

    public Object getUserName() {
        SW<String> username = new SW<>("");
        Debugger.debugRun(this, () -> username.val("test"));
        return Objects.isNull(this.username) ? username.val() : this.username;
    }

    public boolean isNeedInit() {
        return this.needInit;
    }

    private static class LoginUser {

        private String permission;
        private LocalDateTime _lastUpdate;

        public LoginUser(String permission, LocalDateTime _lastUpdate) {
            this.permission = permission;
            this._lastUpdate = _lastUpdate;
        }

        public boolean update() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadline = this._lastUpdate.plusMinutes(30);
            Debugger.debugRun(this, () -> log.info("now [{}] vs deadline [{}]", now.toString(), deadline.toString()));
            if (deadline.isBefore(now))
                return false;
            this._lastUpdate = now;
            return true;
        }

    }
}
