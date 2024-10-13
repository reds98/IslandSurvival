package com.mycompany.islandsurvival;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.*;
import java.util.*;
import java.util.List;

public class IslandSurvival extends JFrame {
    private JPanel mapPanel;
    private JPanel controlPanel;
    private JPanel characterControlPanel;
    private JButton diosButton;
    private JButton recursosButton;
    
    private static final int MAP_SIZE = 10;
    private JPanel[][] mapCells;
    private boolean[][] descubierto;
    
    private Map<String, Personaje> personajes;
    private Map<Point, Set<String>> posicionesPersonajes;
    private Map<Point, List<Recurso>> recursosEnMapa;
    private RecursosManager recursosManager;
    
    private Point puntoInicial;
    private Map<Point, Animal> animalesEnMapa;
    private Map<String, ImageIcon> imagenes;
    private Map<Point, Refugio> refugiosEnMapa;


    public IslandSurvival() {
        setTitle("Island Survival");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        recursosManager = RecursosManager.getInstance();
        animalesEnMapa = new HashMap<>();
        refugiosEnMapa = new HashMap<>();

        puntoInicial = new Point(MAP_SIZE / 2, MAP_SIZE / 2); // Punto central del mapa
        cargarImagenes();
        initComponents();
        initializeCharacters();
        
        setVisible(true);
    }

     private void cargarImagenes() {
        imagenes = new HashMap<>();
        String[] tipos = {"cazador", "explorador", "recolector", "constructor", "curandero", "cientifico",
                          "madera", "piedra", "fruta", "carne", "planta_medicinal", "agua",
                          "conejo", "ciervo", "lobo", "oso", "cabin"};
        for (String tipo : tipos) {
            String ruta = "/images/" + tipo + ".png";
            ImageIcon imagen = new ImageIcon(getClass().getResource(ruta));
            if (tipo.equals("cabin")) {
                imagen = new ImageIcon(imagen.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH));
            } else {
                imagen = new ImageIcon(imagen.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
            }
            imagenes.put(tipo, imagen);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        mapPanel = new JPanel(new GridLayout(MAP_SIZE, MAP_SIZE));
        mapCells = new JPanel[MAP_SIZE][MAP_SIZE];
        descubierto = new boolean[MAP_SIZE][MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                mapCells[i][j] = new JPanel(new GridLayout(2, 2));
                mapCells[i][j].setBackground(Color.BLACK);
                mapCells[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                mapCells[i][j].setPreferredSize(new Dimension(64, 64));
                mapPanel.add(mapCells[i][j]);
            }
        }
        descubierto[puntoInicial.y][puntoInicial.x] = true;
        mapCells[puntoInicial.y][puntoInicial.x].setBackground(Color.GREEN);
        
        add(mapPanel, BorderLayout.CENTER);
        
        controlPanel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        diosButton = new JButton("DIOS");
        diosButton.addActionListener(e -> handleDiosAction());
        recursosButton = new JButton("RECURSOS");
        recursosButton.addActionListener(e -> handleRecursosAction());
        buttonPanel.add(diosButton);
        buttonPanel.add(recursosButton);
         // Añadir un botón para ver refugios
        JButton verRefugiosButton = new JButton("Ver Refugios");
        verRefugiosButton.addActionListener(e -> mostrarRefugios());
        buttonPanel.add(verRefugiosButton);
        
        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        
        characterControlPanel = new JPanel();
        characterControlPanel.setPreferredSize(new Dimension(200, 600));
        
        controlPanel.add(characterControlPanel, BorderLayout.CENTER);
        
        add(controlPanel, BorderLayout.EAST);
        
        recursosEnMapa = new HashMap<>();
    }

    private void initializeCharacters() {
        personajes = new HashMap<>();
        posicionesPersonajes = new HashMap<>();
    }

   private void updateMapCell(int x, int y) {
    if (!descubierto[y][x]) return;
    
    Point p = new Point(x, y);
    Set<String> charactersAtPoint = posicionesPersonajes.getOrDefault(p, new HashSet<>());
    List<Recurso> recursosEnPunto = recursosEnMapa.getOrDefault(p, new ArrayList<>());
    Animal animalEnPunto = animalesEnMapa.get(p);
    Refugio refugioEnPunto = refugiosEnMapa.get(p);
    
    mapCells[y][x].removeAll();
    mapCells[y][x].setBackground(Color.GREEN);

    // Mostrar personajes
    for (String name : charactersAtPoint) {
        Personaje personaje = personajes.get(name);
        if (personaje != null) {
            JLabel label = new JLabel(imagenes.get(personaje.getClass().getSimpleName().toLowerCase()));
            mapCells[y][x].add(label);
        }
    }

    // Mostrar recursos
    for (Recurso recurso : recursosEnPunto) {
        JLabel label = new JLabel(imagenes.get(recurso.getTipo()));
        mapCells[y][x].add(label);
    }

    // Mostrar animal
    if (animalEnPunto != null) {
        JLabel label = new JLabel(imagenes.get(animalEnPunto.getTipoAnimal()));
        mapCells[y][x].add(label);
    }

    // Mostrar refugio
    if (refugioEnPunto != null) {
        JLabel refugioLabel = new JLabel(imagenes.get("cabin"));
        mapCells[y][x].add(refugioLabel);
    }

    mapCells[y][x].revalidate();
    mapCells[y][x].repaint();
}

    private void handleDiosAction() {
        JDialog diosDialog = new JDialog(this, "Controles de Dios", true);
        diosDialog.setLayout(new GridLayout(7, 1));
        
        String[] characterTypes = {"Cazador", "Explorador", "Recolector", "Constructor", "Curandero", "Científico"};
        
        for (String type : characterTypes) {
            JButton addButton = new JButton("Añadir " + type);
            addButton.addActionListener(e -> addCharacter(type));
            diosDialog.add(addButton);
        }
        
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> diosDialog.dispose());
        diosDialog.add(closeButton);
        
        diosDialog.pack();
        diosDialog.setVisible(true);
    }

    private void addCharacter(String type) {
        String name = JOptionPane.showInputDialog(this, "Introduce un nombre para el " + type + ":");
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        
        Personaje newCharacter;
        switch (type) {
            case "Cazador":
                newCharacter = new Cazador(name);
                break;
            case "Explorador":
                newCharacter = new Explorador(name);
                break;
            case "Recolector":
                newCharacter = new Recolector(name);
                break;
            case "Constructor":
                newCharacter = new Constructor(name);
                break;
            case "Curandero":
                newCharacter = new Curandero(name);
                break;
            case "Científico":
                newCharacter = new Científico(name);
                break;
            default:
                return;
        }
        
        personajes.put(name, newCharacter);
        posicionesPersonajes.computeIfAbsent(puntoInicial, k -> new HashSet<>()).add(name);
        updateMapCell(puntoInicial.x, puntoInicial.y);
        updateCharacterControlPanel();
    }

    private void updateCharacterControlPanel() {
        characterControlPanel.removeAll();
        characterControlPanel.setLayout(new GridLayout(personajes.size(), 1));
        
        for (Map.Entry<String, Personaje> entry : personajes.entrySet()) {
            String name = entry.getKey();
            Personaje character = entry.getValue();
            
            JButton characterButton = new JButton(character.getClass().getSimpleName() + ": " + name);
            characterButton.addActionListener(e -> showCharacterControls(name));
            characterControlPanel.add(characterButton);
        }
        
        characterControlPanel.revalidate();
        characterControlPanel.repaint();
    }

    private void showCharacterControls(String name) {
        JDialog controlDialog = new JDialog(this, "Controles de " + name, true);
        Personaje personaje = personajes.get(name);
        
        int numButtons = 8;
        if (personaje instanceof Cazador || personaje instanceof Recolector) {
            numButtons = 10;
        }
        
        controlDialog.setLayout(new GridLayout(numButtons, 1));
        
        JButton moveUpButton = new JButton("Mover Arriba");
        moveUpButton.addActionListener(e -> moveCharacter(name, 0, -1));
        controlDialog.add(moveUpButton);
        
        JButton moveDownButton = new JButton("Mover Abajo");
        moveDownButton.addActionListener(e -> moveCharacter(name, 0, 1));
        controlDialog.add(moveDownButton);
        
        JButton moveLeftButton = new JButton("Mover Izquierda");
        moveLeftButton.addActionListener(e -> moveCharacter(name, -1, 0));
        controlDialog.add(moveLeftButton);
        
        JButton moveRightButton = new JButton("Mover Derecha");
        moveRightButton.addActionListener(e -> moveCharacter(name, 1, 0));
        controlDialog.add(moveRightButton);
        
        JButton exploreButton = new JButton("Explorar");
        exploreButton.addActionListener(e -> performExploration(name));
        controlDialog.add(exploreButton);
        
        JButton collectButton = new JButton("Recolectar");
        collectButton.addActionListener(e -> collectResources(name));
        controlDialog.add(collectButton);
        
        JButton eatButton = new JButton("Comer");
        eatButton.addActionListener(e -> eat(name));
        controlDialog.add(eatButton);
        
        JButton entrarRefugioButton = new JButton("Entrar al Refugio");
        entrarRefugioButton.addActionListener(e -> entrarRefugio(name));
        controlDialog.add(entrarRefugioButton);

        JButton salirRefugioButton = new JButton("Salir del Refugio");
        salirRefugioButton.addActionListener(e -> salirRefugio(name));
        controlDialog.add(salirRefugioButton);
        
        if (personaje instanceof Cazador) {
            JButton cazarButton = new JButton("Cazar");
            cazarButton.addActionListener(e -> cazar(name));
            controlDialog.add(cazarButton);
            
            JButton defenderButton = new JButton("Defender");
            defenderButton.addActionListener(e -> defender(name));
            controlDialog.add(defenderButton);
        } else if (personaje instanceof Recolector) {
            JButton recolectarButton = new JButton("Recolectar");
            recolectarButton.addActionListener(e -> recolectar(name));
            controlDialog.add(recolectarButton);
            
            JButton entregarButton = new JButton("Entregar Recurso");
            entregarButton.addActionListener(e -> entregarRecurso(name));
            controlDialog.add(entregarButton);
        }
        else if (personaje instanceof Constructor) {
            JButton construirRefugioButton = new JButton("Construir Refugio");
            construirRefugioButton.addActionListener(e -> construirRefugio(name));
            controlDialog.add(construirRefugioButton);

            JButton repararRefugioButton = new JButton("Reparar Refugio");
            repararRefugioButton.addActionListener(e -> repararRefugio(name));
            controlDialog.add(repararRefugioButton);
        }
        
        JButton statsButton = new JButton("Ver Estadísticas");
        statsButton.addActionListener(e -> showStats(name));
        controlDialog.add(statsButton);
        
        controlDialog.pack();
        controlDialog.setVisible(true);
    }

    private void moveCharacter(String name, int dx, int dy) {
        Point currentPos = null;
        for (Map.Entry<Point, Set<String>> entry : posicionesPersonajes.entrySet()) {
            if (entry.getValue().contains(name)) {
                currentPos = entry.getKey();
                break;
            }
        }
        
        if (currentPos == null) return;
        
        int newX = Math.max(0, Math.min(MAP_SIZE - 1, currentPos.x + dx));
        int newY = Math.max(0, Math.min(MAP_SIZE - 1, currentPos.y + dy));
        Point newPos = new Point(newX, newY);
        
        posicionesPersonajes.get(currentPos).remove(name);
        if (posicionesPersonajes.get(currentPos).isEmpty()) {
            posicionesPersonajes.remove(currentPos);
        }
        updateMapCell(currentPos.x, currentPos.y);
        
        posicionesPersonajes.computeIfAbsent(newPos, k -> new HashSet<>()).add(name);
        
        Personaje personaje = personajes.get(name);
        if (personaje instanceof Explorador) {
            descubrirArea(newX, newY);
        }
        
        updateMapCell(newX, newY);
        mapPanel.revalidate();
        mapPanel.repaint();
    }
    
    
    private void entrarRefugio(String name) {
        Personaje personaje = personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position != null) {
            Refugio refugio = refugiosEnMapa.get(position);
            if (refugio != null) {
                if (refugio.añadirPersonaje(personaje)) {
                    posicionesPersonajes.get(position).remove(name);
                    updateMapCell(position.x, position.y);
                    JOptionPane.showMessageDialog(this, name + " ha entrado al refugio.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay un refugio en esta posición.");
            }
        }
    }

    private void salirRefugio(String name) {
        Personaje personaje = personajes.get(name);
        if (personaje.getRefugioAsignado() != null) {
            for (Map.Entry<Point, Refugio> entry : refugiosEnMapa.entrySet()) {
                if (entry.getValue() == personaje.getRefugioAsignado()) {
                    Point position = entry.getKey();
                    if (personaje.getRefugioAsignado().eliminarPersonaje(personaje)) {
                        posicionesPersonajes.computeIfAbsent(position, k -> new HashSet<>()).add(name);
                        updateMapCell(position.x, position.y);
                        JOptionPane.showMessageDialog(this, name + " ha salido del refugio.");
                    }
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, name + " no está en un refugio.");
        }
    }

    private void descubrirArea(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < MAP_SIZE && newY >= 0 && newY < MAP_SIZE) {
                    if (!descubierto[newY][newX]) {
                        descubierto[newY][newX] = true;
                        mapCells[newY][newX].setBackground(Color.GREEN);
                    }
                }
            }
        }
    }

    private void performExploration(String name) {
        Personaje personaje = personajes.get(name);
        if (personaje instanceof Explorador) {
            Explorador explorador = (Explorador) personaje;
            Point position = getCharacterPosition(name);
            if (position != null) {
                explorador.explorar();
                descubrirArea(position.x, position.y);
                List<Recurso> recursosEncontrados = explorador.descubrirRecursos();
                recursosEnMapa.computeIfAbsent(position, k -> new ArrayList<>()).addAll(recursosEncontrados);
                
                if (Math.random() < 0.3) {
                    Animal animalDescubierto = explorador.descubrirAnimal();
                    animalesEnMapa.put(position, animalDescubierto);
                    JOptionPane.showMessageDialog(this, name + " ha descubierto un " + animalDescubierto.getTipoAnimal() + " en su exploración.");
                }
                
                updateMapCell(position.x, position.y);
                JOptionPane.showMessageDialog(this, name + " ha explorado el área y encontrado recursos y/o animales.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Solo los exploradores pueden realizar esta acción.");
        }
    }

    private void collectResources(String name) {
        Personaje personaje = personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position != null) {
            List<Recurso> recursosEnPunto = recursosEnMapa.get(position);
            if (recursosEnPunto != null && !recursosEnPunto.isEmpty()) {
                Recurso recursoARecolectar = recursosEnPunto.remove(0);
                recursosManager.agregarRecurso(recursoARecolectar.getTipo(), recursoARecolectar.getCantidad());
                updateMapCell(position.x, position.y);
                JOptionPane.showMessageDialog(this, name + " ha recolectado " + recursoARecolectar.getCantidad() + " " + recursoARecolectar.getTipo() + "(s) y se ha añadido a los recursos globales.");
            } else {
                JOptionPane.showMessageDialog(this, "No hay recursos para recolectar en esta posición.");
            }
        }
    }

    private void eat(String name) {
        Personaje personaje = personajes.get(name);
        String[] options = {"Fruta", "Carne"};
        int choice = JOptionPane.showOptionDialog(this, "¿Qué tipo de comida quieres comer?", "Comer",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice != JOptionPane.CLOSED_OPTION) {
            String tipoComida = options[choice].toLowerCase();
            personaje.comer(tipoComida);
            JOptionPane.showMessageDialog(this, name + " ha comido " + tipoComida + ".");
        }
    }

    private void cazar(String name) {
        Cazador cazador = (Cazador) personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position != null) {
            if (animalesEnMapa.containsKey(position)) {
                boolean exito = cazador.cazar();
                if (exito) {
                    animalesEnMapa.remove(position);
                    updateMapCell(position.x, position.y);
                    JOptionPane.showMessageDialog(this, name + " ha cazado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, name + " no ha tenido éxito en la caza.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay animales para cazar en esta posición.");
            }
        }
    }

    private void defender(String name) {
        Cazador cazador = (Cazador) personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position != null) {
            if (animalesEnMapa.containsKey(position)) {
                Set<String> personajesEnPosicion = posicionesPersonajes.get(position);
                if (personajesEnPosicion.size() > 1) {
                    String[] personajesArray = personajesEnPosicion.toArray(new String[0]);
                    String personajeADefender = (String) JOptionPane.showInputDialog(
                        this,
                        "Elige el personaje a defender:",
                        "Defender",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        personajesArray,
                        personajesArray[0]
                    );
                    if (personajeADefender != null && !personajeADefender.equals(name)) {
                        Personaje personajeDefendido = personajes.get(personajeADefender);
                        Animal animal = animalesEnMapa.get(position);
                        cazador.defender(personajeDefendido, animal);
                        animalesEnMapa.remove(position);
                        updateMapCell(position.x, position.y);
                        JOptionPane.showMessageDialog(this, name + " ha defendido a " + personajeADefender + " de un " + animal.getTipoAnimal());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No hay otros personajes para defender en esta posición.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay animales peligrosos en esta posición.");
            }
        }
    }

    private void recolectar(String name) {
        Recolector recolector = (Recolector) personajes.get(name);
        recolector.recolectar();
        Point position = getCharacterPosition(name);
        if (position != null) {
            updateMapCell(position.x, position.y);
        }
        JOptionPane.showMessageDialog(this, name + " ha realizado una acción de recolección.");
    }

    private void entregarRecurso(String name) {
        Recolector recolector = (Recolector) personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position != null) {
            Set<String> personajesEnPosicion = posicionesPersonajes.get(position);
            if (personajesEnPosicion.size() > 1) {
                String[] personajesArray = personajesEnPosicion.toArray(new String[0]);
                String personajeReceptor = (String) JOptionPane.showInputDialog(
                    this,
                    "Elige el personaje al que entregar el recurso:",
                    "Entregar Recurso",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    personajesArray,
                    personajesArray[0]
                );
                if (personajeReceptor != null && !personajeReceptor.equals(name)) {
                    Personaje receptor = personajes.get(personajeReceptor);
                    String[] tiposRecursos = {"fruta", "agua", "madera", "planta medicinal"};
                    String tipoRecurso = (String) JOptionPane.showInputDialog(
                        this,
                        "Elige el tipo de recurso a entregar:",
                        "Entregar Recurso",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tiposRecursos,
                        tiposRecursos[0]
                    );
                    if (tipoRecurso != null) {
                        String cantidadStr = JOptionPane.showInputDialog(this, "Introduce la cantidad a entregar:");
                        try {
                            int cantidad = Integer.parseInt(cantidadStr);
                            recolector.entregarRecurso(receptor, tipoRecurso, cantidad);
                            JOptionPane.showMessageDialog(this, name + " ha entregado " + cantidad + " " + tipoRecurso + " a " + personajeReceptor);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this, "Cantidad inválida.");
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay otros personajes en esta posición para entregar recursos.");
            }
        }
    }
    
    
    private void construirRefugio(String name) {
    Constructor constructor = (Constructor) personajes.get(name);
    Point position = getCharacterPosition(name);
    if (position != null) {
        if (!refugiosEnMapa.containsKey(position)) {
            constructor.construirRefugio();
            if (constructor.getRefugioAsignado() != null) {
                refugiosEnMapa.put(position, constructor.getRefugioAsignado());
                // No eliminamos al constructor de posicionesPersonajes
                updateMapCell(position.x, position.y);
                JOptionPane.showMessageDialog(this, name + " ha construido un nuevo refugio.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ya existe un refugio en esta posición.");
        }
    }
}

    private void repararRefugio(String name) {
        Constructor constructor = (Constructor) personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position != null) {
            Refugio refugio = refugiosEnMapa.get(position);
            if (refugio != null) {
                constructor.setRefugioAsignado(refugio);
                constructor.repararRefugio();
                JOptionPane.showMessageDialog(this, "Refugio reparado. Nueva estabilidad: " + refugio.getEstabilidad());
            } else {
                JOptionPane.showMessageDialog(this, "No hay un refugio en esta posición para reparar.");
            }
        }
    }
    
    // Método para desgastar los refugios (llamar periódicamente)
    private void desgastarRefugios() {
        for (Refugio refugio : refugiosEnMapa.values()) {
            refugio.desgastar();
        }
    }

    private void mostrarRefugios() {
        StringBuilder infoRefugios = new StringBuilder("Refugios en el mapa:\n\n");
        for (Map.Entry<Point, Refugio> entry : refugiosEnMapa.entrySet()) {
            Point p = entry.getKey();
            Refugio r = entry.getValue();
            infoRefugios.append("Posición (").append(p.x).append(", ").append(p.y).append("): ")
                        .append(r.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(this, infoRefugios.toString());
    }

    private void showStats(String name) {
        Personaje personaje = personajes.get(name);
        JOptionPane.showMessageDialog(this, personaje.toString());
    }

    private Point getCharacterPosition(String name) {
        for (Map.Entry<Point, Set<String>> entry : posicionesPersonajes.entrySet()) {
            if (entry.getValue().contains(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void handleRecursosAction() {
        StringBuilder resumen = new StringBuilder(recursosManager.getResumenRecursos());
        
        resumen.append("\nRecursos en el mapa:\n");
        for (Map.Entry<Point, List<Recurso>> entry : recursosEnMapa.entrySet()) {
            Point p = entry.getKey();
            List<Recurso> recursos = entry.getValue();
            resumen.append("Posición (").append(p.x).append(", ").append(p.y).append("): ");
            for (Recurso r : recursos) {
                resumen.append(r.getTipo()).append(" x").append(r.getCantidad()).append(", ");
            }
            resumen.append("\n");
        }
        
        resumen.append("\nAnimales en el mapa:\n");
        for (Map.Entry<Point, Animal> entry : animalesEnMapa.entrySet()) {
            Point p = entry.getKey();
            Animal animal = entry.getValue();
            resumen.append("Posición (").append(p.x).append(", ").append(p.y).append("): ")
                   .append(animal.getTipoAnimal()).append("\n");
        }

        JOptionPane.showMessageDialog(this, resumen.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IslandSurvival());
    }
}