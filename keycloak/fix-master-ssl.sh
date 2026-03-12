#!/bin/bash
# Espera a que Keycloak arranque y luego deshabilita ssl_required en master
until curl -s http://keycloak:9090/realms/master > /dev/null 2>&1; do
  echo "Esperando a Keycloak..."
  sleep 3
done

echo "Keycloak listo. Obteniendo token admin..."
TOKEN=$(curl -s -X POST "http://keycloak:9090/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=admin-cli&username=admin&password=admin&grant_type=password" \
  | sed 's/.*"access_token":"\([^"]*\)".*/\1/')

echo "Parcheando realm master para deshabilitar ssl_required..."
curl -s -X PUT "http://keycloak:9090/admin/realms/master" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sslRequired": "NONE"}'

echo "Realm master actualizado correctamente."
