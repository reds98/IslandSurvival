package model;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Explorador extends Personaje {
    private int nivelExploracion;
    private Random random = new Random();

    public Explorador(String nombre) {
        super(nombre);
        this.nivelExploracion = 50; // Valor inicial
    }

    @Override
    public void accionar() {
        explorar();
    }

    @Override
    public void comer(String tipoComida) {
        int energiaRecuperada = 0;
        if (tipoComida.equals("fruta")) {
            energiaRecuperada = 10 + random.nextInt(6); // 10-15 puntos
        } else if (tipoComida.equals("carne")) {
            energiaRecuperada = 15 + random.nextInt(6); // 15-20 puntos
        }
        
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipoComida)) {
                recuperarEnergia(energiaRecuperada);
                recurso.usarRecurso(1);
                if (recurso.getCantidad() == 0) {
                    inventario.remove(recurso);
                }
                System.out.println(nombre + " ha comido " + tipoComida + " y recuperado " + energiaRecuperada + " puntos de energía.");
                break;
            }
        }
    }

     public void explorar() {
        int energiaGastada = 15 + random.nextInt(16); // 15-30 puntos
        reducirEnergia(energiaGastada);
        nivelExploracion += 2;

        System.out.println(nombre + " ha explorado el área. Energía gastada: " + energiaGastada);
        System.out.println("Nivel de exploración aumentado a: " + nivelExploracion);

        // La exploración siempre descubre un área, pero no siempre encuentra recursos o animales
        if (random.nextDouble() < 0.7) { // 70% de probabilidad de encontrar algo
            List<Recurso> recursosEncontrados = descubrirRecursos();
            System.out.println(nombre + " ha encontrado recursos durante la exploración:");
            for (Recurso recurso : recursosEncontrados) {
                System.out.println("- " + recurso.getCantidad() + " " + recurso.getTipo());
            }
        }
        
        if (random.nextDouble() < 0.3) { // 30% de probabilidad de encontrar un animal
            Animal animalEncontrado = descubrirAnimal();
            System.out.println(nombre + " ha encontrado un " + animalEncontrado.getTipoAnimal() + " durante la exploración.");
        }
    }
     public Animal descubrirAnimal() {
        String[] tiposAnimales = {"conejo", "ciervo", "lobo", "oso"};
        String tipoAnimal = tiposAnimales[new Random().nextInt(tiposAnimales.length)];
        return new Animal(tipoAnimal);
    }

    public List<Recurso> descubrirRecursos() {
        List<Recurso> recursosEncontrados = new ArrayList<>();
        int cantidadRecursos = 1 + random.nextInt(3); // 1-3 recursos

        for (int i = 0; i < cantidadRecursos; i++) {
            String[] tiposRecursos = {"fruta", "madera", "planta medicinal", "piedra", "agua"};
            String tipoRecurso = tiposRecursos[random.nextInt(tiposRecursos.length)];
            int cantidad = 1 + random.nextInt(3); // 1-3 unidades

            Recurso recursoEncontrado = new Recurso(tipoRecurso, cantidad);
            recursosEncontrados.add(recursoEncontrado);

            // Costo adicional de energía por recolectar
            reducirEnergia(5);
        }

        return recursosEncontrados;
    }

    public void recolectar(Recurso recurso) {
        inventario.add(recurso);
        reducirEnergia(5);
        System.out.println(nombre + " ha recolectado " + recurso.getCantidad() + " " + recurso.getTipo() + "(s).");
    }

    @Override
    public void descansar() {
        if (refugioAsignado != null && refugioAsignado.getEstabilidad() > 50) {
            recuperarEnergia(20);
            System.out.println(nombre + " ha descansado en un refugio estable y recuperado 20 puntos de energía.");
        } else {
            recuperarEnergia(10);
            System.out.println(nombre + " ha descansado pero no en un refugio estable. Ha recuperado 10 puntos de energía.");
        }
    }

    public int getNivelExploracion() {
        return nivelExploracion;
    }

    public String getEstadisticas() {
        return "Estadísticas de " + nombre + ":\n" +
               "Nivel de Energía: " + nivelEnergia + "\n" +
               "Nivel de Salud: " + nivelSalud + "\n" +
               "Nivel de Exploración: " + nivelExploracion + "\n" +
               "Inventario: " + inventarioToString();
    }

    private String inventarioToString() {
        StringBuilder sb = new StringBuilder();
        for (Recurso recurso : inventario) {
            sb.append(recurso.getTipo()).append(": ").append(recurso.getCantidad()).append(", ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "Vacío";
    }
}