package model;

import java.util.Random;

public class Cazador extends Personaje {
    private int habilidadCaza;
    private Random random = new Random();

    public Cazador(String nombre) {
        super(nombre);
        this.habilidadCaza = 50; // Valor inicial
    }

    @Override
    public void accionar() {
        cazar();
    }

    @Override
    public void comer(String tipoComida) {
        int energiaRecuperada = 0;
        if (tipoComida.equals("carne")) {
            energiaRecuperada = 20; // Los cazadores recuperan más energía con carne
        } else if (tipoComida.equals("fruta")) {
            energiaRecuperada = 10;
        }
        
        if (consumirRecurso(tipoComida, 1)) {
            recuperarEnergia(energiaRecuperada);
            System.out.println(nombre + " ha comido " + tipoComida + " y recuperado " + energiaRecuperada + " puntos de energía.");
        } else {
            System.out.println("No hay " + tipoComida + " disponible para comer.");
        }
    }

    public boolean cazar() {
        int energiaGastada = 25 + random.nextInt(11); // 25-35 puntos
        reducirEnergia(energiaGastada);
        
        double probabilidadExito = (habilidadCaza / 100.0) * 0.7; // Máximo 70% de probabilidad de éxito
        boolean exito = random.nextDouble() < probabilidadExito;
        
        if (exito) {
            int cantidadCarne = 1 + random.nextInt(3); // 1-3 unidades de carne
            agregarRecursoGlobal("carne", cantidadCarne);
            habilidadCaza += 2;
            System.out.println(nombre + " ha cazado con éxito y obtenido " + cantidadCarne + " unidades de carne.");
        } else {
            System.out.println(nombre + " no ha tenido éxito en la caza.");
        }
        
        return exito;
    }

    public void defender(Personaje personaje, Animal animal) {
        int energiaGastada = 25;
        reducirEnergia(energiaGastada);
        
        double probabilidadExito = (habilidadCaza / 100.0) * 0.8; // Máximo 80% de probabilidad de éxito en defensa
        boolean exito = random.nextDouble() < probabilidadExito;
        
        if (exito) {
            System.out.println(nombre + " ha defendido con éxito a " + personaje.getNombre() + " del ataque de un " + animal.getTipoAnimal());
            habilidadCaza += 3;
        } else {
            int danoRecibido = 10 + random.nextInt(11); // 10-20 puntos de daño
            reducirSalud(danoRecibido);
            personaje.reducirSalud(danoRecibido / 2); // El personaje defendido recibe la mitad del daño
            System.out.println(nombre + " no pudo defender a " + personaje.getNombre() + " y ambos resultaron heridos.");
        }
    }

    public int getHabilidadCaza() {
        return habilidadCaza;
    }

    @Override
    public String toString() {
        return super.toString() + "\nHabilidad de caza: " + habilidadCaza;
    }
}