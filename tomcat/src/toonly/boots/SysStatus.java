package toonly.boots;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by caoyouxin on 15-2-25.
 */
public class SysStatus {

    private static final AtomicBoolean _IS_DEBUGGING = new AtomicBoolean(false);

    public static final boolean isDebugging() {
        return _IS_DEBUGGING.get();
    }

    public static final boolean setDebugging(boolean isDebugging) {
        return _IS_DEBUGGING.compareAndSet(!isDebugging, isDebugging);
    }

}
