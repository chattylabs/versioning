package com.chattylabs.plugin.internal

final class DefaultOnGitCommandFailure implements OnGitCommandFailure {

    protected DefaultOnGitCommandFailure() {}

    @Override
    void onFailure(String text) {
        throw new RuntimeException(text)
    }
}