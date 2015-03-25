package toonly.debugger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toonly.configer.FileTool;
import toonly.configer.PropsConfiger;
import toonly.configer.watcher.ChangeWatcher;
import toonly.wrapper.Bool;

import java.util.Properties;

/**
 * Created by caoyouxin on 15-2-23.
 */
public class RuleConfiger extends PropsConfiger implements ChangeWatcher.ChangeListener {

    public static final RuleConfiger INSTANCE = new RuleConfiger();

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleConfiger.class);
    private static final String CONFIG_FILE_NAME = "debugger.prop";
    private static final String DEFAULT_NODE_NAME = "default";

    private RuleListTreeNode ruleListTreeNode;

    public boolean applyRule(String invokerName) {
        if (null == ruleListTreeNode) {
            init();
        }

        RuleListTreeNode tmp = ruleListTreeNode;
        String invoker = invokerName.toString();
        for (String name : invoker.split("\\.")) {
            tmp = tmp.get(name);
            if (null == tmp) {
                return ruleListTreeNode.val();
            }
        }

        return tmp.val();
    }

    private synchronized void init() {
        if (null != ruleListTreeNode) {
            return;
        }

        Properties props = this.watch(CONFIG_FILE_NAME).addChangeListener(this).cache(CONFIG_FILE_NAME);

        ruleListTreeNode = new RuleListTreeNode(DEFAULT_NODE_NAME, Feature.DEFAULT_RULE.isOn());

        props.forEach((invokerName, isDebugging) -> {
            RuleListTreeNode tmp = ruleListTreeNode;
            String invoker = invokerName.toString();
            for (String name : invoker.split("\\.")) {
                tmp = tmp.getOrAdd(name);
            }
            tmp.val(Bool.val((String) isDebugging).val());
        });

        StringBuilder sb = new StringBuilder(FileTool.LINE_SEPARATOR);
        ruleListTreeNode.print(sb, 0);
        LOGGER.info(sb.toString());
    }

    @Override
    public void onChange() {
        Properties props = this.config(CONFIG_FILE_NAME);

        RuleListTreeNode aDefault = new RuleListTreeNode(DEFAULT_NODE_NAME, Feature.DEFAULT_RULE.isOn());

        props.forEach((invokerName, isDebugging) -> {
            RuleListTreeNode tmp = aDefault;
            String invoker = invokerName.toString();
            for (String name : invoker.split("\\.")) {
                tmp = tmp.getOrAdd(name);
            }
            tmp.val(Bool.val((String) isDebugging).val());
        });

        ruleListTreeNode = aDefault;
        StringBuilder sb = new StringBuilder(FileTool.LINE_SEPARATOR);
        ruleListTreeNode.print(sb, 0);
        LOGGER.info(sb.toString());
    }
}
