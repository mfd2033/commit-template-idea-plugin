package com.leroymerlin.commit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Damien Arrachequesne
 */
public class CommitPanel {

    private static final Logger logger = Logger.getLogger(CustomConfigurable.class.getName());
    private static final String LONG_DESCRIPTION_DEFAULT_CONTENT = "问题原因：\n解决方案：";
    private JPanel mainPanel;
    private JComboBox changeType;
    private JComboBox changeScope;
    private JTextField shortDescription;
    private JTextArea longDescription;
    private JTextField closedIssues;
    private JTextArea breakingChanges;

    CommitPanel(Project project) {
        for (ChangeType type : ChangeType.values()) {
            changeType.addItem(type);
        }
        File workingDirectory = VfsUtil.virtualToIoFile(project.getBaseDir());
        Command.Result gitUserEmailGetCmdResult = new Command(workingDirectory, "git config user.email").execute();
        if (!gitUserEmailGetCmdResult.isSuccess()) {
            return;
        }
        String gitUserEmail = gitUserEmailGetCmdResult.getOutput().get(0);
        logger.info("Git用户邮箱：" + gitUserEmail);
        String gitLogCmd = "git log --author=" + gitUserEmail + " --format=%s | grep -Eo '^[a-z]+(\\(.*\\)):.*$' | sed 's/^.*(\\(.*\\)):.*$/\\1/' | sort -n | uniq";
        Command.Result result = new Command(workingDirectory, gitLogCmd).execute();
        if (result.isSuccess()) {
            result.getOutput().forEach(changeScope::addItem);
        }
        changeType.addItemListener(itemEvent -> {
            if (Objects.equals(itemEvent.getStateChange(), ItemEvent.SELECTED)) {
                ChangeType s = (ChangeType) itemEvent.getItem();
                if (StringUtils.isEmpty(longDescription.getText()) || LONG_DESCRIPTION_DEFAULT_CONTENT.equals(longDescription.getText())) {
                    if (ChangeType.FIX.equals(s)) {
                        longDescription.setText(LONG_DESCRIPTION_DEFAULT_CONTENT);
                    } else {
                        longDescription.setText("");
                    }
                }
            }
        });
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    CommitMessage getCommitMessage() {
        return new CommitMessage(
                (ChangeType) changeType.getSelectedItem(),
                (String) changeScope.getSelectedItem(),
                shortDescription.getText().trim(),
                longDescription.getText().trim(),
                closedIssues.getText().trim(),
                breakingChanges.getText().trim()
        );
    }

}
