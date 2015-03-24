package toonly.dbmanager.repos;

import toonly.dbmanager.base.*;
import toonly.dbmanager.lowlevel.DT;

/**
 * Created by cls on 15-3-15.
 */
public class RepoInfo implements Creatable, Addable, Selable {
    @Column
    @KeyColumn
    @DT(type = DT.Type.SHORTTEXT)
    private String program;
    @Column
    @KeyColumn
    @DT(type = DT.Type.SHORTTEXT)
    private String db;
    @Column
    @KeyColumn
    @DT(type = DT.Type.SHORTTEXT)
    private String table;
    @Column
    @DuplicatedColumn
    @DT(type = DT.Type.INTEGER)
    private int version;

    @Override
    public String getSchemaName() {
        return RepoConsts.REPO_DB;
    }

    @Override
    public String getTableName() {
        return RepoConsts.INFO_TB;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
