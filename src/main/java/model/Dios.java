package model;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.awt.Point;
import java.util.HashSet;
import javax.swing.Timer;

public class Dios {
    private static Dios instance;
    private Random random;
    private int diaActual;
    private Map<Point, List<Recurso>> recursosEnMapa;
    private Map<Point, Animal> animalesEnMapa;
    private Map<Point, Refugio> refugiosEnMapa;
    private Map<String, Personaje> personajes;
    private Map<Point, Set<String>> posicionesPersonajes;
    private boolean hayTormenta;  
    public static final int DURACION_TORMENTA = 5000;
    private static final String[] TIPOS_ENFERMEDADES = {
        "Fiebre", "Infección", "Intoxicación", "Gripe"
    };

    private Dios() {
        random = new Random();
        diaActual = 1;
        hayTormenta = false;  
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
    
    
    private void procesarEventosDiarios() {
    // Procesar solo efectos de enfermedades
    for (Personaje personaje : personajes.values()) {
        if (personaje.isEnfermo()) {
            if (personaje.isEnfermedadGrave()) {
                personaje.reducirSalud(15);
                personaje.reducirEnergia(10);
                System.out.println(personaje.getNombre() + " sufre por su enfermedad grave");
            } else {
                personaje.reducirSalud(5);
                personaje.reducirEnergia(5);
                System.out.println(personaje.getNombre() + " sufre por su enfermedad leve");
            }
        }
    }

    // Desgaste natural de refugios
    desgastarRefugios();

    System.out.println("=== Fin del día " + diaActual + " ===");
}

    // Control del tiempo
   public void pasarDia() {
        diaActual++;
        System.out.println("\n=== Inicio del día " + diaActual + " ===");
        
        // Procesar todos los eventos del día
        procesarEventosDiarios();
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
        if (personaje == null || personaje.getNivelSalud() <= 0) return;
        
        boolean grave = random.nextBoolean();
        String tipoEnfermedad = TIPOS_ENFERMEDADES[random.nextInt(TIPOS_ENFERMEDADES.length)];
        
        personaje.contraerEnfermedad(tipoEnfermedad, grave);
        
        // Daño inicial por la enfermedad
        if (grave) {
            personaje.reducirSalud(20);
            personaje.reducirEnergia(15);
        } else {
            personaje.reducirSalud(10);
            personaje.reducirEnergia(10);
        }
    }

   public void generarTormenta() {
    if (hayTormenta) return;

    hayTormenta = true;
    System.out.println("¡Una tormenta ha comenzado!");
    
    // Procesar refugios y sus ocupantes
    if (refugiosEnMapa != null) {
        for (Map.Entry<Point, Refugio> entry : refugiosEnMapa.entrySet()) {
            Refugio refugio = entry.getValue();
            Point posicion = entry.getKey();
            
            // Dañar el refugio
            int danoBase = 20 + random.nextInt(21);
            refugio.setEstabilidad(refugio.getEstabilidad() - danoBase);
            
            System.out.println("Refugio en (" + posicion.x + "," + posicion.y + 
                             ") ha recibido " + danoBase + " puntos de daño. " +
                             "Estabilidad actual: " + refugio.getEstabilidad() + "%");
            
            // Dañar a los ocupantes según la estabilidad del refugio
            for (Personaje ocupante : refugio.getOcupantes()) {
                if (refugio.getEstabilidad() < 30) {
                    ocupante.reducirEnergia(25);
                    ocupante.reducirSalud(10);
                    System.out.println(ocupante.getNombre() + " ha sufrido daño severo en el refugio inestable");
                } else if (refugio.getEstabilidad() < 70) {
                    ocupante.reducirEnergia(15);
                    System.out.println(ocupante.getNombre() + " ha perdido energía en el refugio dañado");
                } else {
                    ocupante.reducirEnergia(5);
                    System.out.println(ocupante.getNombre() + " ha perdido un poco de energía en el refugio estable");
                }
            }
        }
    }
    
    // Procesar personajes sin refugio
    if (posicionesPersonajes != null) {
        for (Map.Entry<Point, Set<String>> entry : posicionesPersonajes.entrySet()) {
            Point posicion = entry.getKey();
            Set<String> personajesEnPosicion = entry.getValue();
            
            if (!refugiosEnMapa.containsKey(posicion)) {
                for (String nombrePersonaje : personajesEnPosicion) {
                    Personaje personaje = personajes.get(nombrePersonaje);
                    if (personaje != null && personaje.getRefugioAsignado() == null) {
                        personaje.reducirEnergia(35);
                        personaje.reducirSalud(25);
                        System.out.println(nombrePersonaje + " sufre severamente por estar expuesto a la tormenta");
                    }
                }
            }
        }
    }
    
    Timer tormentaTimer = new Timer(DURACION_TORMENTA, e -> finalizarTormenta());
    tormentaTimer.setRepeats(false);
    tormentaTimer.start();
}
    
     public void finalizarTormenta() {
        hayTormenta = false;
        System.out.println("La tormenta ha terminado");
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
    
    private void aplicarEfectosTormenta() {
    // Lista para mantener registro de personajes ya procesados
    Set<String> personajesProcesados = new HashSet<>();
    
    // Primero procesar refugios y sus ocupantes
    for (Map.Entry<Point, Refugio> entry : refugiosEnMapa.entrySet()) {
        Refugio refugio = entry.getValue();
        
        // Daño al refugio
        int danoRefugio = 20 + random.nextInt(21); // 20-40 puntos de daño
        refugio.setEstabilidad(refugio.getEstabilidad() - danoRefugio);
        
        // Daño a ocupantes del refugio según su estabilidad
        for (Personaje ocupante : refugio.getOcupantes()) {
            if (refugio.getEstabilidad() < 30) {
                ocupante.reducirEnergia(25);
                ocupante.reducirSalud(10);
            } else if (refugio.getEstabilidad() < 70) {
                ocupante.reducirEnergia(15);
            } else {
                ocupante.reducirEnergia(5);
            }
            // Marcar este personaje como procesado
            personajesProcesados.add(ocupante.getNombre());
        }
    }
    
    // Luego procesar todos los personajes que NO están en refugios
    for (Map.Entry<String, Personaje> entry : personajes.entrySet()) {
        String nombre = entry.getKey();
        Personaje personaje = entry.getValue();
        
        // Si el personaje no ha sido procesado (no está en refugio)
        if (!personajesProcesados.contains(nombre)) {
            personaje.reducirEnergia(35);  // Daño severo de energía
            personaje.reducirSalud(25);    // Daño severo de salud
            System.out.println(nombre + " sufre severamente por la tormenta al estar sin refugio");
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
     public boolean isHayTormenta() {
        return hayTormenta;
    }
}