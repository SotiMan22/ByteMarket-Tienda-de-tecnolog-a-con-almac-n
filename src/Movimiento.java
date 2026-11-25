import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Movimiento(String skuProducto, TipoMovimiento tipo, int unidades, String motivo, LocalDateTime fecha) {
    @Override
    public String toString() {
        String fechaFmt = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return String.format("[%s] %-7s | SKU: %-8s | Cant: %3d | Motivo: %s",
                fechaFmt, tipo, skuProducto, unidades, motivo);
    }
}
