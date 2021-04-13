# /bin/bash

docker run --name postgresql-container -p 5432:5432 -e POSTGRES_PASSWORD=topsecret -d postgres