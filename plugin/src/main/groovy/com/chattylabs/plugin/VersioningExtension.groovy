package com.chattylabs.plugin

import com.chattylabs.plugin.internal.GitCommandExecutor
import com.chattylabs.plugin.model.GitSettings
import com.chattylabs.plugin.model.Version
import com.chattylabs.plugin.model.VersionSettings
import org.gradle.api.Action
import org.gradle.api.Project

class VersioningExtension {

    private Version version
    private VersionSettings vSettings = new VersionSettings()
    private GitSettings gSettings = new GitSettings()
    private Project project

    VersioningExtension(Version version, Project p) {
        this.version = version
        this.project = p
    }

    String version() {
        return this.versionByManipulation(true).toString()
    }

    Version versionByManipulation(boolean shouldConfigure) {
        if (shouldConfigure) {
            configurePlugin()
        }
        return this.version
    }

    void versionSetting(Action<? super VersionSettings> settingsAction) {
        settingsAction.execute(vSettings)
    }

    void gitSetting(Action<? super GitSettings> gitAction) {
        gitAction.execute(gSettings)
    }

    VersionSettings versionSettings() {
        return vSettings
    }

    GitSettings gitSettings() {
        return gSettings
    }

    private void configurePlugin() {
        GitCommandExecutor.setGitDir(gSettings.dir() ?: project.rootProject.rootDir)
        new VersioningTask(project).performAction()
    }
}
