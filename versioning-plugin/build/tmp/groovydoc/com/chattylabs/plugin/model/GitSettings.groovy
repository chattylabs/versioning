package com.chattylabs.plugin.model

class GitSettings {
    private File gitFolder = null

    void dir(File git){
        gitFolder = git
    }

    File dir() {
        return gitFolder
    }
}
