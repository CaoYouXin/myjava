package toonly.boots;

import com.sun.istack.internal.NotNull;
import toonly.configer.PropsConfiger;
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

    private static final String CONFIG_FILE_NAME = "user.cfg";
    private static final Properties CONFIGS = new PropsConfiger().cache(CONFIG_FILE_NAME);
    private static final String USERNAME_KEY = "username";
    private static final String DEFAULT_USERNAME_VALUE = USERNAME_KEY;
    private static final String USERNAME_FIELD = CONFIGS.getProperty(USERNAME_KEY, DEFAULT_USERNAME_VALUE);
    private static final String PASSWORD_KEY = "password";
    private static final String DEFAULT_PASSWORD_VALUE = PASSWORD_KEY;
    private static final String PASSWORD_FIELD = CONFIGS.getProperty(PASSWORD_KEY, DEFAULT_PASSWORD_VALUE);
    private static final String PERMISSION_KEY = "permission";
    private static final String DEFAULT_PERMISSION_VALUE = PERMISSION_KEY;
    private static final String PERMISSION_FIELD = CONFIGS.getProperty(PERMISSION_KEY, DEFAULT_PERMISSION_VALUE);
    private static final String USER_DB = CONFIGS.getProperty("schema", "userdb");
    private static final String USER_TABLE = CONFIGS.getProperty("table", "user");

    private UserSelecter() {
    }

    public static Ret check(@NotNull String username, @NotNull String password) {
        Updatable updatable = new Updatable() {
            @Override
            public int getVersion() {
                return 0;
            }

            @Override
            public String getSchemaName() {
                return USER_DB;
            }

            @Override
            public String getTableName() {
                return USER_TABLE;
            }
        };

        if (updatable.needUpdateDDL()) {
            return new Ret(true, false, P.NULL);
        }

        TableId tableId = new TableId(USER_DB, USER_TABLE);
        PreparedSQL select = new Select(tableId, USERNAME_FIELD, PASSWORD_FIELD, PERMISSION_FIELD)
                .where(new Where(new Equal(tableId, USERNAME_FIELD)));
        RS rs = DB.instance().preparedQuery(select.toPreparedSql(), username);
        while (rs.next()) {
            if (password.equals(rs.getString(PASSWORD_FIELD))) {
                return new Ret(false, true, rs.getString(PERMISSION_FIELD));
            }
        }
        return new Ret(false, false, P.NULL);
    }

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

}
