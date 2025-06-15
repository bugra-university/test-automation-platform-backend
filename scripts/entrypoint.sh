#!/bin/bash

# Repo güncellemesi yapılsın mı?
if [ "$REFRESH_CODE" = "true" ]; then
    echo "Refreshing code from Git repository..."
    /app/clone-repo.sh $GIT_REPO $GIT_BRANCH
fi

# Çalışma dizinini değiştir
cd /app

# Önce proje derle
echo "Compiling project..."
mvn compile

# Test sınıflarını keşfet ve veritabanına kaydet
echo "Discovering test classes..."
mvn exec:java -Dexec.mainClass="project_team09.TestDatabaseInitializer" -Dexec.classpathScope=test

# Testleri çalıştır
echo "Running tests..."
mvn test -DsuiteXmlFile=testng.xml -Dtest.name=${TEST_NAME:-all-tests} -Dtest.package=${TEST_PACKAGE:-project_team09.tests} -Dgit.commit.hash=${GIT_HASH:-unknown} 