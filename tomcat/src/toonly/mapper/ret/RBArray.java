package toonly.mapper.ret;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by cls on 15-3-13.
 */
public class RBArray {

    private List<RB> list;

    public RBArray add(RB rb) {
        if (null == this.list) {
            this.list = new ArrayList<>();
        }

        if (this.list.add(rb))
            return this;
        return null;
    }

    public void forEach(Consumer<RB> consumer) {
        this.list.forEach(consumer);
    }

}
