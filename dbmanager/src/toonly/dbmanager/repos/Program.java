package toonly.dbmanager.repos;

import toonly.configer.PropsConfiger;
import toonly.configer.cache.UncachedException;
import toonly.dbmanager.base.*;
import toonly.dbmanager.lowlevel.DT;
import toonly.dbmanager.lowlevel.RS;

import java.util.Properties;

/**
 * Created by cls on 15-3-14.
 */
public class Program implements Addable, Modable, Delable, Selable, Creatable {

    public static final Program instance = new Program();

    private Program() {}

    @Override
    public String getSchemaName() {
        return RepoConsts.REPO_DB;
    }

    @Override
    public String getTableName() {
        return RepoConsts.PROGRAM_TB;
    }

    private Properties config = this.getConfig();

    private Properties getConfig() {
        PropsConfiger propsConfiger = new PropsConfiger();
        try {
            return propsConfiger.cache("program.repo");
        } catch (UncachedException e) {
            return propsConfiger.config("program.repo");
        }
    }

    @Column @KeyColumn @DT(type = DT.Type.shorttext)
    private String name = this.config.getProperty("name", "test");
    @Column @DuplicatedColumn @DT(type = DT.Type.integer)
    private int version = Integer.valueOf(this.config.getProperty("version", "0"));

    public String getName() {
        return name;
    }

    private boolean isRegistered = false;
    private Status status = null;

    public Status getStatus() {
        return status;
    }

    public boolean register() {
        if (null == this.status) this.isRegistered();

        if (this.isRegistered) return true;

        switch (this.status) {
            case NoRepoDB:
                this.createDatabase();
            case NoProgTb:
                this.createTable();
            case NoRrogRd:
                this.add();
                break;
            case UnUpdtVs:
                this.addForDuplicated();
                break;
            default:
                return false;
        }
        this.isRegistered = true;
        return true;
    }

    public boolean isRegistered() {
        if (this.isRegistered) return true;

        if (!this.isDatabaseExist()) {
            this.status = Status.NoRepoDB;
            return false;
        }

        if (!this.isTableExist()) {
            this.status = Status.NoProgTb;
            return false;
        }

        RS rs = this.keySelect();
        while (rs.next()) {
            int currentVersion = rs.getInt("version");
            if (currentVersion < this.version) {
                this.status = Status.UnUpdtVs;
                return false;
            } else {
                this.isRegistered = true;
                return true;
            }
        }
        this.status = Status.NoRrogRd;
        return false;
    }

    public static enum Status {
        NoRepoDB, NoProgTb, NoRrogRd, UnUpdtVs
    }

}
