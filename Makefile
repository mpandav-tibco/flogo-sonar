.PHONY: build docker-up docker-down scan clean

build:
	mvn clean package -DskipTests

docker-up: build
	docker compose up -d
	@echo "Waiting for SonarQube to start (this takes ~2 minutes)..."
	@until curl -s http://localhost:9000/api/system/status | grep -q '"status":"UP"'; do \
		sleep 5; echo "  Still starting..."; \
	done
	@echo "SonarQube is ready at http://localhost:9000"
	@echo "Default credentials: admin / admin"

docker-down:
	docker compose down -v

scan:
	sonar-scanner

clean:
	mvn clean
	docker compose down -v 2>/dev/null || true
