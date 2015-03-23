package toonly.dbmanager.repos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import toonly.dbmanager.base.Creatable;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.permission.PofM;

/**
 * Created by cls on 15-3-15.
 */
public interface Updatable extends Creatable {

    @JsonIgnore int getVersion();

    @PofM(who = "S")
    default boolean needUpdateDDL() {
        if (!this.isDatabaseExist())
            return true;

        RepoInfo repoInfo = new RepoInfo();
        repoInfo.setProgram(Program.INSTANCE.getName());
        repoInfo.setDb(this.getSchemaName());
        repoInfo.setTable(this.getTableName());
        repoInfo.setVersion(this.getVersion());

        if (!repoInfo.isDatabaseExist())
            repoInfo.createDatabase();

        if (!repoInfo.isTableExist())
            repoInfo.createTable();

        if (!this.isTableExist())
            return true;


        RS rs = repoInfo.keySelect();
        while (rs.next()) {
            return this.getVersion() > rs.getInt("version");
        }

        return true;
    }

    @PofM(who = "S")
    default boolean updateDDL() {
        if (!this.isDatabaseExist())
            this.createDatabase();

        RepoInfo repoInfo = new RepoInfo();
        repoInfo.setProgram(Program.INSTANCE.getName());
        repoInfo.setDb(this.getSchemaName());
        repoInfo.setTable(this.getTableName());
        repoInfo.setVersion(this.getVersion());

        if (!repoInfo.isDatabaseExist())
            repoInfo.createDatabase();

        if (!repoInfo.isTableExist())
            repoInfo.createTable();

        if (!this.isTableExist())
            return this.createTable() && repoInfo.addForDuplicated();


        RS rs = repoInfo.keySelect();
        while (rs.next()) {
            if (this.getVersion() <= rs.getInt("version"))
                return true;
        }

        return this.reCreateTable() && repoInfo.addForDuplicated();
    }

}
