import java.util.List;
import java.util.Scanner;

public class Main {
    private static InventarioService inventario = new InventarioService();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = -1;

        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());

                switch (opcion) {
                    case 1:
                        opcionAltaProducto();
                        break;
                    case 2:
                        opcionMovimiento();
                        break;
                    case 3:
                        listarProductos();
                        break;
                    case 4:
                        opcionBuscarYFiltrar();
                        break;
                    case 5:
                        mostrarInformes();
                        break;
                    case 6:
                        ejecutarEscenarioDemo();
                        break;
                    case 0:
                        System.out.println("Cerrando ByteMarket. ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("[!] Opción no válida. Introduce un número entre 0 y 6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[ERROR DE FORMATO] Debes introducir un valor numérico válido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("[REGLA DE NEGOCIO BLOQUEADA] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR INESPERADO] " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n============================================================");
        System.out.println("          BYTEMARKET · INVENTARIO Y REPOSICIÓN");
        System.out.println("============================================================");
        System.out.println("1. Alta de nuevo producto");
        System.out.println("2. Registrar movimiento (ENTRADA / SALIDA / AJUSTE)");
        System.out.println("3. Consultar todo el inventario");
        System.out.println("4. Buscar por SKU o filtrar por Categoría / Estado");
        System.out.println("5. Informe de reposición y valoración total");
        System.out.println("6. [DEMO] Ejecutar escenario de demostración obligatorio");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    private static void opcionAltaProducto() {
        System.out.print("SKU (ej. PER-01): ");
        String sku = sc.nextLine();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Categoría (PERIFERICO, COMPONENTE, EQUIPO): ");
        Categoria cat = Categoria.valueOf(sc.nextLine().toUpperCase().trim());
        System.out.print("Precio (€): ");
        double precio = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Unidades iniciales: ");
        int unidades = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Stock mínimo de aviso: ");
        int min = Integer.parseInt(sc.nextLine().trim());

        inventario.altaProducto(new Producto(sku, nombre, cat, precio, unidades, min));
        System.out.println("[OK] Producto registrado correctamente.");
    }

    private static void opcionMovimiento() {
        System.out.print("SKU del producto: ");
        String sku = sc.nextLine();
        System.out.print("Tipo de movimiento (ENTRADA, SALIDA, AJUSTE): ");
        TipoMovimiento tipo = TipoMovimiento.valueOf(sc.nextLine().toUpperCase().trim());
        System.out.print("Cantidad (o stock real contado si es AJUSTE): ");
        int cant = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Motivo (obligatorio si es AJUSTE): ");
        String motivo = sc.nextLine();

        inventario.registrarMovimiento(sku, tipo, cant, motivo);
        System.out.println("[OK] Movimiento registrado y estado actualizado.");
    }

    private static void listarProductos() {
        System.out.println("\n--- LISTADO DE PRODUCTOS EN ALMACÉN ---");
        List<Producto> lista = inventario.getTodos();
        if (lista.isEmpty()) {
            System.out.println("El inventario está vacío.");
            return;
        }
        lista.forEach(System.out::println);
    }

    private static void opcionBuscarYFiltrar() {
        System.out.print("1. Buscar por SKU | 2. Filtrar por Categoría | 3. Filtrar por Estado: ");
        String sub = sc.nextLine().trim();
        switch (sub) {
            case "1" -> {
                System.out.print("Introduce SKU: ");
                Producto p = inventario.buscarPorSku(sc.nextLine());
                System.out.println(p != null ? p : "[!] No se encontró ningún producto con ese SKU.");
            }
            case "2" -> {
                System.out.print("Categoría (PERIFERICO, COMPONENTE, EQUIPO): ");
                Categoria cat = Categoria.valueOf(sc.nextLine().toUpperCase().trim());
                List<Producto> filtrados = inventario.filtrarPorCategoria(cat);
                if (filtrados.isEmpty()) System.out.println("Sin resultados.");
                else filtrados.forEach(System.out::println);
            }
            case "3" -> {
                System.out.print("Estado (DISPONIBLE, BAJO, AGOTADO): ");
                EstadoStock est = EstadoStock.valueOf(sc.nextLine().toUpperCase().trim());
                List<Producto> filtrados = inventario.filtrarPorEstado(est);
                if (filtrados.isEmpty()) System.out.println("Sin resultados.");
                else filtrados.forEach(System.out::println);
            }
            default -> System.out.println("[!] Subopción no válida.");
        }
    }

    private static void mostrarInformes() {
        System.out.println("\n--- INFORME DE REPOSICIÓN (STOCK BAJO O AGOTADO) ---");
        List<Producto> reponer = inventario.getProductosParaReponer();
        if (reponer.isEmpty()) {
            System.out.println("Todos los productos tienen stock suficiente.");
        } else {
            reponer.forEach(System.out::println);
        }
        System.out.printf("\n>>> VALORACIÓN TOTAL DEL INVENTARIO: %.2f € <<<\n", inventario.calcularValorTotalInventario());
    }

    private static void ejecutarEscenarioDemo() {
        System.out.println("\n================ INICIANDO ESCENARIO DEMO ================");

        inventario.altaProducto(new Producto("PER-01", "Teclado Mecánico RGB", Categoria.PERIFERICO, 85.50, 10, 4));
        inventario.altaProducto(new Producto("CMP-02", "SSD NVMe 1TB", Categoria.COMPONENTE, 65.00, 3, 5));
        inventario.altaProducto(new Producto("EQP-03", "Portátil Pro 16", Categoria.EQUIPO, 1200.00, 2, 2));
        System.out.println("1. Registrados 3 productos iniciales (PER-01, CMP-02, EQP-03).");

        inventario.registrarMovimiento("PER-01", TipoMovimiento.ENTRADA, 5, "Reposición proveedor");
        inventario.registrarMovimiento("EQP-03", TipoMovimiento.SALIDA, 2, "Venta cliente corporativo");
        System.out.println("2. Aplicada ENTRADA (+5) en PER-01 y SALIDA (-2) en EQP-03.");

        System.out.println("3. Probando validación: retirar 50 unidades de CMP-02 (stock actual: 3)...");
        try {
            inventario.registrarMovimiento("CMP-02", TipoMovimiento.SALIDA, 50, "Pedido erróneo");
        } catch (IllegalStateException e) {
            System.out.println("   -> [BLOQUEO CORRECTO] " + e.getMessage());
        }

        System.out.println("4. Generando informes finales:");
        mostrarInformes();
        System.out.println("==========================================================");
    }
}
