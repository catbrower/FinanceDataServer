mvn clean package
docker container prune -f
docker image rm finance-data-server
docker-compose up