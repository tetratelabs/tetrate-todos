.PHONY: clean test build-app build-images push-images deploy

all: clean test build-app build-images push-images deploy

clean:
	mvn clean

test:
	mvn test

build-app:
	mvn package -DskipTests=true

build-images: check-imagerepo
	mvn spring-boot:build-image -DskipTests=true -Dregistry=$(image-repo)

push-images: check-imagerepo
	docker push $(image-repo)/todos-redis:latest

deploy: check-imagerepo
	helm template tetrate-todos ../todos-chart/ --set registry=$(image-repo) > ../deploy.yaml 
	kubectl apply -f ../deploy.yaml
	kubectl delete po -l app=todos-redis 

check-imagerepo:
ifndef image-repo
	$(error image-repo is undefined)
endif
