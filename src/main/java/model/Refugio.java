package model;

import java.util.ArrayList;
import java.util.List;

public class Refugio {
    private int estabilidad;
    private int capacidad;
    private List<Recurso> materialesNecesarios;
    private List<Personaje> ocupantes;

    public Refugio(int capacidad) {
        this.estabilidad = 100;
        this.capacidad = capacidad;
        this.materialesNecesarios = new ArrayList<>();
        this.ocupantes = new ArrayList<>();
        
        materialesNecesarios.add(new Recurso("madera", 5));
        materialesNecesarios.add(new Recurso("piedra", 3));
    }

    public boolean añadirPersonaje(Personaje personaje) {
        if (ocupantes.size() < capacidad) {
            ocupantes.add(personaje);
            personaje.setRefugioAsignado(this);
            System.out.println(personaje.getNombre() + " ha entrado al refugio.");
            return true;
        } else {
            System.out.println("El refugio está lleno. No se puede añadir a " + personaje.getNombre() + ".");
            return false;
        }
    }

    public boolean eliminarPersonaje(Personaje personaje) {
        if (ocupantes.remove(personaje)) {
            personaje.setRefugioAsignado(null);
            System.out.println(personaje.getNombre() + " ha salido del refugio.");
            return true;
        } else {
            System.out.println(personaje.getNombre() + " no estaba en este refugio.");
            return false;
        }
    }

    public void reparar(int cantidadReparacion) {
        estabilidad += cantidadReparacion;
        if (estabilidad > 100) {
            estabilidad = 100;
        }
        System.out.println("El refugio ha sido reparado. Nueva estabilidad: " + estabilidad);
    }

    public void desgastar() {
        estabilidad -= 1;
        if (estabilidad < 0) {
            estabilidad = 0;
        }
        System.out.println("El refugio se ha desgastado. Estabilidad actual: " + estabilidad);
    }

    public int evaluarEstabilidad() {
        if (estabilidad < 30) {
            System.out.println("El refugio está en mal estado y necesita reparaciones urgentes.");
        } else if (estabilidad < 70) {
            System.out.println("El refugio está en condiciones aceptables, pero podría beneficiarse de algunas reparaciones.");
        } else {
            System.out.println("El refugio está en buenas condiciones.");
        }
        return estabilidad;
    }

    // Getters y setters
    public int getEstabilidad() {
        return estabilidad;
    }

    public void setEstabilidad(int estabilidad) {
        this.estabilidad = estabilidad;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public List<Recurso> getMaterialesNecesarios() {
        return new ArrayList<>(materialesNecesarios);
    }

    public List<Personaje> getOcupantes() {
        return new ArrayList<>(ocupantes);
    }

    @Override
    public String toString() {
        return "Refugio - Estabilidad: " + estabilidad + 
               ", Capacidad: " + capacidad + 
               ", Ocupantes: " + ocupantes.size() + 
               ", Materiales necesarios para reparación: " + materialesNecesarios;
    }
}