# ByteMarket · Sistema de Inventario y Reposición Inteligente (Grupo 2)

Aplicación de consola en Java para la gestión de almacén, control de entradas/salidas, cálculo automático de alertas de reposición, validación de reglas de negocio y persistencia opcional en **Supabase (PostgreSQL)** mediante su API REST.

## Requisitos Técnicos
* **Versión de Java:** OpenJDK 21 (compatible con Java 17 o superior).
* **Entorno de desarrollo:** IDE compatible con Java 21 / GitHub Codespaces / OnlineGDB.
* **Base de datos (Ampliación):** Supabase (PostgreSQL + PostgREST API).

## Configuración de Supabase
1. Crear las tablas `productos` y `movimientos` en el **SQL Editor** de Supabase.
2. En `src/SupabaseSync.java`, introducir la `SUPABASE_URL` y la `SUPABASE_ANON_KEY` (ubicadas en *Project Settings > API*).
*(Nota: Si se dejan por defecto, la aplicación funciona completamente en memoria).*

## Instrucciones de Ejecución
1. Compilar el código fuente desde la raíz del proyecto:
   ```bash
   javac src/*.java
   ```
2. Ejecutar la aplicación:
   ```bash
   java -cp src Main
   ```
3. Seleccionar la **opción 6** en el menú para ejecutar el escenario de demostración oficial.
