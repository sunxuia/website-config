#!/bin/bash

mvn clean test -q

if [[ $TRAVIS_PULL_REQUEST == "false" || $TRAVIS_PULL_REQUEST_SLUG == $TRAVIS_REPO_SLUG ]]; then
    echo "start sonarcloud scan"
    mvn org.jacoco:jacoco-maven-plugin:prepare-agent install sonar:sonar -Dsonar.projectKey=sunxuia_website-config \
     -q -Dmaven.test.skip=true
fi

if [[ $TRAVIS_PULL_REQUEST == "false" && $TRAVIS_BRANCH == "master" ]]; then
    echo "deploy jars"
    mvn deploy -Dskip.deploy-docker=true -Dgithub.global.oauth2Token=$github_token -q
fi