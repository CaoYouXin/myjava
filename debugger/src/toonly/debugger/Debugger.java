package toonly.debugger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class Debugger {

    private static final Logger log = LoggerFactory.getLogger(Debugger.class);

    public static interface ExRunnable<E extends Exception> {
        void exRun() throws E;
    }

    public static final void debugExRun(Object invoker, ExRunnable r) {
        _debugRun(invoker.getClass().getName(), r);
    }

    public static final void debugExRun(Class<?> invoker, ExRunnable r) {
        _debugRun(invoker.getName(), r);
    }

    private static final void _debugRun(String invokerName, ExRunnable r) {
        if (Feature.LOW_LEVEL_FLAG.isOn())
            log.info("Invoker Name : {}", invokerName);

        if (Feature.SIMPLE_MODE.isOn() && Feature.SIMPLE_MODE_RULE.isOn()) {
            _exRun(r);
            return;
        }

        if (RuleConfiger.INSTANCE.applyRule(invokerName))
            _exRun(r);
    }

    private static final void _exRun(ExRunnable r) {
        try {
            r.exRun();
        } catch (Exception e) {
            log.info("Error occur : {}", e.getMessage());
        }
    }

    public static final void debugRun(Object invoker, Runnable r) {
        _debugRun(invoker.getClass().getName(), r);
    }

    public static final void debugRun(Class<?> invoker, Runnable r) {
        _debugRun(invoker.getName(), r);
    }

    private static final void _debugRun(String invokerName, Runnable r) {
        if (Feature.LOW_LEVEL_FLAG.isOn())
            log.info("Invoker Name : {}", invokerName);

        if (Feature.SIMPLE_MODE.isOn() && Feature.SIMPLE_MODE_RULE.isOn()) {
            r.run();
            return;
        }

        if (RuleConfiger.INSTANCE.applyRule(invokerName))
            r.run();
    }

}
