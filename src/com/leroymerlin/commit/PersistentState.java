package com.leroymerlin.commit;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mafudi
 * @since 1.0
 */
@State(
        name = "gitCommitTemplateConfig",
        storages = {
                @Storage(
                        file = "$APP_CONFIG$/GitCommitTemplate.xml")
        }
)
public class PersistentState implements PersistentStateComponent<PersistentState> {

    private static final Logger logger = Logger.getLogger(PersistentState.class.getName());
    String gitBashFilePath = "";

    @Nullable
    @Override
    public PersistentState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull final PersistentState pluginConfig) {
        String storedGitBashFilePath = pluginConfig.getGitBashFilePath();
        logger.log(Level.INFO, "加载bash.exe路径：" + storedGitBashFilePath);
        this.gitBashFilePath = storedGitBashFilePath;
    }

    public String getGitBashFilePath() {
        return gitBashFilePath;
    }

    public void setGitBashFilePath(final String gitBashFilePath) {
        this.gitBashFilePath = gitBashFilePath;
    }
}
