package app.entity;

import toonly.dbmanager.base.*;
import toonly.dbmanager.lowlevel.DT;
import toonly.dbmanager.repos.Updatable;
import toonly.mapper.ParamConstructable;

/**
 * Created by cls on 15-3-13.
 */
public class Goods implements Updatable, Addable, Delable, Modable, Selable, Jsonable, ParamConstructable {

    @Column @KeyColumn @DT(type = DT.Type.shorttext) private String code;
    @Column @DuplicatedColumn @DT(type = DT.Type.shorttext) private String name;
    @Column @DuplicatedColumn @DT(type = DT.Type.shorttext) private String color;
    @Column @DuplicatedColumn @DT(type = DT.Type.shorttext) private String size;

    @Override
    public String getSchemaName() {
        return "storehouse";
    }

    @Override
    public String getTableName() {
        return "goods";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
