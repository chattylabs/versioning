#!/usr/bin/env bash
# fail if any commands fails
set -e
# debug log
#set -x

DEFAULT_VERSION="0.1.0"
VERSION_PREFIX="version/"
FEATURE_COMMIT_MESSAGE_PATTERN="^Feature"
PATCH_COMMIT_MESSAGE_PATTERN="^Bug"
VERSION_UPGRADE_MESSAGE="Automatic Version Upgrade [skip ci]"

KEY_ALIAS=""
KEY_PASSWORD=""
STORE_PASSWORD=""
STORE_FILE_PATH=""

# Update all
git fetch --all --prune
git submodule update --init

original_version_number=""
is_new=false

# Check for versions
git rev-parse HEAD
project=`git describe --tags --all --always --long --match "$VERSION_PREFIX[0-99]*" HEAD`

if [[ ${project} =~ .*$VERSION_PREFIX[0-99]+\.[0-99]+\.[0-99]+.* ]]; then
  original_version_number=${project##tags/}
  original_version_number=${original_version_number##"$VERSION_PREFIX"}
  original_version_number=${original_version_number%%-*}
fi
if [[ -z "$original_version_number" ]]; then
  original_version_number=${DEFAULT_VERSION}
  is_new=true
fi

IFS=\. read major minor patch <<< "$original_version_number"

number_of_features=0
number_of_patches=0

if [[ "$is_new" != true ]]; then
  # It does contain previous versions
  last_upraged_commit_id=`git log -1 --pretty=%h \
  --grep "$VERSION_UPGRADE_MESSAGE" --fixed-strings`
  number_of_features=`git rev-list ${last_upraged_commit_id}..HEAD \
  --grep "$FEATURE_COMMIT_MESSAGE_PATTERN" --count`
  if [[ ${number_of_features} == 0 ]]; then
    # No previous features
    number_of_patches=`git rev-list ${last_upraged_commit_id}..HEAD \
    --grep "$PATCH_COMMIT_MESSAGE_PATTERN" --count`
  else
    last_feature_commit_id=`git rev-list -1 --grep \
    "$FEATURE_COMMIT_MESSAGE_PATTERN" ${last_upraged_commit_id}..HEAD --abbrev-commit`
    number_of_patches=`git rev-list ${last_feature_commit_id}..HEAD \
    --grep "$PATCH_COMMIT_MESSAGE_PATTERN" --count`
  fi
fi

minor=$(($minor + $number_of_features))
patch=$(($patch + $number_of_patches))

final_version="$major.$minor.$patch"

# Push version changes
if [[ "$final_version" > "$original_version_number" ]]; then
  ./gradlew doUpdateVersion -Pkey=minor -Pvalue=${minor} --quiet
  ./gradlew doUpdateVersion -Pkey=patch -Pvalue=${patch} --quiet
  git add ./version.properties
  git commit -m "<$VERSION_PREFIX$final_version> $VERSION_UPGRADE_MESSAGE"
  git tag "$VERSION_PREFIX$final_version"
  git push --tags origin HEAD
fi
