package toonly.dbmanager.base;

/**
 * Created by cls on 15-3-27.
 */
public enum Mstr {

    ADD("add"),
    ADD_FOR_DUPLICATED("addForDuplicated"),
    DELETE("delete"),
    MODIFY("modify"),
    SELECT("select"),
    KEY_SELECT("keySelect"),
    FILTER_SELECT("filterSelect");

    private String val;

    Mstr(String val) {
        this.val = val;
    }
    public String val() {
        return this.val;
    }
}
