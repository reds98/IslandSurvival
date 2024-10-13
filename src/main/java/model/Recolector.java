package model;

import java.util.Random;

public class Recolector extends Personaje {
    private int habilidadRecoleccion;
    private Random random = new Random();

    public Recolector(String nombre) {
        super(nombre);
        this.habilidadRecoleccion = 50; // Valor inicial
    }

    @Override
    public void accionar() {
        recolectar();
    }

     @Override
    public void comer(String tipoComida) {
        if (tipoComida.equals("fruta")) {
            int energiaRecuperada = 10 + random.nextInt(6); // 10-15 puntos
            
            // Buscar en el inventario personal
            for (Recurso recurso : inventario) {
                if (recurso.getTipo().equals(tipoComida) && recurso.getCantidad() > 0) {
                    recurso.usarRecurso(1);
                    recuperarEnergia(energiaRecuperada);
                    System.out.println(nombre + " ha comido una fruta y recuperado " + energiaRecuperada + " puntos de energía.");
                    
                    // Remover el recurso si se acabó
                    if (recurso.getCantidad() <= 0) {
                        inventario.remove(recurso);
                    }
                    return;
                }
            }
            System.out.println(nombre + " no tiene frutas en su inventario para comer.");
        } else {
            super.comer(tipoComida);
        }
    }

   public void recolectar() {
    if (nivelEnergia >= 10) {  // Verificar si tiene suficiente energía
        int energiaGastada = 10;
        reducirEnergia(energiaGastada);
        
        // Siempre tiene éxito si tiene energía
        agregarRecursoGlobal("fruta", 2);
        agregarRecursoGlobal("madera", 1);
        System.out.println(nombre + " ha recolectado recursos con éxito.");
    } else {
        System.out.println(nombre + " no tiene suficiente energía para recolectar.");
    }
}

   public void entregarRecurso(Personaje receptor, String tipoRecurso, int cantidad) {
        // Buscar el recurso en el inventario personal
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipoRecurso) && recurso.getCantidad() >= cantidad) {
                recurso.usarRecurso(cantidad);
                Recurso recursoEntregado = new Recurso(tipoRecurso, cantidad);
                receptor.recibirRecurso(recursoEntregado);
                
                // Remover el recurso si se acabó
                if (recurso.getCantidad() <= 0) {
                    inventario.remove(recurso);
                }
                
                System.out.println(nombre + " ha entregado " + cantidad + " " + tipoRecurso + "(s) a " + receptor.getNombre() + ".");
                return;
            }
        }
        System.out.println(nombre + " no tiene suficientes " + tipoRecurso + " para entregar.");
    }

    @Override
    public void descansar() {
        if (refugioAsignado != null && refugioAsignado.getEstabilidad() > 50) {
            recuperarEnergia(15);
            System.out.println(nombre + " ha descansado en un refugio seguro y recuperado 15 puntos de energía.");
        } else {
            super.descansar();
        }
    }

    public int getHabilidadRecoleccion() {
        return habilidadRecoleccion;
    }

    public void mejorarHabilidadRecoleccion(int cantidad) {
        this.habilidadRecoleccion += cantidad;
        if (this.habilidadRecoleccion > 100) {
            this.habilidadRecoleccion = 100;
        }
        System.out.println(nombre + " ha mejorado su habilidad de recolección a " + this.habilidadRecoleccion);
    }

    @Override
    public String toString() {
        return super.toString() + "\nHabilidad de recolección: " + habilidadRecoleccion;
    }
}