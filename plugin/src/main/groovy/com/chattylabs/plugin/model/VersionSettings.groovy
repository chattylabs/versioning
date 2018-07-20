package com.chattylabs.plugin.model

class VersionSettings {

    String prefix

    String[] keywordsMajor

    String[] keywordsMinor

    String[] keywordsPatch

    void tagPrefix(String p) {
        this.prefix = p
    }

    void majorVersionKeys(String... keywords) {
        this.keywordsMajor = keywords
    }

    void minorVersionKeys(String... keywords) {
        this.keywordsMinor = keywords
    }

    void patchVersionKeys(String... keywords) {
        this.keywordsPatch = keywords
    }

    boolean hasMajorKeys() {
        return this.keywordsMajor != null && this.keywordsMajor.length > 0
    }

    boolean hasMinorKeys() {
        return this.keywordsMinor != null && this.keywordsMinor.length > 0
    }

    boolean hasPatchKeys() {
        return this.keywordsPatch != null && this.keywordsPatch.length > 0
    }
}
