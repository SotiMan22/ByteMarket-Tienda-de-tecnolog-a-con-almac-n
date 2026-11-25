import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class SupabaseSync {
    // Sustituye estos valores por los de tu proyecto en Supabase (Project Settings -> API)
    private static final String SUPABASE_URL = "https://TU_PROYECTO.supabase.co";
    private static final String SUPABASE_KEY = "TU_ANON_PUBLIC_KEY";

    private final HttpClient client = HttpClient.newHttpClient();

    private boolean estaConfigurado() {
        return !SUPABASE_URL.contains("TU_PROYECTO") && !SUPABASE_KEY.contains("TU_ANON");
    }

    public void guardarProducto(Producto p) {
        if (!estaConfigurado()) return;

        String json = String.format(Locale.US,
                "{\"sku\":\"%s\",\"nombre\":\"%s\",\"categoria\":\"%s\",\"precio\":%.2f,\"unidades\":%d,\"stock_minimo\":%d,\"estado\":\"%s\"}",
                p.getSku(), escaparJson(p.getNombre()), p.getCategoria(), p.getPrecio(),
                p.getUnidades(), p.getStockMinimo(), p.getEstado());

        enviarPeticion("POST", "/rest/v1/productos", json);
    }

    public void actualizarStockProducto(Producto p) {
        if (!estaConfigurado()) return;

        String json = String.format(
                "{\"unidades\":%d,\"estado\":\"%s\"}",
                p.getUnidades(), p.getEstado());

        enviarPeticion("PATCH", "/rest/v1/productos?sku=eq." + p.getSku(), json);
    }

    public void guardarMovimiento(Movimiento m) {
        if (!estaConfigurado()) return;

        String json = String.format(
                "{\"sku_producto\":\"%s\",\"tipo\":\"%s\",\"unidades\":%d,\"motivo\":\"%s\"}",
                m.skuProducto(), m.tipo(), m.unidades(), escaparJson(m.motivo()));

        enviarPeticion("POST", "/rest/v1/movimientos", json);
    }

    private void enviarPeticion(String metodo, String endpoint, String jsonBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + endpoint))
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=minimal")
                    .method(metodo, HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                System.out.println("[AVISO SUPABASE] Error HTTP " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            System.out.println("[AVISO SUPABASE] No se pudo sincronizar con la nube: " + e.getMessage());
        }
    }

    private String escaparJson(String texto) {
        return texto.replace("\"", "\\\"");
    }
}
