package com.dxc.command_executor;

public class CommandResult {

    public int rc;
    public final String stdout;
    public final String stderr;
    public final boolean timed_out;

    public CommandResult(int rc, boolean timed_out, String stdout, String stderr) {
        this.rc = rc;
        this.stdout = stdout;
        this.stderr = stderr;
        this.timed_out = timed_out;
    }

    @Override
    public String toString() {
        return "CommandResult {rc:" + this.rc + ", timed_out:" + this.timed_out + ", stdout:\"" + this.stdout + "\", stderr:\"" + this.stderr + "\"}";
    }
}
