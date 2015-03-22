package toonly.debugger;

import toonly.configer.PropsConfiger;
import toonly.configer.cache.UncachedException;
import toonly.wrapper.Bool;

import java.util.Properties;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class RuleConfiger extends PropsConfiger {

    public static final RuleConfiger val = new RuleConfiger();

    private RuleListTreeNode _ruleListTree;

    public boolean applyRule(String invokerName) {
        if (null == _ruleListTree) {
            init();
        }

        RuleListTreeNode tmp = _ruleListTree;
        String invoker = invokerName.toString();
        for (String name : invoker.split("\\.")) {
            tmp = tmp.get(name);
            if (null == tmp) {
                return _ruleListTree.val();
            }
        }

        return tmp.val();
    }

    private synchronized void init() {
        if (null != _ruleListTree) {
            return;
        }

        _ruleListTree = new RuleListTreeNode("default", Feature.DEFAULT_RULE.isOn());

        Properties props = this.get();

        props.forEach((invokerName, isDebugging) -> {
            RuleListTreeNode tmp = _ruleListTree;
            String invoker = invokerName.toString();
            for (String name : invoker.split("\\.")) {
                tmp = tmp.getOrAdd(name);
            }
            tmp.val(Bool.val((String) isDebugging).val());
        });

        _ruleListTree.print(0);
    }

    private Properties get() {
        try {
            return this.cache("debugger.prop");
        } catch (UncachedException e) {
            return this.config("debugger.prop");
        }
    }
}
