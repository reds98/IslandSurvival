package model;

import java.util.Random;

public class Constructor extends Personaje {
    private int habilidadConstruccion;
    private Random random = new Random();

    public Constructor(String nombre) {
        super(nombre);
        this.habilidadConstruccion = 50; // Valor inicial
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
        int energiaRecuperada = 0;
        if (tipoComida.equals("carne") || tipoComida.equals("fruta")) {
            energiaRecuperada = 10 + random.nextInt(11); // 10-20 puntos
        }
        
        if (consumirRecurso(tipoComida, 1)) {
            recuperarEnergia(energiaRecuperada);
            System.out.println(nombre + " ha comido " + tipoComida + " y recuperado " + energiaRecuperada + " puntos de energía.");
        } else {
            System.out.println("No hay " + tipoComida + " disponible para comer.");
        }
    }

    public void construirRefugio() {
        int energiaGastada = 20;
        reducirEnergia(energiaGastada);

        if (consumirRecurso("madera", 5) && consumirRecurso("piedra", 3)) {  // Reducimos los costos
            Refugio nuevoRefugio = new Refugio(4); // Capacidad para 4 personas
            setRefugioAsignado(nuevoRefugio);
            habilidadConstruccion += 3;
            System.out.println(nombre + " ha construido un nuevo refugio.");
        } else {
            System.out.println(nombre + " no tiene suficientes materiales para construir un refugio.");
        }
    }

    public void repararRefugio() {
        if (refugioAsignado == null) {
            System.out.println(nombre + " no tiene un refugio asignado para reparar.");
            return;
        }

        int energiaGastada = 15;
        reducirEnergia(energiaGastada);

        int materialesNecesarios = (100 - refugioAsignado.getEstabilidad()) / 10;
        if (consumirRecurso("madera", materialesNecesarios)) {
            int reparacion = 10 * materialesNecesarios;
            refugioAsignado.reparar(reparacion);
            habilidadConstruccion += 2;
            System.out.println(nombre + " ha reparado el refugio, aumentando su estabilidad en " + reparacion + " puntos.");
        } else {
            System.out.println(nombre + " no tiene suficientes materiales para reparar el refugio.");
        }
    }

    @Override
    public void descansar() {
        if (refugioAsignado != null) {
            recuperarEnergia(20);
            System.out.println(nombre + " ha descansado en el refugio y recuperado 20 puntos de energía.");
        } else {
            super.descansar();
        }
    }

    public int getHabilidadConstruccion() {
        return habilidadConstruccion;
    }

    @Override
    public String toString() {
        return super.toString() + "\nHabilidad de construcción: " + habilidadConstruccion;
    }
}