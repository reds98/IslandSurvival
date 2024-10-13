package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Personaje {
    protected String nombre;
    protected int nivelEnergia;
    protected int nivelSalud;
    protected List<Recurso> inventario;
    protected Refugio refugioAsignado;
    protected static RecursosManager recursosManager = RecursosManager.getInstance();

    public Personaje(String nombre) {
        this.nombre = nombre;
        this.nivelEnergia = 100;
        this.nivelSalud = 100;
        this.inventario = new ArrayList<>();
    }

    public abstract void accionar();
    
    public void comer(String tipoComida) {
        int energiaRecuperada = 10;
        if (consumirRecurso(tipoComida, 1)) {
            recuperarEnergia(energiaRecuperada);
            System.out.println(nombre + " ha comido " + tipoComida + " y recuperado " + energiaRecuperada + " puntos de energía.");
        } else {
            System.out.println("No hay " + tipoComida + " disponible para comer.");
        }
    }

   public void descansar() {
        if (refugioAsignado != null) {
            nivelEnergia += 25; // Aumentamos la recuperación de energía en el refugio
            if (nivelEnergia > 100) nivelEnergia = 100;
            System.out.println(nombre + " ha descansado en el refugio y recuperado 25 puntos de energía.");
        } else {
            nivelEnergia += 10;
            if (nivelEnergia > 100) nivelEnergia = 100;
            System.out.println(nombre + " ha descansado sin refugio y recuperado 10 puntos de energía.");
        }
    }

    public void reducirEnergia(int cantidad) {
        nivelEnergia -= cantidad;
        if (nivelEnergia < 0) nivelEnergia = 0;
        System.out.println(nombre + " ha perdido " + cantidad + " puntos de energía.");
    }

    public void reducirSalud(int cantidad) {
        nivelSalud -= cantidad;
        if (nivelSalud < 0) nivelSalud = 0;
        System.out.println(nombre + " ha perdido " + cantidad + " puntos de salud.");
    }

    public void recuperarEnergia(int cantidad) {
        nivelEnergia += cantidad;
        if (nivelEnergia > 100) nivelEnergia = 100;
        System.out.println(nombre + " ha recuperado " + cantidad + " puntos de energía.");
    }

    public void recuperarSalud(int cantidad) {
        nivelSalud += cantidad;
        if (nivelSalud > 100) nivelSalud = 100;
        System.out.println(nombre + " ha recuperado " + cantidad + " puntos de salud.");
    }

    protected boolean consumirRecurso(String tipo, int cantidad) {
        return recursosManager.usarRecurso(tipo, cantidad);
    }

    protected void agregarRecursoGlobal(String tipo, int cantidad) {
        recursosManager.agregarRecurso(tipo, cantidad);
    }

    public void recibirRecurso(Recurso recurso) {
        inventario.add(recurso);
        System.out.println(nombre + " ha recibido " + recurso.getCantidad() + " " + recurso.getTipo() + "(s).");
    }

    public void compartirRecurso(Personaje receptor, Recurso recurso) {
        if (inventario.remove(recurso)) {
            receptor.recibirRecurso(recurso);
            System.out.println(nombre + " ha compartido " + recurso.getCantidad() + " " + recurso.getTipo() + "(s) con " + receptor.getNombre() + ".");
        } else {
            System.out.println(nombre + " no tiene " + recurso.getTipo() + " para compartir.");
        }
    }
    

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public int getNivelEnergia() {
        return nivelEnergia;
    }

    public int getNivelSalud() {
        return nivelSalud;
    }

    public List<Recurso> getInventario() {
        return new ArrayList<>(inventario);
    }

    public Refugio getRefugioAsignado() {
        return refugioAsignado;
    }

    public void setRefugioAsignado(Refugio refugioAsignado) {
        this.refugioAsignado = refugioAsignado;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + "\n" +
               "Nivel de Energía: " + nivelEnergia + "\n" +
               "Nivel de Salud: " + nivelSalud + "\n" +
               "Inventario: " + inventarioToString();
    }

    private String inventarioToString() {
        if (inventario.isEmpty()) {
            return "Vacío";
        }
        StringBuilder sb = new StringBuilder();
        for (Recurso recurso : inventario) {
            sb.append(recurso.getTipo()).append(": ").append(recurso.getCantidad()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
    
}