package toonly.dbmanager.lowlevel;

import com.sun.istack.internal.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cls on 15-3-15.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DT {

    @NotNull Type type();

    public static enum Type {
        integer("int(11)"),
        bitint("bigint(20)"),
        shorttext("varchar(100)"),
        longtext("varchar(1000)"),
        bool("bit(1)"),
        datetime("datetime");

        private String type;

        private Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

}
