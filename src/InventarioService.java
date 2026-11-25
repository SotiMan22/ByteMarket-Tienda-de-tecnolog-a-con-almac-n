import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventarioService {
    private List<Producto> productos = new ArrayList<>();
    private List<Movimiento> historial = new ArrayList<>();
    private SupabaseSync supabase = new SupabaseSync();

    public void altaProducto(Producto nuevo) {
        if (buscarPorSku(nuevo.getSku()) != null) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el SKU: " + nuevo.getSku());
        }
        productos.add(nuevo);
        supabase.guardarProducto(nuevo);
    }

    public Producto buscarPorSku(String sku) {
        if (sku == null) return null;
        for (Producto p : productos) {
            if (p.getSku().equalsIgnoreCase(sku.trim())) {
                return p;
            }
        }
        return null;
    }

    public void registrarMovimiento(String sku, TipoMovimiento tipo, int cantidad, String motivo) {
        Producto p = buscarPorSku(sku);
        if (p == null) {
            throw new IllegalArgumentException("No existe ningún producto con el SKU: " + sku);
        }
        if (cantidad <= 0 && tipo != TipoMovimiento.AJUSTE) {
            throw new IllegalArgumentException("La cantidad para ENTRADA o SALIDA debe ser mayor que 0.");
        }
        if (tipo == TipoMovimiento.AJUSTE && (motivo == null || motivo.isBlank())) {
            throw new IllegalArgumentException("Un movimiento de AJUSTE requiere especificar un motivo.");
        }

        int nuevoStock = switch (tipo) {
            case ENTRADA -> p.getUnidades() + cantidad;
            case SALIDA -> {
                if (cantidad > p.getUnidades()) {
                    throw new IllegalStateException("Salida imposible. Stock disponible: " + p.getUnidades() + ", solicitado: " + cantidad);
                }
                yield p.getUnidades() - cantidad;
            }
            case AJUSTE -> {
                if (cantidad < 0) {
                    throw new IllegalArgumentException("El conteo físico de un ajuste no puede ser negativo.");
                }
                yield cantidad;
            }
        };

        p.setUnidades(nuevoStock);
        String motivoFinal = (motivo == null || motivo.isBlank()) ? "Operación estándar" : motivo.trim();
        Movimiento mov = new Movimiento(p.getSku(), tipo, cantidad, motivoFinal, LocalDateTime.now());
        historial.add(mov);

        supabase.actualizarStockProducto(p);
        supabase.guardarMovimiento(mov);
    }

    public List<Producto> getTodos() { return productos; }
    public List<Movimiento> getHistorial() { return historial; }

    public List<Producto> filtrarPorCategoria(Categoria cat) {
        return productos.stream().filter(p -> p.getCategoria() == cat).toList();
    }

    public List<Producto> filtrarPorEstado(EstadoStock est) {
        return productos.stream().filter(p -> p.getEstado() == est).toList();
    }

    public List<Producto> getProductosParaReponer() {
        return productos.stream()
                .filter(p -> p.getEstado() == EstadoStock.BAJO || p.getEstado() == EstadoStock.AGOTADO)
                .toList();
    }

    public double calcularValorTotalInventario() {
        double total = 0;
        for (Producto p : productos) {
            total += p.getPrecio() * p.getUnidades();
        }
        return total;
    }
}
