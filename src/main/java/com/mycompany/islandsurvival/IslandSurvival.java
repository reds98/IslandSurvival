package com.mycompany.islandsurvival;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer; // Añade esta importación específica
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerNumberModel;

public class IslandSurvival extends JFrame {
    private JPanel mapPanel;
    private JPanel controlPanel;
    private JPanel characterControlPanel;
    private JPanel statusPanel;
    private JButton diosButton;
    private JButton recursosButton;
    private JLabel diaLabel;
    private JLabel climaLabel;
    
    private static final int MAP_SIZE = 10;
    private JPanel[][] mapCells;
    private boolean[][] descubierto;
    
    private Map<String, Personaje> personajes;
    private Map<Point, Set<String>> posicionesPersonajes;
    private Map<Point, List<Recurso>> recursosEnMapa;
    private RecursosManager recursosManager;
    private Map<String, JTextArea> bitacoras;
    
    private Point puntoInicial;
    private Map<Point, Animal> animalesEnMapa;
    private Map<Point, Refugio> refugiosEnMapa;
    private Map<String, ImageIcon> imagenes;
    
    private Dios dios;
    private Timer eventTimer;
    private boolean hayTormenta = false;
    private Random random = new Random();

    public IslandSurvival() {
        setTitle("Island Survival");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        dios = Dios.getInstance();
        bitacoras = new HashMap<>();
        recursosManager = RecursosManager.getInstance();
        animalesEnMapa = new HashMap<>();
        refugiosEnMapa = new HashMap<>();
        puntoInicial = new Point(MAP_SIZE / 2, MAP_SIZE / 2);
        
        cargarImagenes();
        initComponents();
        initializeCharacters();
        initializeStatusPanel();
        initializeEventTimer();
        
        // Inicializar los mapas en el Dios
        dios.setMaps(recursosEnMapa, animalesEnMapa, refugiosEnMapa, personajes);
        
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
        
        // Inicializar el mapa
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
        
        // Panel de control
        controlPanel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        diosButton = new JButton("DIOS");
        diosButton.addActionListener(e -> handleDiosAction());
        recursosButton = new JButton("RECURSOS");
        recursosButton.addActionListener(e -> handleRecursosAction());
        JButton verRefugiosButton = new JButton("Ver Refugios");
        verRefugiosButton.addActionListener(e -> mostrarRefugios());
        
        buttonPanel.add(diosButton);
        buttonPanel.add(recursosButton);
        buttonPanel.add(verRefugiosButton);
        
        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        
        characterControlPanel = new JPanel();
        characterControlPanel.setPreferredSize(new Dimension(200, 600));
        controlPanel.add(characterControlPanel, BorderLayout.CENTER);
        
        add(controlPanel, BorderLayout.EAST);
        
        recursosEnMapa = new HashMap<>();
    }

    private void initializeStatusPanel() {
        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        
        diaLabel = new JLabel("Día: 1");
        diaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        climaLabel = new JLabel("Clima: Normal");
        climaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        statusPanel.add(diaLabel);
        statusPanel.add(climaLabel);
        
        add(statusPanel, BorderLayout.NORTH);
    }

    private void initializeEventTimer() {
    eventTimer = new Timer(10000, e -> {
        // No hacemos nada aquí, las tormentas solo las controla el Dios
    });
    eventTimer.start();
}

    private void initializeCharacters() {
        personajes = new HashMap<>();
        posicionesPersonajes = new HashMap<>();
    }

    private void handleDiosAction() {
        JDialog diosDialog = new JDialog(this, "Controles de Dios", true);
        diosDialog.setLayout(new GridLayout(0, 1, 5, 5));
        
        String[] characterTypes = {"Cazador", "Explorador", "Recolector", "Constructor", "Curandero", "Científico"};
        for (String type : characterTypes) {
            JButton addButton = new JButton("Añadir " + type);
            addButton.addActionListener(e -> addCharacter(type));
            diosDialog.add(addButton);
        }
        
        JButton pasarDiaButton = new JButton("Pasar al siguiente día");
        pasarDiaButton.addActionListener(e -> pasarDia());
        diosDialog.add(pasarDiaButton);
        
        JButton tormentaButton = new JButton("Generar Tormenta");
        tormentaButton.addActionListener(e -> iniciarTormenta());
        diosDialog.add(tormentaButton);
        
        JButton accidenteButton = new JButton("Generar Accidente");
        accidenteButton.addActionListener(e -> generarAccidente());
        diosDialog.add(accidenteButton);
        
        JButton enfermedadButton = new JButton("Generar Enfermedad");
        enfermedadButton.addActionListener(e -> generarEnfermedad());
        diosDialog.add(enfermedadButton);
        
        JButton ataqueAnimalButton = new JButton("Generar Ataque Animal");
        ataqueAnimalButton.addActionListener(e -> generarAtaqueAnimal());
        diosDialog.add(ataqueAnimalButton);
        
        JButton generarRecursoButton = new JButton("Generar Recurso");
        generarRecursoButton.addActionListener(e -> generarRecursoAleatorio());
        diosDialog.add(generarRecursoButton);
        
        JButton verBitacorasButton = new JButton("Ver Bitácoras");
        verBitacorasButton.addActionListener(e -> mostrarBitacoras());
        diosDialog.add(verBitacorasButton);

        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> diosDialog.dispose());
        diosDialog.add(closeButton);
        
        diosDialog.pack();
        diosDialog.setLocationRelativeTo(this);
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
        registrarEnBitacora(name, "Se ha unido a la isla");
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

    // En la clase IslandSurvival, modificar el método showCharacterControls:
private void showCharacterControls(String name) {
    JDialog controlDialog = new JDialog(this, "Controles de " + name, true);
    Personaje personaje = personajes.get(name);

    // Calcular número de botones base y específicos
    int numButtons = 11; // Botones básicos
    if (personaje instanceof Cazador) {
        numButtons = 14; // Básicos + Cazar + Defender
    } else if (personaje instanceof Recolector) {
        numButtons = 13; // Básicos + Recolectar + Entregar
    } else if (personaje instanceof Explorador) {
        numButtons = 13; // Básicos + Explorar + Recolectar
    }

    controlDialog.setLayout(new GridLayout(numButtons, 1));

    // Botones de movimiento (para todos)
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

    // Botón de inventario (para todos)
    JButton inventarioButton = new JButton("Ver Inventario");
    inventarioButton.addActionListener(e -> mostrarInventarioPersonal(name));
    controlDialog.add(inventarioButton);

    // Botones específicos por tipo de personaje
    if (personaje instanceof Explorador) {
        JButton exploreButton = new JButton("Explorar");
        exploreButton.addActionListener(e -> performExploration(name));
        controlDialog.add(exploreButton);

        JButton collectButton = new JButton("Recolectar");
        collectButton.addActionListener(e -> collectResources(name));
        controlDialog.add(collectButton);
    } 
    else if (personaje instanceof Cazador) {
        JButton cazarButton = new JButton("Cazar");
        cazarButton.addActionListener(e -> cazar(name));
        controlDialog.add(cazarButton);

        JButton defenderButton = new JButton("Defender");
        defenderButton.addActionListener(e -> defenderDeDepredador(name));
        controlDialog.add(defenderButton);
    } 
    else if (personaje instanceof Recolector) {
        JButton recolectarButton = new JButton("Recolectar");
        recolectarButton.addActionListener(e -> recolectar(name));
        controlDialog.add(recolectarButton);

        JButton entregarButton = new JButton("Entregar Recurso");
        entregarButton.addActionListener(e -> mostrarDialogoEntrega(name));
        controlDialog.add(entregarButton);
    }

    // Botones comunes para todos
    JButton eatButton = new JButton("Comer");
    eatButton.addActionListener(e -> eat(name));
    controlDialog.add(eatButton);

    JButton entrarRefugioButton = new JButton("Entrar al Refugio");
    entrarRefugioButton.addActionListener(e -> entrarRefugio(name));
    controlDialog.add(entrarRefugioButton);

    JButton salirRefugioButton = new JButton("Salir del Refugio");
    salirRefugioButton.addActionListener(e -> salirRefugio(name));
    controlDialog.add(salirRefugioButton);

    JButton statsButton = new JButton("Ver Estadísticas");
    statsButton.addActionListener(e -> showStats(name));
    controlDialog.add(statsButton);

    controlDialog.pack();
    controlDialog.setLocationRelativeTo(this);
    controlDialog.setVisible(true);
}

// Añadir el nuevo método para defender de depredadores
private void defenderDeDepredador(String name) {
    Cazador cazador = (Cazador) personajes.get(name);
    Point position = getCharacterPosition(name);
    
    if (position == null) return;
    
    Animal depredador = animalesEnMapa.get(position);
    if (depredador == null || !depredador.esDepredador()) {
        JOptionPane.showMessageDialog(this, "No hay depredadores en esta posición para defender.");
        return;
    }

    Set<String> personajesEnPosicion = posicionesPersonajes.get(position);
    if (personajesEnPosicion == null || personajesEnPosicion.size() <= 1) {
        JOptionPane.showMessageDialog(this, "No hay otros personajes para defender en esta posición.");
        return;
    }

    // Mostrar diálogo para seleccionar a quién defender
    List<String> personajesADefender = new ArrayList<>();
    for (String nombrePersonaje : personajesEnPosicion) {
        if (!nombrePersonaje.equals(name)) {
            personajesADefender.add(nombrePersonaje);
        }
    }

    String[] opciones = personajesADefender.toArray(new String[0]);
    String seleccion = (String) JOptionPane.showInputDialog(
        this,
        "¿A quién quieres defender?",
        "Defender de " + depredador.getTipoAnimal(),
        JOptionPane.QUESTION_MESSAGE,
        null,
        opciones,
        opciones[0]
    );

    if (seleccion != null) {
        Personaje personajeADefender = personajes.get(seleccion);
        boolean exitoDefensa = cazador.defender(personajeADefender, depredador);
        
        if (exitoDefensa) {
            // Si la defensa fue exitosa, eliminar el depredador y dar carne al cazador
            animalesEnMapa.remove(position);
            Recurso carne = new Recurso("carne", depredador.getComidaProporcionada());
            cazador.recibirRecurso(carne);
            registrarEnBitacora(name, "Defendió exitosamente a " + seleccion + 
                " del " + depredador.getTipoAnimal() + " y obtuvo " + 
                depredador.getComidaProporcionada() + " unidades de carne");
        } else {
            registrarEnBitacora(name, "Intentó defender a " + seleccion + 
                " del " + depredador.getTipoAnimal() + " pero falló");
        }
        
        updateMapCell(position.x, position.y);
        actualizarEstadoJuego();
    }
}
    
    private void mostrarInventarioPersonal(String name) {
        Personaje personaje = personajes.get(name);
        JDialog inventarioDialog = new JDialog(this, "Inventario de " + name, true);
        inventarioDialog.setLayout(new BorderLayout());

        // Crear tabla para mostrar el inventario
        String[] columnNames = {"Tipo de Recurso", "Cantidad"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Recurso recurso : personaje.getInventario()) {
            model.addRow(new Object[]{recurso.getTipo(), recurso.getCantidad()});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Añadir botones de acción si es necesario
        JPanel buttonPanel = new JPanel();
        if (personaje instanceof Recolector) {
            JButton entregarButton = new JButton("Entregar Recurso");
            entregarButton.addActionListener(e -> mostrarDialogoEntrega(name));
            buttonPanel.add(entregarButton);
        }

        JButton cerrarButton = new JButton("Cerrar");
        cerrarButton.addActionListener(e -> inventarioDialog.dispose());
        buttonPanel.add(cerrarButton);

        inventarioDialog.add(scrollPane, BorderLayout.CENTER);
        inventarioDialog.add(buttonPanel, BorderLayout.SOUTH);

        inventarioDialog.setSize(400, 300);
        inventarioDialog.setLocationRelativeTo(this);
        inventarioDialog.setVisible(true);
    }

    private void mostrarDialogoEntrega(String name) {
        Recolector recolector = (Recolector) personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position == null) {
            JOptionPane.showMessageDialog(this, "Error al obtener la posición del personaje");
            return;
        }

        Set<String> personajesEnPosicion = posicionesPersonajes.get(position);
        if (personajesEnPosicion == null || personajesEnPosicion.size() <= 1) {
            JOptionPane.showMessageDialog(this, "No hay otros personajes cerca para entregar recursos.");
            return;
        }

        // Crear diálogo de entrega
        JDialog entregaDialog = new JDialog(this, "Entregar Recurso", true);
        entregaDialog.setLayout(new GridLayout(0, 1, 5, 5));

        // Selector de personaje receptor
        JComboBox<String> receptorCombo = new JComboBox<>();
        for (String nombrePersonaje : personajesEnPosicion) {
            if (!nombrePersonaje.equals(name)) {
                receptorCombo.addItem(nombrePersonaje);
            }
        }

        // Selector de recurso
        JComboBox<String> recursoCombo = new JComboBox<>();
        for (Recurso recurso : recolector.getInventario()) {
            recursoCombo.addItem(recurso.getTipo() + " (" + recurso.getCantidad() + ")");
        }

        // Spinner para la cantidad
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 99, 1);
        JSpinner cantidadSpinner = new JSpinner(spinnerModel);

        entregaDialog.add(new JLabel("Seleccionar receptor:"));
        entregaDialog.add(receptorCombo);
        entregaDialog.add(new JLabel("Seleccionar recurso:"));
        entregaDialog.add(recursoCombo);
        entregaDialog.add(new JLabel("Cantidad:"));
        entregaDialog.add(cantidadSpinner);

        JButton entregarButton = new JButton("Entregar");
        entregarButton.addActionListener(e -> {
            String receptor = (String) receptorCombo.getSelectedItem();
            String recursoSeleccionado = (String) recursoCombo.getSelectedItem();
            if (recursoSeleccionado != null) {
                String tipoRecurso = recursoSeleccionado.split(" ")[0];
                int cantidad = (int) cantidadSpinner.getValue();

                recolector.entregarRecurso(personajes.get(receptor), tipoRecurso, cantidad);
                entregaDialog.dispose();
                actualizarEstadoJuego();
            }
        });

        entregaDialog.add(entregarButton);

        entregaDialog.pack();
        entregaDialog.setLocationRelativeTo(this);
        entregaDialog.setVisible(true);
    }

    private void updateMapCell(int x, int y) {
        if (!descubierto[y][x]) return;
        
        Point p = new Point(x, y);
        Set<String> charactersAtPoint = posicionesPersonajes.getOrDefault(p, new HashSet<>());
        List<Recurso> recursosEnPunto = recursosEnMapa.getOrDefault(p, new ArrayList<>());
        Animal animalEnPunto = animalesEnMapa.get(p);
        Refugio refugioEnPunto = refugiosEnMapa.get(p);
        
        mapCells[y][x].removeAll();
        mapCells[y][x].setBackground(hayTormenta ? new Color(0, 100, 0) : Color.GREEN);

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
        
        registrarEnBitacora(name, "Se movió a la posición (" + newX + ", " + newY + ")");
        updateMapCell(newX, newY);
        mapPanel.revalidate();
        mapPanel.repaint();
    }

    private void descubrirArea(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < MAP_SIZE && newY >= 0 && newY < MAP_SIZE) {
                    if (!descubierto[newY][newX]) {
                        descubierto[newY][newX] = true;
                        mapCells[newY][newX].setBackground(hayTormenta ? new Color(0, 100, 0) : Color.GREEN);
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

                // Ahora los recursos descubiertos se agregan al mapa, no al inventario
                List<Recurso> recursosEncontrados = explorador.descubrirRecursos();
                if (!recursosEncontrados.isEmpty()) {
                    recursosEnMapa.computeIfAbsent(position, k -> new ArrayList<>())
                                .addAll(recursosEncontrados);
                }

                if (Math.random() < 0.3) {
                    Animal animalDescubierto = explorador.descubrirAnimal();
                    animalesEnMapa.put(position, animalDescubierto);
                    registrarEnBitacora(name, "Ha descubierto un " + animalDescubierto.getTipoAnimal());
                }

                updateMapCell(position.x, position.y);
                registrarEnBitacora(name, "Ha explorado el área y encontrado recursos");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Solo los exploradores pueden realizar esta acción.");
        }
    }

 private void collectResources(String name) {
    Personaje personaje = personajes.get(name);
    Point position = getCharacterPosition(name);
    
    if (position != null) {
        if (personaje instanceof Recolector) {
            recolectar(name);
            return;
        }
        
        if (personaje instanceof Explorador) {
            List<Recurso> recursosEnPunto = recursosEnMapa.get(position);
            if (recursosEnPunto != null && !recursosEnPunto.isEmpty()) {
                Recurso recursoARecolectar = new Recurso(
                    recursosEnPunto.get(0).getTipo(), 
                    recursosEnPunto.get(0).getCantidad()
                );
                
                ((Explorador) personaje).recolectar(recursoARecolectar);
                recursosEnPunto.remove(0);
                if (recursosEnPunto.isEmpty()) {
                    recursosEnMapa.remove(position);
                }
                
                updateMapCell(position.x, position.y);
                registrarEnBitacora(name, "Ha recolectado " + recursoARecolectar.getCantidad() + 
                                  " " + recursoARecolectar.getTipo());
                actualizarEstadoJuego();
            } else {
                JOptionPane.showMessageDialog(this, "No hay recursos para recolectar en esta posición.");
            }
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
            registrarEnBitacora(name, "Ha comido " + tipoComida);
            actualizarEstadoJuego();
        }
    }

    private void pasarDia() {
        dios.pasarDia();
        diaLabel.setText("Día: " + dios.getDiaActual());
        for (String nombre : personajes.keySet()) {
            registrarEnBitacora(nombre, "Nuevo día: " + dios.getDiaActual());
        }
        actualizarEstadoJuego();
    }

    private void iniciarTormenta() {
        hayTormenta = true;
        climaLabel.setText("Clima: ¡TORMENTA!");
        climaLabel.setForeground(Color.RED);
        dios.generarTormenta();
        actualizarEstadoJuego();
        
        Timer tormentaTimer = new Timer(20000, e -> finalizarTormenta());
        tormentaTimer.setRepeats(false);
        tormentaTimer.start();
    }

    private void finalizarTormenta() {
        hayTormenta = false;
        climaLabel.setText("Clima: Normal");
        climaLabel.setForeground(Color.BLACK);
        actualizarEstadoJuego();
    }

    private void generarAccidente() {
        if (personajes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay personajes en el juego.");
            return;
        }
        
        String[] nombres = personajes.keySet().toArray(new String[0]);
        String personajeSeleccionado = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona un personaje para el accidente:",
            "Generar Accidente",
            JOptionPane.QUESTION_MESSAGE,
            null,
            nombres,
            nombres[0]
        );
        
        if (personajeSeleccionado != null) {
            Personaje personaje = personajes.get(personajeSeleccionado);
            dios.generarAccidente(personaje);
            registrarEnBitacora(personajeSeleccionado, "Ha sufrido un accidente");
            actualizarEstadoJuego();
        }
    }

    private void generarEnfermedad() {
        if (personajes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay personajes en el juego.");
            return;
        }
        
        String[] nombres = personajes.keySet().toArray(new String[0]);
        String personajeSeleccionado = (String) JOptionPane.showInputDialog(
            this,
            "Selecciona un personaje para la enfermedad:",
            "Generar Enfermedad",
            JOptionPane.QUESTION_MESSAGE,
            null,
            nombres,
            nombres[0]
        );
        
        if (personajeSeleccionado != null) {
            Personaje personaje = personajes.get(personajeSeleccionado);
            dios.generarEnfermedad(personaje);
            registrarEnBitacora(personajeSeleccionado, "Ha contraído una enfermedad");
            actualizarEstadoJuego();
        }
    }
    
    private Point seleccionarPosicionAtaque() {
    // Crear lista de posiciones válidas (con personajes)
    List<Point> posicionesValidas = new ArrayList<>();
    for (Map.Entry<Point, Set<String>> entry : posicionesPersonajes.entrySet()) {
        if (!entry.getValue().isEmpty()) {
            posicionesValidas.add(entry.getKey());
        }
    }

    if (posicionesValidas.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay posiciones válidas para atacar.");
        return null;
    }

    // Crear array de strings para el diálogo de selección
    String[] opciones = new String[posicionesValidas.size()];
    for (int i = 0; i < posicionesValidas.size(); i++) {
        Point p = posicionesValidas.get(i);
        Set<String> personajes = posicionesPersonajes.get(p);
        opciones[i] = "Posición (" + p.x + ", " + p.y + ") - Personajes: " + 
                      String.join(", ", personajes);
    }

    String seleccion = (String) JOptionPane.showInputDialog(
        this,
        "Selecciona la posición del ataque:",
        "Seleccionar Posición",
        JOptionPane.QUESTION_MESSAGE,
        null,
        opciones,
        opciones[0]
    );

    if (seleccion != null) {
        int index = Arrays.asList(opciones).indexOf(seleccion);
        return posicionesValidas.get(index);
    }

    return null;
}

   public void generarAtaqueAnimal() {
    if (personajes.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No hay personajes en el juego para atacar.");
        return;
    }

    String[] tiposDepredadores = {"lobo", "oso"};
    String tipoAnimal = (String) JOptionPane.showInputDialog(
        this,
        "Selecciona el tipo de depredador:",
        "Generar Ataque Animal",
        JOptionPane.QUESTION_MESSAGE,
        null,
        tiposDepredadores,
        tiposDepredadores[0]
    );

    if (tipoAnimal != null) {
        Animal animal = new Animal(tipoAnimal);
        Point posicionAtaque = seleccionarPosicionAtaque();
        
        if (posicionAtaque != null) {
            animalesEnMapa.put(posicionAtaque, animal);
            Set<String> personajesEnPosicion = posicionesPersonajes.get(posicionAtaque);
            
            if (personajesEnPosicion != null && !personajesEnPosicion.isEmpty()) {
                // El animal ataca a todos los personajes en la posición
                for (String nombrePersonaje : personajesEnPosicion) {
                    Personaje personaje = personajes.get(nombrePersonaje);
                    animal.atacarPersonaje(personaje);
                    registrarEnBitacora(nombrePersonaje, "Fue atacado por un " + tipoAnimal);
                }
            }
            
            updateMapCell(posicionAtaque.x, posicionAtaque.y);
            registrarEnBitacora("Sistema", "¡Un " + tipoAnimal + " ha aparecido en la posición (" + 
                               posicionAtaque.x + ", " + posicionAtaque.y + ")!");
        }
    }
}
    
    private void procesarAtaque(Animal animal, Point posicion, Set<String> personajesEnPosicion) {
    boolean hayCazador = false;
    Cazador cazadorPresente = null;

    // Buscar si hay un cazador
    for (String nombrePersonaje : personajesEnPosicion) {
        Personaje p = personajes.get(nombrePersonaje);
        if (p instanceof Cazador) {
            hayCazador = true;
            cazadorPresente = (Cazador) p;
            break;
        }
    }

    if (hayCazador) {
        // El cazador intenta defender a los demás
        for (String nombrePersonaje : personajesEnPosicion) {
            if (!nombrePersonaje.equals(cazadorPresente.getNombre())) {
                Personaje personajeADefender = personajes.get(nombrePersonaje);
                cazadorPresente.defender(personajeADefender, animal);
                registrarEnBitacora(cazadorPresente.getNombre(), 
                    "Intentó defender a " + personajeADefender.getNombre() + 
                    " de un " + animal.getTipoAnimal());
            }
        }
    } else {
        // El animal ataca a todos
        for (String nombrePersonaje : personajesEnPosicion) {
            Personaje personaje = personajes.get(nombrePersonaje);
            animal.atacarPersonaje(personaje);
            registrarEnBitacora(nombrePersonaje, 
                "Fue atacado por un " + animal.getTipoAnimal());
        }
    }
}

    private void generarRecursoAleatorio() {
        String[] tiposRecursos = {"madera", "piedra", "fruta", "planta medicinal", "agua"};
        Point posicion = new Point(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE));
        String tipoRecurso = tiposRecursos[random.nextInt(tiposRecursos.length)];
        int cantidad = 1 + random.nextInt(5);
        
        dios.generarRecurso(posicion, tipoRecurso, cantidad);
        updateMapCell(posicion.x, posicion.y);
    }

    private void registrarEnBitacora(String nombrePersonaje, String evento) {
        if (!bitacoras.containsKey(nombrePersonaje)) {
            bitacoras.put(nombrePersonaje, new JTextArea());
        }
        JTextArea bitacora = bitacoras.get(nombrePersonaje);
        bitacora.append("Día " + dios.getDiaActual() + ": " + evento + "\n");
    }

    private void mostrarBitacoras() {
        JDialog bitacorasDialog = new JDialog(this, "Bitácoras de Personajes", true);
        bitacorasDialog.setLayout(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        for (Map.Entry<String, JTextArea> entry : bitacoras.entrySet()) {
            String nombre = entry.getKey();
            JTextArea bitacora = entry.getValue();
            bitacora.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(bitacora);
            tabbedPane.addTab(nombre, scrollPane);
        }
        
        bitacorasDialog.add(tabbedPane, BorderLayout.CENTER);
        
        JButton cerrarButton = new JButton("Cerrar");
        cerrarButton.addActionListener(e -> bitacorasDialog.dispose());
        bitacorasDialog.add(cerrarButton, BorderLayout.SOUTH);
        
        bitacorasDialog.setSize(400, 300);
        bitacorasDialog.setLocationRelativeTo(this);
        bitacorasDialog.setVisible(true);
    }

    private void actualizarEstadoJuego() {
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                if (descubierto[i][j]) {
                    updateMapCell(j, i);
                }
            }
        }
        
        for (Map.Entry<String, Personaje> entry : personajes.entrySet()) {
            registrarEnBitacora(entry.getKey(), 
                "Energía: " + entry.getValue().getNivelEnergia() + 
                ", Salud: " + entry.getValue().getNivelSalud());
        }
        
        repaint();
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

    private void entrarRefugio(String name) {
        Personaje personaje = personajes.get(name);
        Point position = getCharacterPosition(name);
        if (position != null) {
            Refugio refugio = refugiosEnMapa.get(position);
            if (refugio != null) {
                if (refugio.añadirPersonaje(personaje)) {
                    posicionesPersonajes.get(position).remove(name);
                    updateMapCell(position.x, position.y);
                    registrarEnBitacora(name, "Ha entrado al refugio");
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
                        registrarEnBitacora(name, "Ha salido del refugio");
                    }
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, name + " no está en un refugio.");
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
                    updateMapCell(position.x, position.y);
                    registrarEnBitacora(name, "Ha construido un nuevo refugio");
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
                registrarEnBitacora(name, "Ha reparado el refugio. Nueva estabilidad: " + refugio.getEstabilidad());
            } else {
                JOptionPane.showMessageDialog(this, "No hay un refugio en esta posición para reparar.");
            }
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
                    registrarEnBitacora(name, "Ha cazado con éxito");
                } else {
                    registrarEnBitacora(name, "No ha tenido éxito en la caza");
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
                        registrarEnBitacora(name, "Ha defendido a " + personajeADefender + " de un " + animal.getTipoAnimal());
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
    Point position = getCharacterPosition(name);
    
    if (position != null && recolector.getNivelEnergia() >= 10) {
        List<Recurso> recursosEnPunto = recursosEnMapa.get(position);
        if (recursosEnPunto != null && !recursosEnPunto.isEmpty()) {
            // Obtener el primer recurso del punto
            Recurso recursoARecolectar = recursosEnPunto.get(0);
            
            // Añadir al inventario del recolector
            recolector.agregarRecursoAInventario(recursoARecolectar.getTipo(), 
                                               recursoARecolectar.getCantidad());
            
            // Eliminar el recurso del mapa
            recursosEnPunto.remove(0);
            if (recursosEnPunto.isEmpty()) {
                recursosEnMapa.remove(position);
            }
            
            // Reducir energía
            recolector.reducirEnergia(10);
            
            // Registrar en bitácora
            registrarEnBitacora(name, "Ha recolectado " + recursoARecolectar.getCantidad() + 
                              " " + recursoARecolectar.getTipo());
            
            // Actualizar la interfaz
            updateMapCell(position.x, position.y);
            actualizarEstadoJuego();
        } else {
            JOptionPane.showMessageDialog(this, 
                "No hay recursos para recolectar en esta posición.",
                "Recolección",
                JOptionPane.INFORMATION_MESSAGE);
        }
    } else if (recolector.getNivelEnergia() < 10) {
        JOptionPane.showMessageDialog(this, 
            "El recolector no tiene suficiente energía para recolectar (mínimo 10)",
            "Energía insuficiente",
            JOptionPane.WARNING_MESSAGE);
        registrarEnBitacora(name, "Intentó recolectar pero no tenía suficiente energía");
    }
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
                            registrarEnBitacora(name, "Ha entregado " + cantidad + " " + tipoRecurso + " a " + personajeReceptor);
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

   private void showStats(String name) {
    Personaje personaje = personajes.get(name);
    StringBuilder stats = new StringBuilder();
    
    stats.append("Nombre: ").append(name).append("\n");
    stats.append("Nivel de Energía: ").append(personaje.getNivelEnergia()).append("\n");
    stats.append("Nivel de Salud: ").append(personaje.getNivelSalud()).append("\n");
    
    if (personaje instanceof Recolector) {
        stats.append("Habilidad de recolección: ")
             .append(((Recolector) personaje).getHabilidadRecoleccion())
             .append("\n");
    }
    
    stats.append("\nInventario Personal:\n");
    List<Recurso> inventario = personaje.getInventario();
    if (inventario.isEmpty()) {
        stats.append("- Vacío");
    } else {
        for (Recurso recurso : inventario) {
            stats.append("- ").append(recurso.getTipo())
                 .append(": ").append(recurso.getCantidad())
                 .append("\n");
        }
    }

    JOptionPane.showMessageDialog(this, stats.toString(), 
        "Estadísticas de " + name, JOptionPane.INFORMATION_MESSAGE);
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IslandSurvival());
    }
}