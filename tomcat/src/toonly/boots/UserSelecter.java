package toonly.boots;

import com.sun.istack.internal.NotNull;
import toonly.configer.PropsConfiger;
import toonly.configer.cache.UncachedException;
import toonly.dbmanager.lowlevel.DB;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.permission.P;
import toonly.dbmanager.repos.Updatable;
import toonly.dbmanager.sqlbuilder.*;

import java.util.Properties;

/**
 * Created by cls on 15-3-13.
 */
public class UserSelecter {

    static class Ret {
        boolean needInit;
        boolean suc;
        String permission;

        public Ret(boolean needInit, boolean suc, String permission) {
            this.needInit = needInit;
            this.suc = suc;
            this.permission = permission;
        }
    }

    private static final Properties _configs = getConfigs();

    private static Properties getConfigs() {
        PropsConfiger propsConfiger = new PropsConfiger();
        try {
            return propsConfiger.cache("user.cfg");
        } catch (UncachedException e) {
            return propsConfiger.config("user.cfg");
        }
    }

    private static final String _username = _configs.getProperty("username", "username");
    private static final String _password = _configs.getProperty("password", "password");
    private static final String _permission = _configs.getProperty("permission", "permission");
    private static final String _userDb = _configs.getProperty("schema", "userdb");
    private static final String _userTable = _configs.getProperty("table", "user");

    public static Ret check(@NotNull String username, @NotNull String password) {
        Updatable updatable = new Updatable() {
            @Override
            public int getVersion() {
                return 0;
            }

            @Override
            public String getSchemaName() {
                return _userDb;
            }

            @Override
            public String getTableName() {
                return _userTable;
            }
        };
        if (updatable.needUpdateDDL())
            return new Ret(true, false, P.NULL);

        TableId tableId = new TableId(_userDb, _userTable);
        PreparedSQL select = new Select(tableId, _username, _password, _permission)
                .where(new Where(new Equal(tableId, _username)));
        RS rs = DB.instance().preparedQuery(select.toPreparedSql(), username);
        while (rs.next()) {
            if (password.equals(rs.getString(_password))) {
                return new Ret(false, true, rs.getString(_permission));
            }
        }
        return new Ret(false, false, P.NULL);
    }

}
