package com.chattylabs.plugin

import com.chattylabs.plugin.util.PluginUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UpdateVersionTask extends DefaultTask {

    @Override
    String getGroup() {
        return PluginUtil.TASK_GROUP
    }

    @TaskAction
    void performTask() {
        new VersioningTask(project).execute(false, true)
    }
}