package util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 *
 * @author Stoyan Balabanov
 */
public class RunCMD {
    private File workingDirectory;
    
    public RunCMD(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
    
    public void executeCommand(String command) throws IOException, InterruptedException{
         boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", "ls");
        }

        builder.directory(workingDirectory);
        Process process = builder.start();
        StreamGobbler streamGobbler = 
                new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        assert exitCode == 0;
    }
}
