package toonly.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.dbmanager.base.ECCalculator;
import toonly.dbmanager.base.Entity;
import toonly.wrapper.Bool;
import toonly.wrapper.SW;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by cls on 15-3-13.
 */
public interface ParamConstructable extends Entity {

    static final Logger log = LoggerFactory.getLogger(ParamConstructable.class);

    default boolean construct(Map<String, String[]> data) {
        ECCalculator ecc = new ECCalculator(this);

        SW<Boolean> suc = new SW(true);
        ecc.dtForEach((f, dt) -> {
            Object o = null;
            switch (dt.type()) {
                case INTEGER:
                    o = asT(data, f, (str) -> Integer.valueOf(str));
                    break;
                case LONG:
                    o = asT(data, f, (str) -> Long.valueOf(str));
                    break;
                case SHORTTEXT:case LONGTEXT:
                    o = asT(data, f, (str) -> str);
                    break;
                case BOOLEAN:
                    o = asT(data, f, (str) -> Bool.val(str).val());
                    break;
                case DATETIME:
                    //TODO 还要看前端API如何
                    o = asT(data, f, (str) -> LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME));
                    break;
                default:
                    throw new RuntimeException("no such data type");
            }
            if (null != o) ecc.setValue(f, o);
            else if (suc.val()) {
                suc.val(false);
                log();
            }
        });

        return suc.val();
    }

    default <T> T asT(Map<String, String[]> data, String key, Function<String, T> fn) {
        String[] strings = data.get(key);
        if (null != strings && 0 < strings.length) {
            return fn.apply(strings[0]);
        }
        return null;
    }

    default void log() {
        log.info("cannot read from params : ");
    }

}
