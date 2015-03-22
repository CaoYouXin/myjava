package toonly.dbmanager.permission;

/**
 * Created by cls on 15-3-20.
 */
public enum DMethod {

    add,
    addForDuplicated,
    delete,
    modify,
    select,
    keySelect,
    filterSelect;

    private String who;
    public DMethod who(String who) {
        this.who = who;
        return this;
    }
    public String who() {
        return this.who;
    }
}
