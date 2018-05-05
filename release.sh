#!/usr/bin/env bash
# fail if any commands fails
set -e
# debug log
#set -x

if [ $# -lt 4 ]; then
    echo "You need to provide 4 arguments"
    exit 1
fi

version_tag_prefix=$1
final_version=$2
mapping_path=$3
version_upgrade_message=$4

IFS=\. read major minor patch <<< "$final_version"

# Update internal stored APK version
rm -f -r ./apk-version
mkdir -p ./apk-version/${final_version}/mapping/release/
cp ${mapping_path} ./apk-version/${final_version}/mapping/release/

# Push version changes
git status
git add .
git commit -m "<$version_tag_prefix$final_version> $version_upgrade_message"
git tag "$version_tag_prefix$final_version"
git push --tags origin HEAD