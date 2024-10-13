package model;

import java.util.Random;

public class Constructor extends Personaje {
    private int habilidadConstruccion;
    private Random random = new Random();

    public Constructor(String nombre) {
        super(nombre);
        this.habilidadConstruccion = 50;
    }

    @Override
    public void accionar() {
        if (refugioAsignado == null) {
            construirRefugio();
        } else {
            repararRefugio();
        }
    }

    @Override
    public void comer(String tipoComida) {
        Recurso comida = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipoComida) && recurso.getCantidad() > 0) {
                comida = recurso;
                break;
            }
        }

        if (comida != null) {
            int energiaRecuperada;
            if (tipoComida.equals("carne")) {
                energiaRecuperada = 20;
            } else if (tipoComida.equals("fruta")) {
                energiaRecuperada = 15;
            } else {
                return;
            }

            comida.usarRecurso(1);
            recuperarEnergia(energiaRecuperada);
            
            if (comida.getCantidad() <= 0) {
                inventario.remove(comida);
            }
        }
    }

    public boolean construirRefugio() {
        // Verificar si tiene suficiente energía
        if (nivelEnergia < 20) {
            System.out.println(nombre + " no tiene suficiente energía para construir.");
            return false;
        }

        // Verificar materiales en el inventario
        Recurso madera = null;
        Recurso piedra = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("madera") && recurso.getCantidad() >= 5) {
                madera = recurso;
            }
            if (recurso.getTipo().equals("piedra") && recurso.getCantidad() >= 3) {
                piedra = recurso;
            }
        }

        if (madera != null && piedra != null) {
            // Construir el refugio
            madera.usarRecurso(5);
            piedra.usarRecurso(3);
            
            // Remover recursos si se agotaron
            if (madera.getCantidad() <= 0) inventario.remove(madera);
            if (piedra.getCantidad() <= 0) inventario.remove(piedra);

            reducirEnergia(20);
            
            Refugio nuevoRefugio = new Refugio(4); // Capacidad para 4 personas
            refugioAsignado = nuevoRefugio;
            
            habilidadConstruccion += 3;
            if (habilidadConstruccion > 100) habilidadConstruccion = 100;
            
            return true;
        }
        
        return false;
    }

    public boolean repararRefugio() {
        if (refugioAsignado == null || nivelEnergia < 15) {
            return false;
        }

        // Buscar madera en el inventario
        Recurso madera = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals("madera") && recurso.getCantidad() >= 2) {
                madera = recurso;
                break;
            }
        }

        if (madera != null) {
            madera.usarRecurso(2);
            if (madera.getCantidad() <= 0) {
                inventario.remove(madera);
            }

            reducirEnergia(15);
            
            int reparacion = 20 + (habilidadConstruccion / 5); // Entre 20 y 40 puntos
            refugioAsignado.reparar(reparacion);
            
            habilidadConstruccion += 2;
            if (habilidadConstruccion > 100) habilidadConstruccion = 100;
            
            return true;
        }
        
        return false;
    }

    public int getHabilidadConstruccion() {
        return habilidadConstruccion;
    }

    @Override
    public String toString() {
        return super.toString() + "\nHabilidad de construcción: " + habilidadConstruccion;
    }
}