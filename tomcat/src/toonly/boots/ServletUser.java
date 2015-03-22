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

    private static final Map<String, LoginUser> _NAME_2_USER = new HashMap<>();

    public static boolean _login(String username, String permission) {
        _NAME_2_USER.put(username, new LoginUser(permission, LocalDateTime.now()));
        return true;
    }

    private static boolean _logout(String username) {
        return null != _NAME_2_USER.remove(username);
    }

    public static String _getPermission(String username) {
        LoginUser loginUser = _NAME_2_USER.get(username);
        if (null != loginUser) {
            return loginUser._permission;
        }
        return P.NULL;
    }

    private static final Logger log = LoggerFactory.getLogger(ServletUser.class);

    private boolean _isNormalRequest;
    private boolean _needInit;
    private String _username;

    public ServletUser(ServletRequest source) {
        super(source);
        init();
    }

    private void init() {

        Object username = this.val().getParameter("un");
        if (null == username) {
            this._isNormalRequest = true;
            return;
        }
        this._isNormalRequest = false;
        this._username = username.toString();

    }

    boolean login() {
        Object password = this.val().getParameter("pwd");
        Debugger.debugRun(this, () -> log.info("un : {} ; pwd : {}", this._username, Objects.toString(password)));
        if (null == password) {
            NullPointerException e = new NullPointerException();
            BugReporter.reportBug(this, "登录不发密码……真是醉了", e);
            return false;
        }

        UserSelecter.Ret ret = UserSelecter.check(this._username, password.toString());
        if (ret.needInit) {
            this._needInit = true;
            return false;
        }

        if (ret.suc)
            return _login(this._username, ret.permission);
        else
            _logout(this._username);
        return false;
    }

    boolean logout() {
        return _logout(this._username);
    }

    boolean isNormalRequest() {
        return this._isNormalRequest;
    }

    boolean isLogin() {
        return this.is((loginUser) -> true);
    }

    boolean isAdmin() {
        return this.is((loginUser) -> P.S == loginUser._permission);
    }

    private boolean is(Function<LoginUser, Boolean> fn) {
        LoginUser loginUser = _NAME_2_USER.get(this._username);
        if (null == loginUser) {
            Debugger.debugRun(this, () -> log.info("never login."));
            return false;
        }

        if (loginUser.update()) {
            return fn.apply(loginUser);
        } else {
            _logout(this._username);
            Debugger.debugRun(this, () -> log.info("has logout."));
            return false;
        }
    }

    Object getUserName() {
        SW<String> username = new SW<>("");
        Debugger.debugRun(this, () -> username.val("test"));
        return Objects.isNull(this._username) ? username.val() : this._username;
    }

    public boolean isNeedInit() {
        return this._needInit;
    }

    private static class LoginUser {

        private String _permission;
        private LocalDateTime _lastUpdate;

        public LoginUser(String _permission, LocalDateTime _lastUpdate) {
            this._permission = _permission;
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
