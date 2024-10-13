package model;

import java.util.HashMap;
import java.util.Map;

public class RecursosManager {
    private static RecursosManager instance;
    private Map<String, Integer> recursosGlobales;

    private RecursosManager() {
        recursosGlobales = new HashMap<>();
        inicializarRecursos();
    }

    public static RecursosManager getInstance() {
        if (instance == null) {
            instance = new RecursosManager();
        }
        return instance;
    }

    private void inicializarRecursos() {
        recursosGlobales.put("fruta", 0);
        recursosGlobales.put("carne", 0);
        recursosGlobales.put("madera", 0);
        recursosGlobales.put("planta medicinal", 0);
        recursosGlobales.put("piedra", 0);
        recursosGlobales.put("agua", 0);
    }

    public void agregarRecurso(String tipo, int cantidad) {
        recursosGlobales.put(tipo, recursosGlobales.getOrDefault(tipo, 0) + cantidad);
    }

    public boolean usarRecurso(String tipo, int cantidad) {
        int disponible = recursosGlobales.getOrDefault(tipo, 0);
        if (disponible >= cantidad) {
            recursosGlobales.put(tipo, disponible - cantidad);
            return true;
        }
        return false;
    }

    public Map<String, Integer> getRecursosGlobales() {
        return new HashMap<>(recursosGlobales);
    }

    public String getResumenRecursos() {
        StringBuilder resumen = new StringBuilder("Recursos globales:\n");
        for (Map.Entry<String, Integer> entry : recursosGlobales.entrySet()) {
            resumen.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return resumen.toString();
    }
}