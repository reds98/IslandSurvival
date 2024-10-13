package model;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.awt.Point;

public class Dios {
    private static Dios instance;
    private Random random;
    private int diaActual;
    private Map<Point, List<Recurso>> recursosEnMapa;
    private Map<Point, Animal> animalesEnMapa;
    private Map<Point, Refugio> refugiosEnMapa;
    private Map<String, Personaje> personajes;
    private Map<Point, Set<String>> posicionesPersonajes;

    private Dios() {
        random = new Random();
        diaActual = 1;
    }

    public static Dios getInstance() {
        if (instance == null) {
            instance = new Dios();
        }
        return instance;
    }

    // Métodos para inicializar referencias a los mapas del juego
    public void setMaps(Map<Point, List<Recurso>> recursos, Map<Point, Animal> animales, 
                       Map<Point, Refugio> refugios, Map<String, Personaje> personajes) {
        this.recursosEnMapa = recursos;
        this.animalesEnMapa = animales;
        this.refugiosEnMapa = refugios;
        this.personajes = personajes;
    }

    public void setPositions(Map<Point, Set<String>> posiciones) {
        this.posicionesPersonajes = posiciones;
    }

    // Control del tiempo
    public void pasarDia() {
        diaActual++;
        desgastarRefugios();
        aplicarEventosAleatorios();
    }

    // Generación de eventos aleatorios
    private void aplicarEventosAleatorios() {
    // Ya no generamos tormentas aleatorias
    // Solo manejamos el desgaste natural de los refugios
    desgastarRefugios();
}

    // Métodos para generar eventos específicos
    public void generarAccidente(Personaje personaje) {
        boolean accidenteGrave = random.nextBoolean();
        if (accidenteGrave) {
            personaje.reducirSalud(20);
            personaje.reducirEnergia(20);
            System.out.println(personaje.getNombre() + " ha sufrido un accidente grave.");
        } else {
            personaje.reducirSalud(10);
            personaje.reducirEnergia(10);
            System.out.println(personaje.getNombre() + " ha sufrido un accidente leve.");
        }
    }

    public void generarEnfermedad(Personaje personaje) {
        boolean enfermedadGrave = random.nextBoolean();
        if (enfermedadGrave) {
            personaje.reducirSalud(20);
            System.out.println(personaje.getNombre() + " ha contraído una enfermedad grave.");
        } else {
            personaje.reducirSalud(10);
            System.out.println(personaje.getNombre() + " ha contraído una enfermedad leve.");
        }
    }

    public void generarTormenta() {
        System.out.println("¡Una tormenta ha comenzado!");
        for (Map.Entry<Point, Refugio> entry : refugiosEnMapa.entrySet()) {
            Refugio refugio = entry.getValue();
            int dano = 20 + random.nextInt(21); // 20-40 puntos de daño
            refugio.setEstabilidad(refugio.getEstabilidad() - dano);
            
            // Afectar a los personajes según la estabilidad del refugio
            for (Personaje personaje : refugio.getOcupantes()) {
                if (refugio.getEstabilidad() < 30) {
                    personaje.reducirEnergia(25);
                    personaje.reducirSalud(10);
                } else if (refugio.getEstabilidad() < 70) {
                    personaje.reducirEnergia(15);
                } else {
                    personaje.reducirEnergia(5);
                }
            }
        }
    }

    public void generarAtaqueAnimal() {
        String[] tiposAnimales = {"lobo", "oso"}; // Solo depredadores
        String tipoAnimal = tiposAnimales[random.nextInt(tiposAnimales.length)];
        Animal animal = new Animal(tipoAnimal);
        
        // Encontrar personajes vulnerables (no en refugios)
        List<Point> posicionesPosibles = new ArrayList<>();
        for (Map.Entry<Point, Set<String>> entry : posicionesPersonajes.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                posicionesPosibles.add(entry.getKey());
            }
        }
        
        if (!posicionesPosibles.isEmpty()) {
            Point puntoAtaque = posicionesPosibles.get(random.nextInt(posicionesPosibles.size()));
            animalesEnMapa.put(puntoAtaque, animal);
            
            Set<String> personajesEnPunto = posicionesPersonajes.get(puntoAtaque);
            boolean hayCazador = false;
            Cazador cazadorPresente = null;
            
            // Verificar si hay un cazador
            for (String nombrePersonaje : personajesEnPunto) {
                Personaje p = personajes.get(nombrePersonaje);
                if (p instanceof Cazador) {
                    hayCazador = true;
                    cazadorPresente = (Cazador) p;
                    break;
                }
            }
            
            System.out.println("¡Un " + tipoAnimal + " está atacando en la posición (" + 
                             puntoAtaque.x + ", " + puntoAtaque.y + ")!");
            
            if (hayCazador) {
                // Si hay cazador, intenta defender
                for (String nombrePersonaje : personajesEnPunto) {
                    if (!nombrePersonaje.equals(cazadorPresente.getNombre())) {
                        Personaje personajeADefender = personajes.get(nombrePersonaje);
                        cazadorPresente.defender(personajeADefender, animal);
                    }
                }
            } else {
                // Si no hay cazador, el animal ataca a todos
                for (String nombrePersonaje : personajesEnPunto) {
                    Personaje personaje = personajes.get(nombrePersonaje);
                    animal.atacarPersonaje(personaje);
                }
            }
        }
    }

    // Métodos para hacer aparecer recursos y animales
    public void generarRecurso(Point posicion, String tipo, int cantidad) {
        List<Recurso> recursos = recursosEnMapa.computeIfAbsent(posicion, k -> new ArrayList<>());
        recursos.add(new Recurso(tipo, cantidad));
        System.out.println("Recurso generado: " + cantidad + " " + tipo + " en (" + 
                         posicion.x + ", " + posicion.y + ")");
    }

    public void generarAnimal(Point posicion, String tipo) {
        animalesEnMapa.put(posicion, new Animal(tipo));
        System.out.println("Animal generado: " + tipo + " en (" + posicion.x + ", " + posicion.y + ")");
    }

    private void desgastarRefugios() {
        for (Refugio refugio : refugiosEnMapa.values()) {
            refugio.desgastar();
        }
    }

    public int getDiaActual() {
        return diaActual;
    }
}