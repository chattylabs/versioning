package com.chattylabs.plugin.util

import com.chattylabs.plugin.internal.DefaultOnGitCommandFailure
import com.chattylabs.plugin.internal.OnGitCommandFailure
import com.chattylabs.plugin.internal.OnGitCommandSuccess

class GitUtil {

    static File gitDir = null

    static void setGitDir(File directory) {
        if (!directory.isDirectory()) {
            throw new RuntimeException("${directory} is not a valid Git directory or subdirectory")
        }

        gitDir = directory

        fetchAll()
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

    static String getLastTagCommitId(String tagName) {
        String tagCommit = null
        revParse(tagName, {
            tagCommit = it.replace("\n", "")
        })

        return tagCommit
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

    static def checkTags(String versionPrefix = "",
                         OnGitCommandSuccess successListener = null,
                         OnGitCommandFailure failureListener = null) {
        def commands = CommandUtil.processCommands(
                'git tag -l %1$s[0-99]\\.[0-99]\\.[0-99] --sort=-creatordate',
                versionPrefix)
        executeGitCommand(commands, successListener, failureListener)
    }

    static def createTag(String tagName) {
        executeGitCommand(CommandUtil.processCommands("git tag $tagName"))
    }

    static def pushTags() {
        executeGitCommand(CommandUtil.processCommands("git push --tags"))
    }

    static ArrayList<String> getCommitList(String[] msgKeywords, String fromCommit, String toCommit = "HEAD") {
        final String grepCommand = CommandUtil.formGrepTemplate(1, msgKeywords.length)
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