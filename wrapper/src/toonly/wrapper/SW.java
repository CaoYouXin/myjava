package toonly.wrapper;

import java.util.Objects;

/**
 * Created by caoyouxin on 15-2-21.
 */
public class SW<T> {

    private T source;

    public SW(T source) {
        this.source = source;
    }

    public SW() {
    }

    public SW val(T source) {
        this.source = source;
        return this;
    }

    public T val() {
        return this.source;
    }

    public boolean isNull() {
        return Objects.isNull(this.source);
    }

}
