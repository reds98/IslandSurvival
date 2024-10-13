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
    protected boolean enfermo;
    protected boolean enfermedadGrave;
    protected String tipoEnfermedad;
    

    public Personaje(String nombre) {
        this.nombre = nombre;
        this.nivelEnergia = 100;
        this.nivelSalud = 100;
        this.inventario = new ArrayList<>();
        this.enfermo = false;
        this.enfermedadGrave = false;
        this.tipoEnfermedad = null;
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
        int recuperacion = 15;
        if (enfermo) {
            recuperacion = enfermedadGrave ? 5 : 10;
        }
        
        if (refugioAsignado != null) {
            if (refugioAsignado.getEstabilidad() >= 70) {
                recuperacion += 5;
            }
        }
        
        recuperarEnergia(recuperacion);
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
    // Buscar si ya existe un recurso del mismo tipo
    boolean encontrado = false;
    for (Recurso r : inventario) {
        if (r.getTipo().equals(recurso.getTipo())) {
            // Si existe, sumar la cantidad
            r.agregarRecurso(recurso.getCantidad());
            encontrado = true;
            break;
        }
    }
    
    // Si no se encontró el tipo de recurso, añadirlo como nuevo
    if (!encontrado) {
        inventario.add(recurso);
    }
    
    System.out.println(nombre + " ha recibido " + recurso.getCantidad() + " " + 
                      recurso.getTipo() + "(s).");
}

    public void compartirRecurso(Personaje receptor, Recurso recurso) {
        if (inventario.remove(recurso)) {
            receptor.recibirRecurso(recurso);
            System.out.println(nombre + " ha compartido " + recurso.getCantidad() + " " + recurso.getTipo() + "(s) con " + receptor.getNombre() + ".");
        } else {
            System.out.println(nombre + " no tiene " + recurso.getTipo() + " para compartir.");
        }
    }
    
    public void curar() {
        this.enfermo = false;
        this.enfermedadGrave = false;
        this.tipoEnfermedad = null;
        System.out.println(nombre + " ha sido curado de su enfermedad");
    }
    
    public void contraerEnfermedad(String tipo, boolean grave) {
        this.enfermo = true;
        this.enfermedadGrave = grave;
        this.tipoEnfermedad = tipo;
        System.out.println(nombre + " ha contraído " + tipo + (grave ? " (grave)" : " (leve)"));
    }
    

    // Getters y setters
    
     public String getTipoEnfermedad() {
        return tipoEnfermedad;
    }
     public boolean isEnfermo() {
        return enfermo;
    }
    
    public void setEnfermo(boolean enfermo) {
        this.enfermo = enfermo;
    }
    
    public boolean isEnfermedadGrave() {
        return enfermedadGrave;
    }
    
    public void setEnfermedadGrave(boolean enfermedadGrave) {
        this.enfermedadGrave = enfermedadGrave;
    }
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
        StringBuilder info = new StringBuilder();
        info.append("Nombre: ").append(nombre).append("\n");
        info.append("Nivel de Energía: ").append(nivelEnergia).append("\n");
        info.append("Nivel de Salud: ").append(nivelSalud).append("\n");
        if (enfermo) {
            info.append("Estado: Enfermo - ").append(tipoEnfermedad)
                .append(enfermedadGrave ? " (grave)" : " (leve)").append("\n");
        }
        info.append("Inventario: ").append(inventarioToString());
        return info.toString();
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