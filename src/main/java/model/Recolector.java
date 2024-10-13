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
            if (consumirRecurso(tipoComida, 1)) {
                recuperarEnergia(energiaRecuperada);
                System.out.println(nombre + " ha comido una fruta y recuperado " + energiaRecuperada + " puntos de energía.");
            } else {
                System.out.println("No hay frutas disponibles para comer.");
            }
        } else {
            super.comer(tipoComida);
        }
    }

    public void recolectar() {
        int energiaGastada = 10 + random.nextInt(11); // 10-20 puntos
        reducirEnergia(energiaGastada);

        double probabilidadExito = (habilidadRecoleccion / 100.0) * 0.8; // Máximo 80% de probabilidad de éxito
        boolean exito = random.nextDouble() < probabilidadExito;

        if (exito) {
            String[] tiposRecursos = {"fruta", "agua", "madera", "planta medicinal"};
            String tipoRecurso = tiposRecursos[random.nextInt(tiposRecursos.length)];
            int cantidad = 1 + random.nextInt(3); // 1-3 unidades

            agregarRecursoGlobal(tipoRecurso, cantidad);
            habilidadRecoleccion += 2;
            System.out.println(nombre + " ha recolectado " + cantidad + " " + tipoRecurso + "(s).");
        } else {
            System.out.println(nombre + " no ha tenido éxito en la recolección.");
        }
    }

    public void entregarRecurso(Personaje receptor, String tipoRecurso, int cantidad) {
        if (consumirRecurso(tipoRecurso, cantidad)) {
            receptor.recibirRecurso(new Recurso(tipoRecurso, cantidad));
            System.out.println(nombre + " ha entregado " + cantidad + " " + tipoRecurso + "(s) a " + receptor.getNombre() + ".");
        } else {
            System.out.println("No hay suficientes " + tipoRecurso + " para entregar.");
        }
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