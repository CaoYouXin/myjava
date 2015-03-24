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
        INTEGER("int(11)"),
        LONG("bigint(20)"),
        SHORTTEXT("varchar(100)"),
        LONGTEXT("varchar(1000)"),
        BOOLEAN("bit(1)"),
        DATETIME("datetime");

        private String type;

        private Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

}
