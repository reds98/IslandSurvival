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
        // Buscar el recurso en el inventario personal
        Recurso recursoComida = null;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipoComida)) {
                recursoComida = recurso;
                break;
            }
        }

        if (recursoComida != null && recursoComida.getCantidad() > 0) {
            int energiaRecuperada;
            if (tipoComida.equals("carne")) {
                energiaRecuperada = 20; // Los cazadores recuperan más energía con carne
            } else if (tipoComida.equals("fruta")) {
                energiaRecuperada = 10;
            } else {
                return; // Tipo de comida no válido
            }

            // Consumir la comida
            recursoComida.usarRecurso(1);
            recuperarEnergia(energiaRecuperada);
            System.out.println(nombre + " ha comido " + tipoComida + " y recuperado " + 
                             energiaRecuperada + " puntos de energía.");

            // Si se acabó el recurso, removerlo del inventario
            if (recursoComida.getCantidad() <= 0) {
                inventario.remove(recursoComida);
            }
        } else {
            System.out.println(nombre + " no tiene " + tipoComida + " en su inventario para comer.");
        }
    }

    public boolean cazar() {
        if (nivelEnergia >= 25) {  // Verificar si tiene suficiente energía
            int energiaGastada = 25;
            reducirEnergia(energiaGastada);
            
            // Siempre tiene éxito si tiene energía
            int cantidadCarne = 2; // Cantidad fija de carne
            agregarRecursoAInventario("carne", cantidadCarne);
            
            habilidadCaza += 2;
            if (habilidadCaza > 100) habilidadCaza = 100;
            
            System.out.println(nombre + " ha cazado con éxito y obtenido " + cantidadCarne + 
                             " unidades de carne.");
            return true;
        } else {
            System.out.println(nombre + " no tiene suficiente energía para cazar.");
            return false;
        }
    }

    // Método para manejar la adición de recursos al inventario
    private void agregarRecursoAInventario(String tipo, int cantidad) {
        if (cantidad <= 0) return;

        // Buscar si ya existe el recurso
        boolean encontrado = false;
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipo)) {
                recurso.agregarRecurso(cantidad); // Sumamos al existente
                encontrado = true;
                break;
            }
        }

        // Si no se encontró, crear nuevo recurso
        if (!encontrado) {
            inventario.add(new Recurso(tipo, cantidad));
        }
    }

    public boolean defender(Personaje personaje, Animal animal) {
        int energiaGastada = 25;
        reducirEnergia(energiaGastada);
        
        double probabilidadExito = (habilidadCaza / 100.0) * 0.8;
        boolean exito = random.nextDouble() < probabilidadExito;
        
        if (exito) {
            System.out.println(nombre + " ha defendido con éxito a " + personaje.getNombre() + 
                             " del ataque de un " + animal.getTipoAnimal());
            habilidadCaza += 3;
            if (habilidadCaza > 100) habilidadCaza = 100;
        } else {
            int danoRecibido = 10 + random.nextInt(11);
            reducirSalud(danoRecibido);
            personaje.reducirSalud(danoRecibido / 2);
            System.out.println(nombre + " no pudo defender a " + personaje.getNombre() + 
                             " y ambos resultaron heridos.");
        }
        
        return exito;
    }

    public int getHabilidadCaza() {
        return habilidadCaza;
    }

    @Override
    public String toString() {
        return super.toString() + "\nHabilidad de caza: " + habilidadCaza;
    }
}