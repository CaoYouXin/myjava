package toonly.dbmanager.repos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import toonly.dbmanager.base.Creatable;
import toonly.dbmanager.lowlevel.RS;
import toonly.dbmanager.permission.PofM;

import static toonly.dbmanager.repos.RepoInfo.VERSION_COLUMN;

/**
 * Created by cls on 15-3-15.
 */
public interface Updatable extends Creatable {

    @JsonIgnore
    int getVersion();

    default boolean needUpdateDDL() {
        if (this.ifCreateNeed()) {
            return true;
        }

        RepoInfo repoInfo = getRepoInfo();

        RS rs = repoInfo.keySelect();
        if (rs.next()) {
            return this.getVersion() > rs.getInt(VERSION_COLUMN);
        }

        return true;
    }

    @PofM(who = "S")
    default boolean updateDDL() {
        RepoInfo repoInfo = getRepoInfo();

        if (this.ifCreateNeed()) {
            return this.createIfNeed() && repoInfo.addForDuplicated();
        }

        RS rs = repoInfo.keySelect();
        while (rs.next()) {
            if (this.getVersion() <= rs.getInt(VERSION_COLUMN)) {
                return true;
            }
        }

        return this.reCreateTable() && repoInfo.addForDuplicated();
    }

    default RepoInfo getRepoInfo() {
        RepoInfo repoInfo = new RepoInfo();
        repoInfo.setProgram(Program.INSTANCE.getName());
        repoInfo.setDb(this.getSchemaName());
        repoInfo.setTable(this.getTableName());
        repoInfo.setVersion(this.getVersion());
        repoInfo.createIfNeed();
        return repoInfo;
    }

}
