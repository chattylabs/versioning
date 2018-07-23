package com.chattylabs.plugin

import com.chattylabs.plugin.internal.GitCommandExecutor
import com.chattylabs.plugin.model.VersionSettings
import com.chattylabs.plugin.util.PluginUtil
import org.gradle.api.Project

class VersioningTask {

    VersionSettings versioningSetting = null
    VersioningExtension versioningExtension = null
    String currentVersion = null
    String versionPrefix = null
    Project project = null
    int[] newVersion

    VersioningTask(Project p) {
        this.project = p
    }

    def performAction() {
        versioningExtension = (project.property("versioning") as VersioningExtension)
        versioningSetting = versioningExtension.versionSettings()

        readCurrentVersionTag()
        calculateNewVersion()

        versioningExtension.versionByManipulation(false).setMajor(newVersion[0])
        versioningExtension.versionByManipulation(false).setMinor(newVersion[1])
        versioningExtension.versionByManipulation(false).setPatch(newVersion[2])
        versioningExtension.versionByManipulation(false).save(PluginUtil.getSavedVersionProperty(project))

        println "new version ${newVersion}"
    }

    private void readCurrentVersionTag() {

        versionPrefix = versioningSetting.getPrefix() ?: ""

        def regEx = "^tags[/]${versionPrefix.replace("/", "[/]")}.*"
        GitCommandExecutor.describeTags(versionPrefix, {
            // TODO: Create VersionHelper
            if (it.replace("\n", "").matches(regEx)) {
                currentVersion = it.replaceFirst("-.*\$", "")
                        .replaceFirst("^tags[/]${versionPrefix.replace("/", "[/]")}", "")
                        .replace("\n", "")
            } else {
                throw new RuntimeException("Invalid tag $it")
            }
        })

        newVersion = splitVersion(currentVersion)
        println "Current Version ${newVersion}"
    }

    private void calculateNewVersion() {
        calculateMajor()
    }

    private void calculateMajor() {
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

    private void calculateMinor(String previousMajorCommit = getLastTagCommit()) {
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

    private void calculatePatch(String previousMinorCommit = getLastTagCommit()) {
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
    private static int[] splitVersion(String version) {
        return [version.find("^\\d+").toInteger(),
                version.find("\\.\\d+\\.").find("\\d+").toInteger(),
                version.find("\\d+\$").toInteger()]
    }

    // TODO: move this to GitHelper
    private String getLastTagCommit() {
        String tagCommit = null
        GitCommandExecutor.revParse("$versionPrefix$currentVersion", {
            tagCommit = it.replace("\n", "")
        })

        return tagCommit
    }
}
