package toonly.debugger;

import toonly.configer.PropsConfiger;
import toonly.configer.cache.UncachedException;
import toonly.configer.watcher.ChangeWatcher;
import toonly.wrapper.Bool;

import java.util.Properties;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class RuleConfiger extends PropsConfiger implements ChangeWatcher.ChangeListener {

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
        this.watch("debugger.prop").AddChangeListener(this);
        try {
            return this.cache("debugger.prop");
        } catch (UncachedException e) {
            return this.config("debugger.prop");
        }
    }

    @Override
    public void onChange() {
        Properties props = this.config("debugger.prop");

        RuleListTreeNode aDefault = new RuleListTreeNode("default", Feature.DEFAULT_RULE.isOn());

        props.forEach((invokerName, isDebugging) -> {
            RuleListTreeNode tmp = aDefault;
            String invoker = invokerName.toString();
            for (String name : invoker.split("\\.")) {
                tmp = tmp.getOrAdd(name);
            }
            tmp.val(Bool.val((String) isDebugging).val());
        });

        _ruleListTree = aDefault;
        _ruleListTree.print(0);
    }
}
