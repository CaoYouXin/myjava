package toonly.appobj;

/**
 * Created by cls on 15-3-21.
 */
public class UnPermissioned extends Exception {
    public UnPermissioned(String appName, String methodName, String username) {
        super(String.format("[app : %s, cmd : %s] by [user : %s] FAIL.", appName, methodName, username));
    }
}
