.PHONY: up down build build-api logs status test clean

# Docker commands
up:
	docker-compose -f docker/docker-compose.yml up -d

down:
	docker-compose -f docker/docker-compose.yml down

logs:
	docker-compose -f docker/docker-compose.yml logs -f

status:
	docker-compose -f docker/docker-compose.yml ps

# Maven commands
build:
	cd backend && ./mvnw clean install -DskipTests

build-api:
	cd backend && ./mvnw clean install -pl data-pulse-api -am -DskipTests

test:
	cd backend && ./mvnw test

clean:
	cd backend && ./mvnw clean
