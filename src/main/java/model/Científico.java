package model;

import java.util.Random;

public class Científico extends Personaje {
    private int habilidadCiencia;
    private Random random = new Random();
    private static final int ENERGIA_CREAR_MEDICAMENTO = 15;
    private static final int ENERGIA_INVESTIGAR = 10;
    private static final int PLANTAS_NECESARIAS_MEDICAMENTO = 3;
    private static final double PROBABILIDAD_BASE_EXITO = 0.7; // 70% de probabilidad base

    public Científico(String nombre) {
        super(nombre);
        this.habilidadCiencia = 50;
    }

    @Override
    public void accionar() {
        // El científico intentará crear medicamentos si tiene los recursos necesarios
        if (buscarRecursoEnInventario("planta medicinal") != null) {
            System.out.println(nombre + " está listo para investigar y crear medicamentos.");
        } else {
            System.out.println(nombre + " necesita plantas medicinales para investigar.");
        }
    }

    @Override
    public void comer(String tipoComida) {
        int energiaRecuperada = 0;
        
        switch (tipoComida) {
            case "fruta":
                energiaRecuperada = 10;
                break;
            case "carne":
                energiaRecuperada = 20;
                break;
            default:
                return;
        }
        
        Recurso comida = buscarRecursoEnInventario(tipoComida);
        if (comida != null && comida.getCantidad() > 0) {
            comida.usarRecurso(1);
            recuperarEnergia(energiaRecuperada);
            
            if (comida.getCantidad() <= 0) {
                inventario.remove(comida);
            }
            
            System.out.println(nombre + " ha comido " + tipoComida + 
                " y recuperado " + energiaRecuperada + " puntos de energía.");
        } else {
            System.out.println(nombre + " no tiene " + tipoComida + " para comer.");
        }
    }

    public boolean crearMedicamentoAvanzado() {
        if (nivelEnergia < ENERGIA_CREAR_MEDICAMENTO) {
            System.out.println(nombre + " no tiene suficiente energía para crear un medicamento.");
            return false;
        }

        Recurso plantaMedicinal = buscarRecursoEnInventario("planta medicinal");
        
        if (plantaMedicinal != null && plantaMedicinal.getCantidad() >= PLANTAS_NECESARIAS_MEDICAMENTO) {
            reducirEnergia(ENERGIA_CREAR_MEDICAMENTO);
            plantaMedicinal.usarRecurso(PLANTAS_NECESARIAS_MEDICAMENTO);
            
            if (plantaMedicinal.getCantidad() <= 0) {
                inventario.remove(plantaMedicinal);
            }

            // Probabilidad de éxito basada en habilidad y probabilidad base
            double probabilidadExito = PROBABILIDAD_BASE_EXITO + ((habilidadCiencia / 100.0) * 0.3);
            if (random.nextDouble() < probabilidadExito) {
                agregarRecursoAInventario("medicamento avanzado", 1);
                mejorarHabilidad(3);
                System.out.println(nombre + " ha creado exitosamente un medicamento avanzado.");
                return true;
            } else {
                System.out.println(nombre + " falló en crear el medicamento avanzado.");
                return false;
            }
        }
        
        System.out.println(nombre + " necesita al menos " + PLANTAS_NECESARIAS_MEDICAMENTO + 
            " plantas medicinales para crear un medicamento avanzado.");
        return false;
    }

    public boolean curarEnfermedadGrave(Personaje enfermo) {
        if (nivelEnergia < ENERGIA_CREAR_MEDICAMENTO) {
            System.out.println(nombre + " no tiene suficiente energía para aplicar el tratamiento.");
            return false;
        }

        if (!enfermo.isEnfermo() || !enfermo.isEnfermedadGrave()) {
            System.out.println(enfermo.getNombre() + " no tiene una enfermedad grave que requiera tratamiento avanzado.");
            return false;
        }

        Recurso medicamentoAvanzado = buscarRecursoEnInventario("medicamento avanzado");
        
        if (medicamentoAvanzado != null) {
            reducirEnergia(ENERGIA_CREAR_MEDICAMENTO);
            medicamentoAvanzado.usarRecurso(1);
            
            if (medicamentoAvanzado.getCantidad() <= 0) {
                inventario.remove(medicamentoAvanzado);
            }

            enfermo.recuperarSalud(20);
            enfermo.curar();
            mejorarHabilidad(4);
            
            System.out.println(nombre + " ha curado la enfermedad grave de " + 
                enfermo.getNombre() + " usando un medicamento avanzado.");
            return true;
        }
        
        System.out.println(nombre + " no tiene medicamentos avanzados para tratar la enfermedad.");
        return false;
    }

    public void investigar() {
        if (nivelEnergia < ENERGIA_INVESTIGAR) {
            System.out.println(nombre + " no tiene suficiente energía para investigar.");
            return;
        }

        reducirEnergia(ENERGIA_INVESTIGAR);
        mejorarHabilidad(1);
        
        System.out.println(nombre + " ha investigado y mejorado su conocimiento científico.");
    }

    @Override
    public void descansar() {
        int recuperacionBase = 15;
        if (refugioAsignado != null) {
            if (refugioAsignado.getEstabilidad() >= 70) {
                recuperacionBase += 5;
            }
        }
        recuperarEnergia(recuperacionBase);
        System.out.println(nombre + " ha descansado y recuperado " + 
            recuperacionBase + " puntos de energía.");
    }

    private Recurso buscarRecursoEnInventario(String tipo) {
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipo) && recurso.getCantidad() > 0) {
                return recurso;
            }
        }
        return null;
    }

    private void mejorarHabilidad(int cantidad) {
        habilidadCiencia += cantidad;
        if (habilidadCiencia > 100) {
            habilidadCiencia = 100;
        }
        System.out.println(nombre + " ha mejorado su habilidad científica a " + habilidadCiencia);
    }

    private void agregarRecursoAInventario(String tipo, int cantidad) {
        boolean encontrado = false;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipo)) {
                recurso.agregarRecurso(cantidad);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            inventario.add(new Recurso(tipo, cantidad));
        }
    }

    public int getHabilidadCiencia() {
        return habilidadCiencia;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder(super.toString());
        info.append("\nHabilidad científica: ").append(habilidadCiencia);
        info.append("\nInventario científico:");
        
        int plantasMedicinales = 0;
        int medicamentosAvanzados = 0;
        
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("planta medicinal")) {
                plantasMedicinales = recurso.getCantidad();
            } else if (recurso.getTipo().equals("medicamento avanzado")) {
                medicamentosAvanzados = recurso.getCantidad();
            }
        }
        
        info.append("\n- Plantas medicinales: ").append(plantasMedicinales);
        info.append("\n- Medicamentos avanzados: ").append(medicamentosAvanzados);
        
        return info.toString();
    }
}