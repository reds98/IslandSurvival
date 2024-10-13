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
        
        // Buscar en el inventario personal
        for (Recurso recurso : inventario) {
            if (recurso.getTipo().equals(tipoComida) && recurso.getCantidad() > 0) {
                recurso.usarRecurso(1);
                recuperarEnergia(energiaRecuperada);
                System.out.println(nombre + " ha comido " + tipoComida + " y recuperado " + energiaRecuperada + " puntos de energía.");
                
                // Remover el recurso si se acabó
                if (recurso.getCantidad() <= 0) {
                    inventario.remove(recurso);
                }
                return;
            }
        }
        System.out.println(nombre + " no tiene " + tipoComida + " en su inventario para comer.");
    }

  public void explorar() {
    if (nivelEnergia >= 15) {  // Verificar si tiene suficiente energía
        int energiaGastada = 15;
        reducirEnergia(energiaGastada);
        nivelExploracion += 2;

        System.out.println(nombre + " ha explorado el área. Energía gastada: " + energiaGastada);
        System.out.println("Nivel de exploración aumentado a: " + nivelExploracion);

        // La exploración siempre es exitosa si tiene energía
        List<Recurso> recursosEncontrados = descubrirRecursos();
        System.out.println(nombre + " ha encontrado recursos durante la exploración:");
        for (Recurso recurso : recursosEncontrados) {
            System.out.println("- " + recurso.getCantidad() + " " + recurso.getTipo());
        }
        
        // La posibilidad de encontrar animales se mantiene aleatoria
        if (random.nextDouble() < 0.3) { // 30% de probabilidad de encontrar animal
            Animal animalEncontrado = descubrirAnimal();
            System.out.println(nombre + " ha encontrado un " + animalEncontrado.getTipoAnimal() + " durante la exploración.");
        }
    } else {
        System.out.println(nombre + " no tiene suficiente energía para explorar.");
    }
}
 public Animal descubrirAnimal() {
    // Los animales que puede encontrar son aleatorios pero solo pacíficos
    String[] tiposAnimales = {"conejo", "ciervo"};
    String tipoAnimal = tiposAnimales[random.nextInt(tiposAnimales.length)];
    Animal animalDescubierto = new Animal(tipoAnimal);
    System.out.println(nombre + " ha descubierto un " + tipoAnimal + " en el área.");
    return animalDescubierto;
}
    // En la clase Explorador, modificamos el método descubrirRecursos:
public List<Recurso> descubrirRecursos() {
    List<Recurso> recursosEncontrados = new ArrayList<>();
    if (nivelEnergia >= 5) {  // Verificar si tiene suficiente energía
        // Generar 1-3 recursos aleatorios
        int cantidadRecursos = 1 + random.nextInt(3); // 1-3 recursos
        String[] tiposRecursos = {"fruta", "madera", "planta medicinal", "piedra", "agua"};

        for (int i = 0; i < cantidadRecursos; i++) {
            String tipoRecurso = tiposRecursos[random.nextInt(tiposRecursos.length)];
            int cantidad = 1 + random.nextInt(3); // 1-3 unidades
            Recurso recursoEncontrado = new Recurso(tipoRecurso, cantidad);
            recursosEncontrados.add(recursoEncontrado);
            reducirEnergia(5);
        }
    }
    return recursosEncontrados;
}

    public void recolectar(Recurso recurso) {
        if (nivelEnergia >= 5) {  // Verificar si tiene suficiente energía
            // Buscar si ya existe el recurso en el inventario
            boolean encontrado = false;
            for (Recurso r : inventario) {
                if (r.getTipo().equals(recurso.getTipo())) {
                    r.agregarRecurso(recurso.getCantidad()); // Sumamos al existente
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                inventario.add(new Recurso(recurso.getTipo(), recurso.getCantidad())); // Crear nuevo si no existe
            }

            reducirEnergia(5);
            System.out.println(nombre + " ha recolectado " + recurso.getCantidad() + " " + recurso.getTipo() + "(s).");
        } else {
            System.out.println(nombre + " no tiene suficiente energía para recolectar.");
        }
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