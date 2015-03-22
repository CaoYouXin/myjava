package toonly.debugger;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class RuleListTreeNode implements Comparable<RuleListTreeNode> {
    private List<RuleListTreeNode> _subListTree;
    @NotNull
    private String _name;
    private boolean _isDebugging;

    @Override
    public int compareTo(@NotNull RuleListTreeNode o) {
        return this._name.compareTo(o._name);
    }

    public RuleListTreeNode(String name) {
        this._name = name;
    }

    public RuleListTreeNode(String name, boolean isDebugging) {
        this._name = name;
        this._isDebugging = isDebugging;
    }

    public RuleListTreeNode getOrAdd(@NotNull String name) {
        RuleListTreeNode node = new RuleListTreeNode(name);

        if (null == _subListTree) {
            _subListTree = new ArrayList<>();
            _subListTree.add(node);
            return node;
        }

        int i = _subListTree.indexOf(node);
        if (-1 != i) {
            return _subListTree.get(i);
        } else {
            _subListTree.add(node);
            return node;
        }
    }

    public RuleListTreeNode get(String name) {
        if (null == _subListTree) {
            return null;
        }

        RuleListTreeNode node = new RuleListTreeNode(name);
        int i = _subListTree.indexOf(node);
        if (-1 != i) {
            return _subListTree.get(i);
        } else {
            node._name = "*";
            i = _subListTree.indexOf(node);
            if (-1 != i) {
                return _subListTree.get(i);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleListTreeNode that = (RuleListTreeNode) o;

        if (!_name.equals(that._name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    public void val(boolean val) {
        this._isDebugging = val;
    }

    public boolean val() {
        return this._isDebugging;
    }

    @Override
    public String toString() {
        return "RuleListTreeNode{" +
                "_name='" + _name + '\'' +
                ", _isDebugging=" + _isDebugging +
                '}';
    }

    void print(int flex) {
        for (int i = 0; i < flex; i++) {
            System.out.print("\t");
        }
        System.out.println(this.toString());
        if (null != _subListTree) {
            _subListTree.forEach((node) -> {
                node.print(flex + 1);
            });
        }
    }

}
