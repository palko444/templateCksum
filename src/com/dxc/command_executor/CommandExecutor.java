package com.dxc.command_executor;

//import com.dxc.omc.utils.Log;
//import com.dxc.omc.utils.Strings;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.joda.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CommandExecutor {

    public static CommandExecutor create(String command, String... args) {
        List<String> v = new ArrayList<>(args.length + 1);
        v.add(command);
        v.addAll(Arrays.asList(args));
        return new CommandExecutor(v);
    }

    private final List<String> commandLine;
    private final ProcessBuilder pb;

    private CommandExecutor(List<String> command) {
        this.commandLine = command;
        this.pb = new ProcessBuilder();
    }

    public CommandExecutor env(String key, String value) {
        this.pb.environment().put(key, value);
        return this;
    }

    public CommandExecutor arg(String arg) {
        this.commandLine.add(arg);
        return this;
    }

    public <T extends Collection<String>> CommandExecutor args(T args) {
        for (String arg : args) {
            this.arg(arg);
        }
        return this;
    }

    public CommandExecutor args(String... args) {
        for (String arg : args) {
            this.arg(arg);
        }
        return this;
    }

    public CommandResult exec(Duration timeout) throws IOException, StreamGobbler.QueueFullException {

        FutureTask<CommandResult> ft = new FutureTask<>(new Callable<CommandResult>() {
            @Override
            public CommandResult call() throws IOException {
                pb.command(commandLine);
//                Log.verbose("Executing command: [", Strings.fromCollection(commandLine, " "), "]");
                Process p = pb.start();

                StringWriter stdout = new StringWriter();
                StreamGobbler out_gobbler = StreamGobbler.create(new InputStreamReader(p.getInputStream()), stdout, 65536);

                StringWriter stderr = new StringWriter();
                StreamGobbler err_gobbler = StreamGobbler.create(new InputStreamReader(p.getErrorStream()), stderr, 65536);

                int rc;
                boolean timed_out = false;
                try {
//                    Log.trace("Waiting for command to finish");
                    rc = p.waitFor();
//                    Log.verbose("Command finished normally ... exit code: ", String.valueOf(rc));
                } catch (InterruptedException _ie) {
                    timed_out = true;
//                    Log.warn("Command execution interrupted ... destroying process");
                    p.destroy();
                    // TODO: we might give the process some time to finish and capture the output, but we're cutting the output here
                    out_gobbler.interrupt();
                    err_gobbler.interrupt();
                    try {
                        rc = p.waitFor();
//                        Log.verbose("Process successfully destroyed ... exit code: ", String.valueOf(rc));
                    } catch (InterruptedException _ie2) {
//                        Log.warn("Process destruction interrupted ...");
                        rc = -1;
                    }
                }
//                Log.trace("Waiting for stdout gobbler");
                out_gobbler.wait_completion(Duration.millis(200));
//                Log.trace("Waiting for stderr gobbler");
                err_gobbler.wait_completion(Duration.millis(200));
                String out = stdout.toString();
                String err = stderr.toString();
//                Log.debug("stdout: \n", out, "\nstderr: \n", err);
                return new CommandResult(rc, timed_out, out, err);
            }
        });
        Thread t = new Thread(ft);
        t.setDaemon(true);
        t.start();
        t.setName("process");
        try {
//            Log.trace("Waiting for CommandExecutor thread to finish for ", String.valueOf(timeout.getMillis()), " milliseconds");
            t.join(timeout.getMillis());
            t.interrupt();
            t.join(100);
            t.interrupt();
            try {
                // after double interrupt, it should not be possible for the process thread to be running anymore
                // therfore ft.get() should always succeed
                // that's why we have the assertion in catch (InterruptedException ie) branch
//                Log.trace("Getting value from CommandExecutor thread");
                return ft.get();
            } catch (ExecutionException ee) {
//                Log.trace("CommandExecutor thread got exception");
                Throwable ec = ee.getCause();
                if (ec instanceof IOException) {
                    throw (IOException) ec;
                }
                throw new RuntimeException("Unexpected exception occured", ec);
            }
        } catch (InterruptedException ie) {
            throw new AssertionError("", ie);
        }
    }
}
