#!/bin/bash

# Wait for API to be ready
echo "Waiting for API to be ready..."
until $(curl --output /dev/null --silent --fail ${API_BASE_URL}/actuator/health); do
    printf '.'
    sleep 5
done
echo "API is ready!"

# Wait for Selenium Hub to be ready
echo "Waiting for Selenium Hub to be ready..."
until $(curl --output /dev/null --silent --head --fail ${SELENIUM_HUB_URL}); do
    printf '.'
    sleep 5
done
echo "Selenium Hub is ready!"

# Run the tests
echo "Running tests..."
mvn test -DsuiteXmlFile=testng.xml

# Copy test reports to volume
mkdir -p /app/TestOutput/reports
cp -r target/surefire-reports/* /app/TestOutput/reports/

echo "Tests completed. Results available in /app/TestOutput/reports"
