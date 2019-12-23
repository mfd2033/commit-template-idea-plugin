package com.leroymerlin.commit;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author mafudi
 * @since 1.0
 */
public class CustomConfigurable extends Component implements Configurable {

    private static final Logger logger = Logger.getLogger(CustomConfigurable.class.getName());
    private final PersistentState persistentState = ServiceManager.getService(PersistentState.class);
    private JPanel mainPanel;
    private TextFieldWithBrowseButton bashFile;
    private boolean isInit = false;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Git Commit Template";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        isInit = true;
        bashFile.setText(persistentState.getGitBashFilePath());
        bashFile.addBrowseFolderListener("Select bash.exe", "", null, new FileChooserDescriptor(true, false, false, false, false, false)
        {
            @Override
            public boolean isFileSelectable(VirtualFile file)
            {
                return (file.getName().equals("bash.exe"));
            }
        });
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        String gitBashFilePathText = this.bashFile.getText();
        logger.info("bash.exe路径：" + gitBashFilePathText);
        persistentState.setGitBashFilePath(gitBashFilePathText);
    }

    @Override
    public void reset() {
        if (isInit) {
            isInit = false;
            return;
        }
        logger.info("call reset");
        bashFile.setText("");
    }
}
