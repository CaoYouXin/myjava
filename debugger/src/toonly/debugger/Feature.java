package toonly.debugger;

/**
 * Created by caoyouxin on 15-2-23.
 */
public enum Feature {

    LOW_LEVEL_FLAG,//1
    SIMPLE_MODE,//2
    SIMPLE_MODE_RULE,//4
    DEFAULT_RULE;//8

    private static volatile int _flag = 0;

    public static synchronized void set(int flag) {
        _flag = flag;
    }

    public static synchronized int get() {
        return _flag;
    }

    public boolean isOn() {
        return ((1 << ordinal()) & _flag) > 0;
    }

    public void setOn() {
        _flag |= (1 << ordinal());
    }

    public void setOff() {
        _flag &= ~(1 << ordinal());
    }

}
