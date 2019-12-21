package com.leroymerlin.commit;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author mafudi
 */
public class CommandTest {

    @Test
    public void should_return_git_version_when_execute_given_command() {
        File workDir = new File(System.getProperty("user.dir"));
        String cmd = "git log --all --format=%s | grep -Eo '^[a-z]+(\\(.*\\)):.*$' | sed 's/^.*(\\(.*\\)):.*$/\\1/' | sort -n | uniq";
        Command command = new Command(workDir, cmd);

        Command.Result result = command.execute();

        List<String> outputs = result.getOutput();
        assertNotNull(outputs);
        String output = outputs.get(0);
        System.out.println(output);
        assertTrue(output.startsWith("git version"));
    }

}
