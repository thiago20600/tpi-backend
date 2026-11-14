#!/bin/bash
set -e

export MSYS_NO_PATHCONV=1
export DOCKER_BUILDKIT=1

if [ -e ./ms-catalogo/.docker-build-deps ] || \
   [ -e ./ms-salidas-y-reservas/.docker-build-deps ] || \
   [ -e ./ms-pagos/.docker-build-deps ]; then
  echo "Ya existe un directorio temporal .docker-build-deps; no se modificó." >&2
  exit 1
fi

cleanup() {
  rm -rf ./ms-catalogo/.docker-build-deps
  rm -rf ./ms-salidas-y-reservas/.docker-build-deps
  rm -rf ./ms-pagos/.docker-build-deps
}
trap cleanup EXIT

echo "=========================================="
echo "PASO 1: Compilando tpi-common dentro de un contenedor Maven..."
echo "=========================================="
docker run --rm \
  -v "${PWD}/tpi-common:/app" \
  -v "${HOME}/.m2:/root/.m2" \
  -w /app \
  maven:3.9-eclipse-temurin-21 \
  mvn clean install -DskipTests

echo "=========================================="
echo "PASO 2: Preparando el artefacto tpi-common para los builds..."
echo "=========================================="
COMMON_ARTIFACT="${HOME}/.m2/repository/com/tpbackend/tpi-common/1.0-SNAPSHOT"
for service in ms-catalogo ms-salidas-y-reservas ms-pagos; do
  common_context="./${service}/.docker-build-deps/tpi-common/1.0-SNAPSHOT"
  mkdir -p "${common_context}"
  cp "${COMMON_ARTIFACT}/tpi-common-1.0-SNAPSHOT.jar" \
    "${COMMON_ARTIFACT}/tpi-common-1.0-SNAPSHOT.pom" \
    "${common_context}/"
done

echo "=========================================="
echo "PASO 3: Construyendo las imágenes con caché Maven de BuildKit..."
echo "=========================================="
docker compose build

echo "=========================================="
echo "PASO 4: Levantando Docker Compose..."
echo "=========================================="
docker compose up -d

echo "=========================================="
echo "¡Listo! Los contenedores están corriendo."
echo "==========================================" 