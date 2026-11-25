public class Producto {
    private String sku;
    private String nombre;
    private Categoria categoria;
    private double precio;
    private int unidades;
    private int stockMinimo;
    private EstadoStock estado;

    public Producto(String sku, String nombre, Categoria categoria, double precio, int unidades, int stockMinimo) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU no puede estar vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (unidades < 0 || stockMinimo < 0) {
            throw new IllegalArgumentException("Las unidades y el stock mínimo deben ser mayores o iguales a 0.");
        }

        this.sku = sku.toUpperCase().trim();
        this.nombre = nombre.trim();
        this.categoria = categoria;
        this.precio = precio;
        this.unidades = unidades;
        this.stockMinimo = stockMinimo;
        actualizarEstado();
    }

    public void actualizarEstado() {
        if (this.unidades == 0) {
            this.estado = EstadoStock.AGOTADO;
        } else if (this.unidades <= this.stockMinimo) {
            this.estado = EstadoStock.BAJO;
        } else {
            this.estado = EstadoStock.DISPONIBLE;
        }
    }

    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public Categoria getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getUnidades() { return unidades; }
    public int getStockMinimo() { return stockMinimo; }
    public EstadoStock getEstado() { return estado; }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
        actualizarEstado();
    }

    @Override
    public String toString() {
        String etiquetaEstado = switch (estado) {
            case DISPONIBLE -> "[OK] DISPONIBLE";
            case BAJO -> "[!] STOCK BAJO";
            case AGOTADO -> "[X] AGOTADO";
        };

        return String.format("%-8s | %-22s | %-10s | %8.2f € | Stock: %3d (Min: %2d) | %s",
                sku, nombre, categoria, precio, unidades, stockMinimo, etiquetaEstado);
    }
}
