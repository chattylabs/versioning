package com.chattylabs.plugin.internal

final class DefaultOnGitCommandFailure implements OnGitCommandFailure {

    DefaultOnGitCommandFailure() {}

    @Override
    void onFailure(String text) {
        throw new RuntimeException(text)
    }
}