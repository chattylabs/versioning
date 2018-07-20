package com.chattylabs.plugin.internal

class GitCommandExecutor {

    static File gitDir = null

    static void setGitDir(File directory) {
        if (!directory.isDirectory() || !new File(directory.absolutePath + "/.git").exists()) {
            throw new RuntimeException("Not a valid Git directory")
        }

        gitDir = directory
    }

    static File getGitDir() {
        if (gitDir == null) {
            throw new RuntimeException("Please set gitDir...")
        }

        return gitDir
    }

    static def fetchAll(OnGitCommandSuccess successListener = null, OnGitCommandFailure failureListener = null) {
        executeGitCommand(CommandUtil.processCommands("git fetch --all --prune"), successListener, failureListener)
    }

    static def revParse(String pointer,
                        OnGitCommandSuccess successListener = null,
                        OnGitCommandFailure failureListener = null) {
        executeGitCommand(CommandUtil.processCommands("git rev-parse $pointer"), successListener, failureListener)
    }

    static def revParse(OnGitCommandSuccess successListener = null,
                        OnGitCommandFailure failureListener = null) {
        revParse("HEAD", successListener, failureListener)
    }

    static def revList(String extraCommands,
                       String[] params,
                       OnGitCommandSuccess successListener = null,
                       OnGitCommandFailure failureListener = null) {
        executeGitCommand(CommandUtil.processCommands("git rev-list $extraCommands", params),
                successListener, failureListener)
    }

    static def revList(OnGitCommandSuccess successListener = null,
                       OnGitCommandFailure failureListener = null) {
        revList("HEAD", null, successListener, failureListener)
    }

    static def describeTags(String versionPrefix = "",
                            OnGitCommandSuccess successListener = null,
                            OnGitCommandFailure failureListener = null) {
        def commands = CommandUtil.processCommands('git describe --tags --all --always --long --match %1$s[0-99]*',
                versionPrefix)
        executeGitCommand(commands, successListener, failureListener)
    }

    private static void executeGitCommand(List<String> commandToExecute,
                                          OnGitCommandSuccess successListener = null,
                                          OnGitCommandFailure failureListener = new DefaultOnGitCommandFailure()) {
        def successOutput = new StringBuilder()
        def failureOutput = new StringBuilder()
        def process = commandToExecute.execute(new String[0], getGitDir())
        process.waitForProcessOutput(successOutput, failureOutput)
        if (!failureOutput.toString().isEmpty() && failureListener != null) {
            failureListener.onFailure(failureOutput.toString())
        } else if (successListener != null) {
            successListener.onSuccess(successOutput.toString())
        }
    }
}