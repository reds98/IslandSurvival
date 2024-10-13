package model;

import java.util.Random;

public class Curandero extends Personaje {
    private int habilidadCurar;
    private Random random = new Random();
    private static final int ENERGIA_CURAR = 10;
    private static final int ENERGIA_PREPARAR_REMEDIO = 15;
    private static final int PLANTAS_NECESARIAS_REMEDIO = 2;
    private static final int COSTO_RECOLECTAR = 10;

    public Curandero(String nombre) {
        super(nombre);
        this.habilidadCurar = 50;
    }

    @Override
    public void accionar() {
        // El curandero intentará curar a personajes enfermos cercanos si tiene los recursos
        if (buscarRecursoEnInventario("remedio") != null || 
            buscarRecursoEnInventario("planta medicinal") != null) {
            System.out.println(nombre + " está listo para curar a otros personajes.");
        } else if (nivelEnergia >= COSTO_RECOLECTAR) {
            System.out.println(nombre + " buscará plantas medicinales.");
        }
    }

    @Override
    public void comer(String tipoComida) {
        int energiaRecuperada = 0;
        
        switch (tipoComida) {
            case "planta medicinal":
                energiaRecuperada = 25; // Mayor recuperación con plantas medicinales
                break;
            case "fruta":
                energiaRecuperada = 15;
                break;
            case "carne":
                energiaRecuperada = 15;
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

    public boolean curar(Personaje enfermo) {
        if (!tieneEnergiaParaCurar()) {
            System.out.println(nombre + " no tiene suficiente energía para curar.");
            return false;
        }

        if (!enfermo.isEnfermo()) {
            System.out.println(enfermo.getNombre() + " no está enfermo y no necesita curación.");
            return false;
        }

        if (enfermo.isEnfermedadGrave()) {
            System.out.println("La enfermedad es demasiado grave para ser curada con remedios básicos.");
            return false;
        }

        // Intentar usar primero un remedio, si no hay, usar planta medicinal
        Recurso remedio = buscarRecursoEnInventario("remedio");
        Recurso plantaMedicinal = buscarRecursoEnInventario("planta medicinal");

        if (remedio != null || plantaMedicinal != null) {
            reducirEnergia(ENERGIA_CURAR);
            
            int cantidadCurada = calcularCantidadCurada(remedio != null);
            enfermo.recuperarSalud(cantidadCurada);
            
            if (remedio != null) {
                remedio.usarRecurso(1);
                if (remedio.getCantidad() <= 0) {
                    inventario.remove(remedio);
                }
            } else {
                plantaMedicinal.usarRecurso(1);
                if (plantaMedicinal.getCantidad() <= 0) {
                    inventario.remove(plantaMedicinal);
                }
            }

            enfermo.curar();
            mejorarHabilidad(3);
            
            System.out.println(nombre + " ha curado a " + enfermo.getNombre() + 
                " recuperando " + cantidadCurada + " puntos de salud.");
            return true;
        }
        
        System.out.println(nombre + " no tiene remedios ni plantas medicinales para curar.");
        return false;
    }

    public boolean prepararRemedio() {
        if (nivelEnergia < ENERGIA_PREPARAR_REMEDIO) {
            System.out.println(nombre + " no tiene suficiente energía para preparar un remedio.");
            return false;
        }

        Recurso plantaMedicinal = buscarRecursoEnInventario("planta medicinal");
        
        if (plantaMedicinal != null && plantaMedicinal.getCantidad() >= PLANTAS_NECESARIAS_REMEDIO) {
            reducirEnergia(ENERGIA_PREPARAR_REMEDIO);
            plantaMedicinal.usarRecurso(PLANTAS_NECESARIAS_REMEDIO);
            
            if (plantaMedicinal.getCantidad() <= 0) {
                inventario.remove(plantaMedicinal);
            }

            // Probabilidad de éxito basada en la habilidad
            double efectividad = 0.5 + (habilidadCurar / 200.0); // Entre 0.5 y 1.0
            if (random.nextDouble() < efectividad) {
                agregarRecursoAInventario("remedio", 1);
                mejorarHabilidad(2);
                System.out.println(nombre + " ha preparado un remedio exitosamente.");
                return true;
            } else {
                System.out.println(nombre + " falló al preparar el remedio.");
                return false;
            }
        }
        
        System.out.println(nombre + " necesita al menos " + PLANTAS_NECESARIAS_REMEDIO + 
            " plantas medicinales para preparar un remedio.");
        return false;
    }

    public boolean recolectarPlantasMedicinales(int cantidadDisponible) {
    if (nivelEnergia < COSTO_RECOLECTAR) {
        System.out.println(nombre + " no tiene suficiente energía para recolectar.");
        return false;
    }

    reducirEnergia(COSTO_RECOLECTAR);
    
    // La cantidad recolectada depende de la habilidad y la cantidad disponible
    double probabilidadExito = habilidadCurar / 100.0;
    if (random.nextDouble() < probabilidadExito) {
        // Determinar cuánto recolecta (entre 1 y la cantidad disponible)
        int cantidadRecolectada = Math.min(cantidadDisponible, 1 + (habilidadCurar / 50));
        agregarRecursoAInventario("planta medicinal", cantidadRecolectada);
        mejorarHabilidad(1);
        
        System.out.println(nombre + " ha recolectado " + cantidadRecolectada + 
            " planta(s) medicinal(es).");
        return true;
    }
    
    System.out.println(nombre + " no encontró plantas medicinales útiles.");
    return false;
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

    private boolean tieneEnergiaParaCurar() {
        return nivelEnergia >= ENERGIA_CURAR;
    }

    private Recurso buscarRecursoEnInventario(String tipo) {
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipo) && recurso.getCantidad() > 0) {
                return recurso;
            }
        }
        return null;
    }

    private int calcularCantidadCurada(boolean usandoRemedio) {
        // Base: 15-25 con plantas, 25-35 con remedio
        int base = usandoRemedio ? 25 : 15;
        int bonus = habilidadCurar / 10;
        return base + bonus;
    }

    private void mejorarHabilidad(int cantidad) {
        habilidadCurar += cantidad;
        if (habilidadCurar > 100) {
            habilidadCurar = 100;
        }
        System.out.println(nombre + " ha mejorado su habilidad de curación a " + habilidadCurar);
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

    public int getHabilidadCurar() {
        return habilidadCurar;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder(super.toString());
        info.append("\nHabilidad de curación: ").append(habilidadCurar);
        info.append("\nInventario medicinal:");
        
        int plantasMedicinales = 0;
        int remedios = 0;
        
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("planta medicinal")) {
                plantasMedicinales = recurso.getCantidad();
            } else if (recurso.getTipo().equals("remedio")) {
                remedios = recurso.getCantidad();
            }
        }
        
        info.append("\n- Plantas medicinales: ").append(plantasMedicinales);
        info.append("\n- Remedios: ").append(remedios);
        
        return info.toString();
    }
}