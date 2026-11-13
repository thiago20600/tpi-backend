1. abrir docker desktop
2. en la raiz del proyecto ejecutar `chmod +x build.sh`
3. ejecutar el archivo build.sh con `bash build.sh` en gitbash

El script compila con Maven dentro de Docker y usa la caché de BuildKit para las dependencias; no requiere Java ni Maven instalados en el host.
