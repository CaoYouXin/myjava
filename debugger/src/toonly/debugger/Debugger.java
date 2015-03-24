package toonly.debugger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class Debugger {

    private static final Logger LOGGER = LoggerFactory.getLogger(Debugger.class);

    private Debugger() {
    }

    public static final void debugExRun(Object invoker, ExRunnable r) {
        debugByName(invoker.getClass().getName(), null, r);
    }

    public static final void debugExRun(Class<?> invoker, ExRunnable r) {
        debugByName(invoker.getName(), null, r);
    }

    public static final void debugRun(Object invoker, Runnable r) {
        debugByName(invoker.getClass().getName(), r, null);
    }

    public static final void debugRun(Class<?> invoker, Runnable r) {
        debugByName(invoker.getName(), r, null);
    }

    private static final void debugByName(String invokerName, Runnable r, ExRunnable er) {
        if (Feature.LOW_LEVEL_FLAG.isOn()) {
            LOGGER.info("Invoker Name : {}", invokerName);
        }

        if (Feature.SIMPLE_MODE.isOn() && Feature.SIMPLE_MODE_RULE.isOn()) {
            debug(r, er);
            return;
        }

        if (RuleConfiger.INSTANCE.applyRule(invokerName)) {
            debug(r, er);
        }
    }

    private static void debug(Runnable r, ExRunnable er) {
        if (null != r) {
            r.run();
            return;
        }
        if (null != er) {
            try {
                er.exRun();
            } catch (Exception e) {
                LOGGER.info("Error[{}] occur : {}", e.getClass().getName(), e.getLocalizedMessage());
            }
        }
    }

    public static interface ExRunnable<E extends Exception> {
        void exRun() throws E;
    }

}
