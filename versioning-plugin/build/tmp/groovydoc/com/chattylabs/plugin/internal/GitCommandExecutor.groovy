package com.chattylabs.plugin.internal

class GitCommandExecutor {

    static File gitDir = null

    static void setGitDir(File directory) {
        if (!directory.isDirectory()) {
            throw new RuntimeException("${directory} is not a valid Git directory or subdirectory")
        }

        gitDir = directory
    }

    static File getGitDir() {
        if (gitDir == null) {
            throw new IllegalArgumentException("Git directory does not exist or has not been provided")
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

    static ArrayList<String> getCommitList(String[] msgKeywords, String fromCommit, String toCommit = "HEAD") {
        String grepCommand = CommandUtil.formGrepTemplate(1, msgKeywords.length)
        // TODO: Add a logger and print (helpful)
//        println "Grep -- Command -- $grepCommand \n commit list : $fromCommit..$toCommit"
        ArrayList<String> commitList = null
        revList("$fromCommit..$toCommit $grepCommand",
                msgKeywords, {
            commitList = it.split("\\n").findAll { it.length() == 40 }
        })
        return commitList
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