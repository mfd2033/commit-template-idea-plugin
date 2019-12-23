package com.leroymerlin.commit;

import com.intellij.openapi.components.ServiceManager;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class Command {

    private static final Logger logger = Logger.getLogger(Command.class.getName());
    private final File workingDirectory;
    private final String command;
    private final PersistentState persistentState = ServiceManager.getService(PersistentState.class);

    Command(File workingDirectory, String command) {
        this.workingDirectory = workingDirectory;
        this.command = command;
    }

    static class Result {
        static Result ERROR = new Result(-1);

        private final int exitValue;
        private final List<String> output;

        Result(int exitValue) {
            this.exitValue = exitValue;
            this.output = null;
        }

        Result(int exitValue, List<String> output) {
            this.exitValue = exitValue;
            this.output = output;
        }

        boolean isSuccess() {
            return exitValue == 0;
        }

        List<String> getOutput() {
            return output;
        }
    }

    Result execute() {
        try {
            String gitBashFilePath = persistentState.getGitBashFilePath();
            String cmdHeader = StringUtils.isNotBlank(gitBashFilePath) ? gitBashFilePath : "/bin/sh";
            logger.info("cmdHeader->" + cmdHeader + "\ncmd->" + this.command);
            Process process = new ProcessBuilder(cmdHeader, "-c", this.command)
                    .directory(workingDirectory)
                    .start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            List<String> output = reader.lines().collect(Collectors.toList());

            process.waitFor(2, TimeUnit.SECONDS);
            process.destroy();
            process.waitFor();

            return new Result(process.exitValue(), output);
        } catch (Exception e) {
            return Result.ERROR;
        }
    }

}