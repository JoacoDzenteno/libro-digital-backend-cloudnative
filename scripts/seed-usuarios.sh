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
OUT="$(mktemp)"
trap 'rm -f "$OUT"' EXIT

echo "Sembrando usuarios en $API"

# Espera a que el api-gateway responda algo, lo que sea (hasta 90s).
listo=0
for i in $(seq 1 45); do
  codigo=$(curl -s -o /dev/null -m 2 -w "%{http_code}" "$API/api/auth/login" 2>/dev/null || echo 000)
  if [ "$codigo" != "000" ]; then listo=1; break; fi
  [ "$i" = "1" ] && printf "Esperando al api-gateway"
  printf "."
  sleep 2
done
echo

if [ "$listo" = "0" ]; then
  echo "ERROR: nadie responde en $API despues de 90 segundos."
  echo
  echo "El api-gateway no esta arriba. Revisalo con:"
  echo "    docker compose ps            # debe aparecer api-gateway"
  echo "    docker compose logs api-gateway"
  echo
  echo "Si no aparece en la lista, levantalo con:"
  echo "    docker compose up -d api-gateway"
  exit 1
fi

crear() {
  local nombre="$1" apellido="$2" rut="$3" email="$4" rol="$5"
  local codigo
  codigo=$(curl -s -o "$OUT" -w "%{http_code}" -X POST "$API/api/auth/register" \
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
    }" 2>/dev/null || echo 000)

  case "$codigo" in
    2*)  printf "  OK     %-22s %-16s clave: %s\n" "$email" "$rol" "$rut" ;;
    000) printf "  FALLO  %-22s sin conexion con el gateway\n" "$email" ;;
    409|500)
         printf "  EXISTE %-22s (el email o el RUT ya estan registrados)\n" "$email" ;;
    *)   printf "  FALLO  %-22s HTTP %s\n" "$email" "$codigo"
         [ -s "$OUT" ] && printf "         %s\n" "$(head -c 200 "$OUT")" ;;
  esac
}

crear "Admin"    "Colegio"  "11111111-1" "admin@colegio.cl"      "ADMINISTRATIVO"
crear "Javiera"  "Perez"    "22222222-2" "profesor@colegio.cl"   "PROFESOR"
crear "Matias"   "Rojas"    "33333333-3" "estudiante@colegio.cl" "ESTUDIANTE"
crear "Carolina" "Soto"     "44444444-4" "apoderado@colegio.cl"  "APODERADO"

echo
echo "Entra en http://localhost:5173 con cualquiera de esos correos."
echo "La contrasena es el RUT que aparece al lado."
