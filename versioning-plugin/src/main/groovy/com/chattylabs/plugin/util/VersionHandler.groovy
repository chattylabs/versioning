package com.chattylabs.plugin.util

class VersionHandler {

    private final List<Integer> mVersions = new ArrayList<>()

    VersionHandler(Integer[] versions) {
        mVersions.addAll(versions)
    }

    void increaseVersionBy(Integer index, Integer version) {
        if (version <= 0) {
            return
        }

        for (int idx = index + 1; idx < mVersions.size(); idx++) {
            mVersions[idx] = 0
        }

        mVersions[index] += version
        if (mVersions[index] > 99 && index > 0) {
            increaseVersionBy(index - 1, 1)
        }
    }

    Integer getVersion(int index) {
        return mVersions.get(index)
    }
}