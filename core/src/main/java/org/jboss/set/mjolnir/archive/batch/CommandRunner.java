package org.jboss.set.mjolnir.archive.batch;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CommandRunner {

    public static void makeDir(File directory, String newDirectoryName) throws IOException, InterruptedException {
        runCommand(directory, "sh", "-c", "mkdir " + newDirectoryName);
    }

    public static void gitClone(File directory, String originUrl) throws IOException, InterruptedException {
        runCommand(directory, "git", "clone", "--mirror", originUrl);
    }

    public static void gitAddRemote(File directory, String userName, String originUrl) throws IOException, InterruptedException {
        runCommand(directory, "git", "remote", "add", userName, originUrl);
    }

    public static void gitFetch(File directory, String userName) throws IOException, InterruptedException {
        runCommand(directory, "git", "fetch", "--tags", userName);
    }

    private static void runCommand(File directory, String... command) throws IOException, InterruptedException {
        Objects.requireNonNull(directory, "directory");
        if (!directory.exists()) {
            throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(directory);
        processBuilder.command(command);
        Process process = processBuilder.start();
        int exit = process.waitFor();

        if (exit != 0) {
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }
    }
}
