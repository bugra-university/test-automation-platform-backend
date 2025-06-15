#!/bin/bash

GIT_REPO=${1:-https://github.com/your-org/project_team09.git}
GIT_BRANCH=${2:-main}

# Repo dizini boşsa, clone yap
if [ ! -d "/app/repo/.git" ]; then
    echo "Cloning repository $GIT_REPO branch $GIT_BRANCH..."
    git clone -b $GIT_BRANCH $GIT_REPO /app/repo
else
    echo "Repository already exists. Pulling latest changes..."
    cd /app/repo
    git pull origin $GIT_BRANCH
fi

# Maven bağımlılıklarını indir
cd /app/repo
mvn dependency:go-offline 