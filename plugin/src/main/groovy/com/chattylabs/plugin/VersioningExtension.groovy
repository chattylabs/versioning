package com.chattylabs.plugin

import com.chattylabs.plugin.model.Version
import com.chattylabs.plugin.model.VersionSettings
import org.gradle.api.Action

class VersioningExtension {

    private Version version

    private VersionSettings settings

    VersioningExtension(Version version) {
        this.version = version
        // TODO Load default Settings
        this.settings = new VersionSettings()
    }

    Version getVersion() {
        return version
    }

    def setSettings(Action<VersionSettings> settingsAction) {
        settingsAction.execute(settings)
    }
}
