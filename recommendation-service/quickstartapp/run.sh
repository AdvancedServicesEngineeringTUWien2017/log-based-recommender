#!/bin/bash
set -e

if [ ! -f /.dockerenv ]; then
  echo "*** NOTICE: Make sure you're running this from inside the Docker container! ***"
  exit 1
fi


echo "Step 1: Making sure everything is running"
sv start /etc/service/*

echo "EventServer may take a minute to start..."
until curl http://localhost:7070; do echo "waiting for EventServer to come online..."; sleep 3; done

pio status
echo "Step 1: Passed"


echo "Step 2. Create a new Engine from an Engine Template"

git clone https://github.com/apache/incubator-predictionio-template-recommender.git MyRecommendation
cd MyRecommendation

echo "Step 2: Passed"


echo "Step 3. Generate an App ID and Access Key"

#echo "YES" | pio app delete MyApp1
pio app new MyApp1 --access-key "secret-key"
KEY="secret-key"
echo "KEY=$KEY"

pio app list
echo "Step 3: Passed"

echo "Step 4 Import Sample Data"

curl -i -X POST http://localhost:7070/events.json?accessKey=$KEY \
-H "Content-Type: application/json" \
-d '{
  "event" : "rate",
  "entityType" : "user",
  "entityId" : "u0",
  "targetEntityType" : "item",
  "targetEntityId" : "i0",
  "properties" : {
    "rating" : 5
  }
  "eventTime" : "2014-11-02T09:39:45.618-08:00"
}'

echo "Inserted rate event"

curl -i -X POST http://localhost:7070/events.json?accessKey=$KEY \
-H "Content-Type: application/json" \
-d '{
  "event" : "buy",
  "entityType" : "user",
  "entityId" : "u1",
  "targetEntityType" : "item",
  "targetEntityId" : "i2",
  "eventTime" : "2014-11-10T12:34:56.123-08:00"
}'

echo "Inserted buy event
"
echo "Step 5. Deploy the Engine as a Service"
sed -i "s|INVALID_APP_NAME|MyApp1|" engine.json
sed -i "s|\"numIterations\": 20|\"numIterations\": 10|" engine.json

echo "Building...  It may take some time to download all the libraries."
pio build --verbose

echo "Training..."
pio train -- --driver-memory 4G

echo "You may now deploy engine by running: cd /quickstartapp/MyRecommendation && pio deploy --ip 0.0.0.0&"
echo "Sample REST call: curl -H \"Content-Type: application/json\" -d '{ \"user\": \"1\", \"num\": 4 }' http://localhost:8000/queries.json|jq"
cd /quickstartapp/MyRecommendation && pio deploy --ip 0.0.0.0&