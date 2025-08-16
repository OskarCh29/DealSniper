#!/bin/bash

#===========================
# Function to shut down containers when application stopped
# Used for small improvement by now. After adding microservices consider another
#===========================
cleanup() {
  echo "Stopping docker..."
  docker compose down
}
trap cleanup EXIT

echo "Starting up Docker compose..."
docker compose up -d

echo "Waiting for docker services to be ready..."
sleep 10

echo "Migrating and generating date..."
mvn clean generate-sources

echo "Starting main application..."
mvn spring-boot:run
