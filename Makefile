.PHONY: clean test build-app build-images push-images

all: all-mesh all-legacy

all-mesh: clean test build-app build-images push-images-mesh deploy-mesh

all-legacy: clean test build-app build-images push-images deploy

clean:
	mvn clean

test:
	mvn test

build-app:
	mvn package -DskipTests=true

build-images: check-imagerepo
	mvn spring-boot:build-image -DskipTests=true -Dregistry=$(image-repo)

push-images: check-imagerepo
	docker push $(image-repo)/todos-edge:latest
	docker push $(image-repo)/todos-api:latest
	docker push $(image-repo)/todos-postgres:latest
	docker push $(image-repo)/todos-redis:latest
	docker push $(image-repo)/todos-webui:latest
	docker push $(image-repo)/todos-registry:latest

push-images-mesh: check-imagerepo
	docker push $(image-repo)/todos-redis-mesh:latest
	docker push $(image-repo)/todos-postgres-mesh:latest
	docker push $(image-repo)/todos-api-mesh:latest
	docker push $(image-repo)/todos-webui-mesh:latest

deploy: check-imagerepo
	helm template tetrate-todos todos-original/todos-chart/ --set registry=$(image-repo) > deploy.yaml 
	kubectl apply -f deploy.yaml

deploy-mesh: check-imagerepo
	helm template tetrate-todos-mesh todos-mesh/todos-chart --set registry=$(image-repo) > deploy-mesh.yaml 
	kubectl apply -f deploy-mesh.yaml

restart: 
	kubectl delete po -l app=todos-registry 
	kubectl delete po -l app=todos-edge 
	kubectl delete po -l app=todos-postgres
	kubectl delete po -l app=todos-redis
	kubectl delete po -l app=todos-api-v1
	kubectl delete po -l app=todos-api-v2
	kubectl delete po -l app=todos-webui

check-imagerepo:
ifndef image-repo
	$(error image-repo is undefined)
endif
