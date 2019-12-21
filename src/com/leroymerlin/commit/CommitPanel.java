package com.leroymerlin.commit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;

import javax.swing.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Damien Arrachequesne
 */
public class CommitPanel {

    private static final Logger logger = Logger.getLogger(CustomConfigurable.class.getName());
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
        logger.log(Level.INFO, "Git用户邮箱：" + gitUserEmail);
        String gitLogCmd = "git log --author=" + gitUserEmail + " --format=%s | grep -Eo '^[a-z]+(\\(.*\\)):.*$' | sed 's/^.*(\\(.*\\)):.*$/\\1/' | sort -n | uniq";
        Command.Result result = new Command(workingDirectory, gitLogCmd).execute();
        if (result.isSuccess()) {
            result.getOutput().forEach(changeScope::addItem);
        }
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
