package toonly.debugger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final String CONFIG_FILE_NAME = "debugger.prop";
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

        Properties props = this.watch(CONFIG_FILE_NAME).AddChangeListener(this).cache(CONFIG_FILE_NAME);

        _ruleListTree = new RuleListTreeNode("default", Feature.DEFAULT_RULE.isOn());

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

    @Override
    public void onChange() {
        Properties props = this.config(CONFIG_FILE_NAME);

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
