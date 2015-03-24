package toonly.appobj;

/**
 * Created by cls on 15-3-24.
 */
public class InvokeAppError extends Exception {
    public InvokeAppError(String appName, String methodName) {
        super(String.format("invoke [app : %s, cmd : %s] FAIL.", appName, methodName));
    }
}
