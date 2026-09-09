#!/usr/bin/env bash
# Crea los cuatro usuarios de prueba, uno por rol.
#
# Las bases de datos arrancan vacias: sin esto no hay con quien iniciar sesion.
# La contrasena de cada usuario es su RUT (asi funciona /api/auth/register).
#
# Uso:   ./scripts/seed-usuarios.sh
#        ./scripts/seed-usuarios.sh http://<ip-o-host>:8080

set -u
API="${1:-http://localhost:8080}"

echo "Sembrando usuarios en $API"
echo

# Espera a que el gateway responda antes de intentar (hasta 60s).
for i in $(seq 1 30); do
  if curl -s -o /dev/null -m 2 "$API/api/auth/login" 2>/dev/null; then break; fi
  [ "$i" = "1" ] && printf "Esperando al gateway"
  printf "."
  sleep 2
done
echo

crear() {
  local nombre="$1" apellido="$2" rut="$3" email="$4" rol="$5"
  local codigo
  codigo=$(curl -s -o /tmp/seed_out -w "%{http_code}" -X POST "$API/api/auth/register" \
    -H "Content-Type: application/json" \
    -d "{
      \"email\": \"$email\",
      \"rol\": \"$rol\",
      \"persona\": {
        \"nombre\": \"$nombre\",
        \"apellido\": \"$apellido\",
        \"rut\": \"$rut\",
        \"email\": \"$email\"
      }
    }")

  case "$codigo" in
    2*) printf "  OK   %-24s %-16s clave: %s\n" "$email" "$rol" "$rut" ;;
    *)  printf "  FALLO %-23s HTTP %s\n" "$email" "$codigo"
        head -c 200 /tmp/seed_out; echo ;;
  esac
}

crear "Admin"    "Colegio"  "11111111-1" "admin@colegio.cl"      "ADMINISTRATIVO"
crear "Javiera"  "Perez"    "22222222-2" "profesor@colegio.cl"   "PROFESOR"
crear "Matias"   "Rojas"    "33333333-3" "estudiante@colegio.cl" "ESTUDIANTE"
crear "Carolina" "Soto"     "44444444-4" "apoderado@colegio.cl"  "APODERADO"

echo
echo "Listo. Entra en http://localhost:5173 con cualquiera de esos correos."
echo "Si alguno dio FALLO con codigo 500, lo mas probable es que ya exista"
echo "(el email y el RUT son unicos): eso no es un problema."
