package com.leroymerlin.commit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Damien Arrachequesne
 */
public class CommitPanel {

    private static final Logger logger = Logger.getLogger(CustomConfigurable.class.getName());
    private static final Map<ChangeType, String> DEFAULT_LONG_DESCRIPTION_CONTENT_MAP;
    private JPanel mainPanel;
    private JComboBox changeScope;
    private JTextField shortDescription;
    private JTextArea longDescription;
    private JTextArea sourceKeyword;
    private JTextArea breakingChanges;
    private JLabel breakingChangesHelp;
    private JRadioButton featRadioButton;
    private JRadioButton fixRadioButton;
    private JRadioButton docsRadioButton;
    private JRadioButton styleRadioButton;
    private JRadioButton refactorRadioButton;
    private JRadioButton perfRadioButton;
    private JRadioButton testRadioButton;
    private JRadioButton buildRadioButton;
    private JRadioButton choreRadioButton;
    private JRadioButton revertRadioButton;
    private ButtonGroup changeTypeGroup;

    static {
        DEFAULT_LONG_DESCRIPTION_CONTENT_MAP = new HashMap<>();
        DEFAULT_LONG_DESCRIPTION_CONTENT_MAP.put(ChangeType.FIX, "问题原因：\n解决方案：");
        DEFAULT_LONG_DESCRIPTION_CONTENT_MAP.put(ChangeType.REVERT, "This reverts commit xxx");
    }

    CommitPanel(Project project) {
        File workingDirectory = VfsUtil.virtualToIoFile(project.getBaseDir());
        Command.Result gitUserEmailGetCmdResult = new Command(workingDirectory, "git config user.email").execute();
        if (!gitUserEmailGetCmdResult.isSuccess()) {
            logger.warning("failed to get git user email!");
            return;
        }
        String gitUserEmail = gitUserEmailGetCmdResult.getOutput().get(0);
        logger.info("Git用户邮箱：" + gitUserEmail);
        String gitLogCmd = "git log --author=" + gitUserEmail + " --format=%s | grep -Eo '^[a-z]+(\\(.*\\)):.*$' | sed 's/^.*(\\(.*\\)):.*$/\\1/' | sort -n | uniq";
        Command.Result result = new Command(workingDirectory, gitLogCmd).execute();
        if (result.isSuccess()) {
            logger.info("get git change scope");
            result.getOutput().forEach(changeScope::addItem);
        }
        breakingChangesHelp.setToolTipText("重大更改，当前代码与上一个版本不兼容。" +
                "<br>比如：<br>基础业务模型变更<br>公共模块的接口变更<br>添加过时标记（@Deprecated）");
        initRadioButtonActionListener();
    }

    private void initRadioButtonActionListener() {
        featRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        fixRadioButton.addActionListener(getCommonRadioButtonActionListener(DEFAULT_LONG_DESCRIPTION_CONTENT_MAP.get(ChangeType.FIX)));
        docsRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        styleRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        refactorRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        perfRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        testRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        perfRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        buildRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        choreRadioButton.addActionListener(getCommonRadioButtonActionListener(""));
        revertRadioButton.addActionListener(getCommonRadioButtonActionListener(DEFAULT_LONG_DESCRIPTION_CONTENT_MAP.get(ChangeType.REVERT)));
    }

    private ActionListener getCommonRadioButtonActionListener(final String defaultDescriptionText) {
        /*
          如果defaultDescriptionText为空
              如果描述内容为空，则return
              如果描述内容不为空
                  如果描述内容与默认值相同，则将描述内容设为空
          如果defaultDescriptionText不为空
              如果描述内容为空，则将描述内容设为defaultDescriptionText
              如果描述内容不为空
                  如果描述内容是类型描述的默认值，则将描述内容设为defaultDescriptionText
         */
        return e -> {
            if (StringUtils.isEmpty(defaultDescriptionText)) {
                if (DEFAULT_LONG_DESCRIPTION_CONTENT_MAP.containsValue(longDescription.getText())) {
                    longDescription.setText("");
                    return;
                }
                return;
            }
            if (StringUtils.isEmpty(longDescription.getText())) {
                longDescription.setText(defaultDescriptionText);
                return;
            }
            if (DEFAULT_LONG_DESCRIPTION_CONTENT_MAP.containsValue(longDescription.getText())) {
                longDescription.setText(defaultDescriptionText);
            }
        };
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    CommitMessage getCommitMessage() {
        return new CommitMessage(
                getSelectedChangeType(),
                (String) changeScope.getSelectedItem(),
                shortDescription.getText().trim(),
                longDescription.getText().trim(),
                sourceKeyword.getText().trim(),
                breakingChanges.getText().trim()
        );
    }

    private ChangeType getSelectedChangeType() {
        for (Enumeration<AbstractButton> buttons = changeTypeGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return ChangeType.valueOf(button.getActionCommand().toUpperCase());
            }
        }
        return null;
    }

}
