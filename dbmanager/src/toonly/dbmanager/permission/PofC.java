package toonly.dbmanager.permission;

import toonly.dbmanager.base.Mstr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by cls on 15-3-20.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PofC {
    Mstr method();
    String who();
}
