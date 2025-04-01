openssl genrsa -out ./src/main/resources/private.pem 2048
openssl rsa -in ./src/main/resources/private.pem -pubout > ./src/main/resources/public.pub
