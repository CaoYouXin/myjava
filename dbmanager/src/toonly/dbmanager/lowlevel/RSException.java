package toonly.dbmanager.lowlevel;

/**
 * Created by cls on 15-3-23.
 */
public class RSException extends RuntimeException {
    public RSException(String columnLabel, String type, Object obj) {
        super(String.format("[%s] is not a %s, but a %s.", columnLabel, type, null == obj ? "null" : obj.getClass().getName()));
    }
}
