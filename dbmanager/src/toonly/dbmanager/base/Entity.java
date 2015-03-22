package toonly.dbmanager.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by caoyouxin on 15-2-7.
 */
public interface Entity {

    @JsonIgnore String getSchemaName();
    @JsonIgnore String getTableName();

}
