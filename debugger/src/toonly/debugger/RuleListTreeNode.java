package toonly.debugger;

import com.sun.istack.internal.NotNull;
import toonly.configer.FileTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class RuleListTreeNode implements Comparable<RuleListTreeNode> {
    private List<RuleListTreeNode> subListTree;
    @NotNull
    private String name;
    private boolean isDebugging;

    public RuleListTreeNode(String name) {
        this.name = name;
    }

    public RuleListTreeNode(String name, boolean isDebugging) {
        this.name = name;
        this.isDebugging = isDebugging;
    }

    @Override
    public int compareTo(@NotNull RuleListTreeNode o) {
        return this.name.compareTo(o.name);
    }

    public RuleListTreeNode getOrAdd(@NotNull String name) {
        RuleListTreeNode node = new RuleListTreeNode(name);

        if (null == subListTree) {
            subListTree = new ArrayList<>();
            subListTree.add(node);
            return node;
        }

        int i = subListTree.indexOf(node);
        if (-1 != i) {
            return subListTree.get(i);
        } else {
            subListTree.add(node);
            return node;
        }
    }

    public RuleListTreeNode get(String name) {
        if (null == subListTree) {
            return null;
        }

        RuleListTreeNode node = new RuleListTreeNode(name);
        int i = subListTree.indexOf(node);
        if (-1 != i) {
            return subListTree.get(i);
        } else {
            node.name = "*";
            i = subListTree.indexOf(node);
            if (-1 != i) {
                return subListTree.get(i);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuleListTreeNode that = (RuleListTreeNode) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void val(boolean val) {
        this.isDebugging = val;
    }

    public boolean val() {
        return this.isDebugging;
    }

    @Override
    public String toString() {
        return "RuleListTreeNode{" +
                "name='" + name + '\'' +
                ", isDebugging=" + isDebugging +
                '}';
    }

    void print(StringBuilder sb, int flex) {
        for (int i = 0; i < flex; i++) {
            sb.append('\t');
        }
        sb.append(this.toString()).append(FileTool.LINE_SEPARATOR);
        if (null != subListTree) {
            subListTree.forEach(node -> node.print(sb, flex + 1));
        }
    }

}
