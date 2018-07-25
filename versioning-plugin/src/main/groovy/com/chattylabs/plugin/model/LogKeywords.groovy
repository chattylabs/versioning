package com.chattylabs.plugin.model

class LogKeywords {

    private String[] mMajor
    private String[] mMinor
    private String[] mPatch

    void major(String... keywords) {
        this.mMajor = keywords
    }

    void minor(String... keywords) {
        this.mMinor = keywords
    }

    void patch(String... keywords) {
        this.mPatch = keywords
    }

    String[] getMajor() {
        return this.mMajor
    }

    String[] getMinor() {
        return this.mMinor
    }

    String[] getPatch() {
        return this.mPatch
    }

    boolean hasMajor() {
        return this.mMajor != null && this.mMajor.length > 0
    }

    boolean hasMinor() {
        return this.mMinor != null && this.mMinor.length > 0
    }

    boolean hasPatch() {
        return this.mPatch != null && this.mPatch.length > 0
    }
}