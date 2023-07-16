# Runs a postgresql db instance in a single docker container
# prerequisite is that a volume named polar-postgres has been created
# Contrary to the philosophy of the book I feel this is a demo of genuine persistence and should persist
# changes made
# The application's integration tests should reset their own temporary datasource and not use this one
# main difference the use of a persistent volume on the container host with the -v argument
# the --rm argument removes the container instance after it stops, therefore you can rerun the script without complaints
# the container named polar-postgres already exists

docker run -d \
  --name polar-postgres \
  -e POSTGRES_DB=polardb_catalog \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -v polar-postgres:/var/lib/postgresql/data \
  --rm \
  postgres:15.3