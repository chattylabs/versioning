package com.chattylabs.plugin.internal

interface OnGitCommandSuccess {

    void onSuccess(String text)
}

interface OnGitCommandFailure {

    void onFailure(String text)
}