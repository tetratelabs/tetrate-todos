.PHONY: clean test build-app build-images push-images

all: all-mesh all-legacy

all-mesh: clean test build-app build-images deploy-mesh

all-legacy: clean test build-app build-images push-images deploy

clean:
	./mvnw clean

test:
	./mvnw test

build-app:
	./mvnw package -DskipTests=true

build-images: check-vars
	./mvnw spring-boot:build-image -DskipTests=true -Dregistry=$(image-repo) -Dtodos.version=$(version)

push-images: check-vars
	docker push $(image-repo)/todos-edge:$(version)
	docker push $(image-repo)/todos-api:$(version)
	docker push $(image-repo)/todos-postgres:$(version)
	docker push $(image-repo)/todos-redis:$(version)
	docker push $(image-repo)/todos-webui:$(version)
	docker push $(image-repo)/todos-registry:$(version)

deploy: check-vars
	helm template tetrate-todos helm --set registry=$(image-repo) --set version=$(version) > deploy.yaml 
	kubectl apply -f deploy.yaml

deploy-mesh: check-vars
	helm template tetrate-todos-mesh helm --set registry=$(image-repo) --set version=$(version) > deploy-mesh.yaml 
	kubectl apply -f deploy-mesh.yaml
	kubectl apply -f tetrate/istio.yaml

restart: 
	kubectl delete po -l app=todos-registry 
	kubectl delete po -l app=todos-edge 
	kubectl delete po -l app=todos-postgres
	kubectl delete po -l app=todos-redis
	kubectl delete po -l app=todos-api
	kubectl delete po -l app=todos-webui

check-vars:
ifndef image-repo
	$(error image-repo is undefined)
endif

ifndef version
	$(error version is undefined)
endif
