service := todos-api

.PHONY: clean test build-app build-images push-images deploy

all: clean test build-app build-images push-images deploy

clean:
	mvn clean

test:
	mvn test

build-app:
	mvn package -DskipTests=true

build-images: check-vars
	mvn spring-boot:build-image -DskipTests=true -Dregistry=$(image-repo) -Dtodos.version=$(version)

push-images: check-vars
	docker push $(image-repo)/$(service):$(version)

deploy: check-vars
	helm template tetrate-todos ../../helm/ --set registry=$(image-repo) --set version=$(version) > ../../deploy.yaml 
	kubectl apply -f ../../deploy.yaml
	kubectl delete po -l app=$(service)

check-vars:
ifndef image-repo
	$(error image-repo is undefined)
endif

ifndef version
	$(error version is undefined)
endif
