#!/usr/bin/env bash

tag="$1"
msg="$2"

echo "" > "$HOME/.netrc"
echo "machine github.com" >> "$HOME/.netrc"
echo "login $GITHUB_ACTOR" >> "$HOME/.netrc"
echo "password $GITHUB_TOKEN" >> "$HOME/.netrc"
echo "machine api.github.com" >> "$HOME/.netrc"
echo "login $GITHUB_ACTOR" >> "$HOME/.netrc"
echo "password $GITHUB_TOKEN" >> "$HOME/.netrc"
chmod 600 "$HOME/.netrc"

git config --global user.email "actions@github.com"
git config --global user.name "Github Actions"

echo "Set tag $tag to ${GITHUB_SHA}"
git tag -f -a "$tag" -m "$msg" "${GITHUB_SHA}"

echo "Push the tag"
git push -f origin "refs/tags/$tag"