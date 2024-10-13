/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author sahid
 */
public class Animal {
    private String tipoAnimal;
    private int fuerza;
    private int dificultadCaza;
    private int comidaProporcionada;
    private boolean esDepredador;

    public Animal(String tipoAnimal) {
        this.tipoAnimal = tipoAnimal;
        switch (tipoAnimal) {
            case "conejo":
                this.fuerza = 10;
                this.dificultadCaza = 20;
                this.comidaProporcionada = 1;
                this.esDepredador = false;
                break;
            case "ciervo":
                this.fuerza = 30;
                this.dificultadCaza = 50;
                this.comidaProporcionada = 3;
                this.esDepredador = false;
                break;
            case "lobo":
                this.fuerza = 60;
                this.dificultadCaza = 70;
                this.comidaProporcionada = 2;
                this.esDepredador = true;
                break;
            case "oso":
                this.fuerza = 100;
                this.dificultadCaza = 90;
                this.comidaProporcionada = 5;
                this.esDepredador = true;
                break;
            default:
                this.fuerza = 20;
                this.dificultadCaza = 30;
                this.comidaProporcionada = 1;
                this.esDepredador = false;
        }
    }

    public void atacarPersonaje(Personaje personaje) {
        int daño = (int) (fuerza * (Math.random() * 0.5 + 0.5)); // Entre 50% y 100% de la fuerza
        personaje.reducirSalud(daño);
        System.out.println("Un " + tipoAnimal + " ha atacado a " + personaje.getNombre() + 
                          " causando " + daño + " puntos de daño.");
    }

    public boolean serCazado(Cazador cazador) {
        int probabilidadExito = cazador.getHabilidadCaza() - this.dificultadCaza;
        return Math.random() * 100 < probabilidadExito;
    }

    public String getTipoAnimal() {
        return tipoAnimal;
    }

    public int getFuerza() {
        return fuerza;
    }

    public int getDificultadCaza() {
        return dificultadCaza;
    }

    public int getComidaProporcionada() {
        return comidaProporcionada;
    }

    public boolean esDepredador() {
        return esDepredador;
    }
}
