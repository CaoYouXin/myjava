package toonly.dbmanager.repos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.configer.PropsConfiger;
import toonly.dbmanager.base.*;
import toonly.dbmanager.lowlevel.DT;
import toonly.dbmanager.lowlevel.RS;

import java.util.Properties;

/**
 * Created by cls on 15-3-14.
 */
public class Program implements Addable, Modable, Delable, Selable, Creatable {

    public static final Program INSTANCE = new Program();

    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);
    private static final String CONFIG_FILE_NAME = "program.repo";
    private Properties config = new PropsConfiger().cache(CONFIG_FILE_NAME);
    @Column
    @KeyColumn
    @DT(type = DT.Type.shorttext)
    private String name = this.config.getProperty("name", "test");
    @Column
    @DuplicatedColumn
    @DT(type = DT.Type.integer)
    private int version = Integer.valueOf(this.config.getProperty("version", "0"));
    private boolean isRegistered = false;
    private Status status = null;

    private Program() {
    }

    @Override
    public String getSchemaName() {
        return RepoConsts.REPO_DB;
    }

    @Override
    public String getTableName() {
        return RepoConsts.PROGRAM_TB;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public boolean register() {
        if (null == this.status) {
            this.isRegistered();
        }

        if (this.isRegistered) {
            return true;
        }

        switch (this.status) {
            case NO_REPO_DB:
                this.createDatabase();
            case NO_PROGRAM_TB:
                this.createTable();
            case NO_PROGRAM_RD:
                this.add();
                break;
            case UN_UPDATE_VS:
                this.addForDuplicated();
                break;
            default:
                return false;
        }
        this.isRegistered = true;
        return true;
    }

    public boolean isRegistered() {
        if (this.isRegistered) {
            return true;
        }

        if (!this.isDatabaseExist()) {
            this.status = Status.NO_REPO_DB;
            return false;
        }

        if (!this.isTableExist()) {
            this.status = Status.NO_PROGRAM_TB;
            return false;
        }

        RS rs = this.keySelect();
        while (rs.next()) {
            int currentVersion = rs.getInt("version");
            if (currentVersion < this.version) {
                this.status = Status.UN_UPDATE_VS;
                return false;
            } else {
                this.isRegistered = true;
                return true;
            }
        }
        this.status = Status.NO_PROGRAM_RD;
        return false;
    }

    public static enum Status {
        NO_REPO_DB, NO_PROGRAM_TB, NO_PROGRAM_RD, UN_UPDATE_VS
    }

}
