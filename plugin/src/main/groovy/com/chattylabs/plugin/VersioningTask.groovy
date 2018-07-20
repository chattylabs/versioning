package com.chattylabs.plugin

import com.chattylabs.plugin.internal.GitCommandExecutor
import com.chattylabs.plugin.model.GitSettings
import com.chattylabs.plugin.model.VersionSettings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VersioningTask extends DefaultTask {

    GitSettings gitSettings = null
    VersionSettings versioningSetting = null

    String currentVersion = null
    String versionPrefix = null
    int[] newVersion = new int[3]

    @Override
    String getGroup() {
        return "versioning"
    }

    @TaskAction
    def performAction() {
        VersioningExtension versioningExtension = (project.property("versioning") as VersioningExtension)
        versioningSetting = versioningExtension.versionSettings()
        gitSettings = versioningExtension.gitSettings()

        readCurrentVersionTag()
        calculateNewVersion()

        println "newversion ${newVersion}"
    }

    void readCurrentVersionTag() {

        versionPrefix = versioningSetting.getPrefix() ?: ""

        // TODO: Assuming we rootProject rootDir has .git folder. We need to change this logic.
        // We can provide gradle option to set this directory if it is other than root has .git.
        GitCommandExecutor.setGitDir(gitSettings.dir() ?: project.rootProject.rootDir)
//        GitCommandExecutor.setGitDir(new File("/Users/surtamil/Desktop/Suryavel/GIT/tag_filter"))

        def regEx = "^tags[/]${versionPrefix.replace("/", "[/]")}.*"
        GitCommandExecutor.describeTags(versionPrefix, {
            // TODO: Create VersionHelper
            if (it.replace("\n", "").matches(regEx)) {
                currentVersion = it.replaceFirst("-.*\$", "")
                        .replaceFirst("^tags[/]${versionPrefix.replace("/", "[/]")}", "")
                        .replace("\n", "")
                println currentVersion
            } else {
                throw new RuntimeException("Invalid tag $it")
            }
        })

        newVersion = splitVersion(currentVersion)
    }

    void calculateNewVersion() {
        calculateMajor()
    }

    void calculateMajor() {
        if (!versioningSetting.hasMajorKeys()) {
            calculateMinor()
            return
        }

        def majorCommits = GitCommandExecutor.getCommitList(versioningSetting.keywordsMajor,
                "$versionPrefix$currentVersion")
        // TODO: Add a logger and print (helpful)
//        println "majorShouldBeAddedBy ${majorCommits.size()} -- $majorCommits"
        newVersion[0] += majorCommits.size()
        calculateMinor(majorCommits.size() > 0 ? majorCommits.get(0) : getLastTagCommit())
    }

    void calculateMinor(String previousMajorCommit = getLastTagCommit()) {
        if (!versioningSetting.hasMinorKeys()) {
            calculatePatch()
            return
        }

        // TODO: Add a logger and print (helpful)
//        println "previous major commit $previousMajorCommit \n minor keys : ${versioningSetting.keywordsMinor}"
        def minorCommits = GitCommandExecutor.getCommitList(versioningSetting.keywordsMinor, previousMajorCommit)
        // TODO: Add a logger and print (helpful)
//        println "minorShouldBeAddedBy ${minorCommits.size()} -- $minorCommits"
        newVersion[1] += minorCommits.size()
        calculatePatch(minorCommits.size() > 0 ? minorCommits.get(0) : getLastTagCommit())
    }

    void calculatePatch(String previousMinorCommit = getLastTagCommit()) {
        if (!versioningSetting.hasPatchKeys()) {
            return
        }

        // TODO: Add a logger and print (helpful)
//        println "previous minor commit $previousMinorCommit \n patch keys : ${versioningSetting.keywordsPatch}"
        def patchCommits = GitCommandExecutor.getCommitList(versioningSetting.keywordsPatch, previousMinorCommit)
        // TODO: Add a logger and print (helpful)
//        println "patchShouldBeAddedBy ${patchCommits.size()} -- $patchCommits"
        newVersion[2] += patchCommits.size()
    }

    // TODO : move this method to common util
    static int[] splitVersion(String version) {
        return [version.find("^\\d+").toInteger(),
                version.find("\\.\\d+\\.").find("\\d+").toInteger(),
                version.find("\\d+\$").toInteger()]
    }

    // TODO: move this to GitHelper
    String getLastTagCommit() {
        String tagCommit = null
        GitCommandExecutor.revParse("$versionPrefix$currentVersion", {
            tagCommit = it.replace("\n", "")
        })

        return tagCommit
    }
}
