Log-based ecommerce recommendation system
===================

The full documentation can be found in documentation/demo-documentation.pdf.

The project can be run in docker. However, the docker machine needs to run on specific IP: http://192.168.99.100

The system consists of 5 instances:
1. Analytic service: 8080
2. Recommendation service: 9000 (PredicitonIO dashboard), 9200 (Elasticsearch), 7070 (Event server), 8000 (Event engine)
3. Notification service: 8070
4. Simulation service: 8060
5. Kibana: 5601

First we need to create images for all the service.
1. Open docker console.
2. Make sure it runs on IP http://192.168.99.100 (if not, change it in the docker config): docker-machine ip 
3. Go to analytic-service directory and run: mvn package docker:build
4. Go to notification-service directory and run: mvn package docker:build
5. Go to simulation-service directory and run: mvn package docker:build
6. Go to recommendation-service directory and run: docker build . -t predictionio

Secondly we need to run all the services

1. Go back to project root folder
2. Run: docker-compose up
3. Wait untill all services start (it might take few minutes)
4. The service interface can be found at: http://192.168.99.100:8080

Using the service:
- Before inserting any data, we need to create an index or mapping where the data will be stored.
- If you want to use the data from simulation service go to: http://192.168.99.100:8080/neweventtype and create Recommender event type with mapping named "simulations". 
You need to copy the properties from the example (you may also add more properties).
- You can check if the event type was created here: http://192.168.99.100:8080/eventtypes
- You may define more indexes and mappings either through service interface or directly through Kibana.
- The data can be inserted to Elasticsearch through API available on port 9200. 
- The data which are inserted in the recommender are automatically stored in Elastic. They can be inserted manually through: http://192.168.99.100:8080/newrecommenderevent
- External applications can insert events through analytic service: http://192.168.99.100:8080/insertEvent The call accepts JSONObject with required fields: event, entityId,entityType,targetEntityId. Other properties are optional.
- For the recommender to work there is some number of events needed.
- After higher number of events has been inserted (few hundred), it is highly probable that the recommendation system needs to be retrained, otherwise, it will return zero recommendations. To retrain the system, run in docker following command: docker exec predictionio-container /bin/sh -c "cd /quickstartapp/MyRecommendation && pio train && pio deploy --ip 0.0.0.0&"
- The recommendations can be retrieved at: http://192.168.99.100:8080/getrecommendations


