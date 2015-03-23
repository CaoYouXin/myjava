package toonly.boots;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSelecter.class);
    private static final String CONFIG_FILE_NAME = "user.cfg";
    private static final Properties CONFIGS = getConfigs();
    private static final String USERNAME_FIELD = CONFIGS.getProperty("username", "username");
    private static final String PASSWORD_FIELD = CONFIGS.getProperty("password", "password");
    private static final String PERMISSION_FIELD = CONFIGS.getProperty("permission", "permission");
    private static final String USER_DB = CONFIGS.getProperty("schema", "userdb");
    private static final String USER_TABLE = CONFIGS.getProperty("table", "user");

    private UserSelecter() {
    }

    private static Properties getConfigs() {
        PropsConfiger propsConfiger = new PropsConfiger();
        try {
            return propsConfiger.cache(CONFIG_FILE_NAME);
        } catch (UncachedException e) {
            LOGGER.info("file[{}] not cached.", CONFIG_FILE_NAME);
            return propsConfiger.config(CONFIG_FILE_NAME);
        }
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
