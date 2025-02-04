
docker compose down

rm custom-mapper.jar
jar cf custom-mapper.jar -C custom-mapper .

echo "Custom Mapper JAR created"

docker compose up -d