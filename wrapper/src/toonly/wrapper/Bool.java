package toonly.wrapper;

/**
 * Created by caoyouxin on 15-2-23.
 */
public enum Bool {

    TRUE, FALSE, NULL;

    public static Bool val(String bStr) {
        Bool bool = valueOf(bStr.toUpperCase());
        if (null == bool) {
            return NULL;
        }
        return bool;
    }

    public Boolean val() {
        switch (ordinal()) {
            case 0: return true;
            case 1: return false;
            case 2: return null;
            default: return null;
        }
    }

    @Override
    public String toString() {
        switch (ordinal()) {
            case 0: return "True";
            case 1: return "False";
            case 2: return "Null";
            default: return null;
        }
    }

    public String toUpperCaseString() {
        switch (ordinal()) {
            case 0: return "TRUE";
            case 1: return "FALSE";
            case 2: return "NULL";
            default: return null;
        }
    }

    public String toLowerCaseString() {
        switch (ordinal()) {
            case 0: return "true";
            case 1: return "false";
            case 2: return "null";
            default: return null;
        }
    }

}
