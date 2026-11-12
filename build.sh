#!/bin/bash
set -e

export MSYS_NO_PATHCONV=1

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
echo "PASO 2: Copiando .m2 al contexto de build de ms-catalogo..."
echo "=========================================="
rm -rf ./ms-catalogo/.m2
rm -rf ./gateway/.m2
rm -rf ./ms-salidas-y-reservas/.m2
cp -r "${HOME}/.m2" ./ms-catalogo/.m2
cp -r "${HOME}/.m2" ./gateway/.m2
cp -r "${HOME}/.m2/." ./ms-salidas-y-reservas/.m2/

echo "=========================================="
echo "PASO 3: Construyendo imagen y levantando Docker Compose..."
echo "=========================================="
docker compose up --build -d

echo "=========================================="
echo "PASO 4: Limpiando .m2 copiado..."
echo "=========================================="
rm -rf ./ms-catalogo/.m2
rm -rf ./gateway/.m2
rm -rf ./ms-salidas-y-reservas/.m2

echo "=========================================="
echo "¡Listo! Los contenedores están corriendo."
echo "==========================================" 