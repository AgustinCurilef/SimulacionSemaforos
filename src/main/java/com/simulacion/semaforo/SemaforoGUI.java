package com.simulacion.semaforo;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class SemaforoGUI extends JFrame {
    private final Map<String, SemaforoVisual> semaforosVisuales;
    private final Map<String, List<String>> filasAutos;
    private final List<AutoCruzando> autosCruzando;
    private JPanel panelInterseccion;
    private JPanel panelInfo;
    private JLabel labelTiempo;
    private int tiempoTranscurrido = 0;

    private static final Color COLOR_CARRETERA = new Color(64, 64, 64);
    private static final Color COLOR_LINEA = Color.WHITE;
    private static final Color COLOR_FONDO = new Color(34, 139, 34);

    public SemaforoGUI() {
        semaforosVisuales = new HashMap<>();
        filasAutos = new HashMap<>();
        autosCruzando = new ArrayList<>();

        // Inicializar estructuras de datos
        String[] direcciones = { "Norte", "Sur", "Este", "Oeste" };
        for (String direccion : direcciones) {
            semaforosVisuales.put(direccion, new SemaforoVisual(ColorSemaforo.ROJO));
            filasAutos.put(direccion, new ArrayList<>());
        }

        initializeGUI();

        // Timer para actualizar el tiempo
        Timer timerTiempo = new Timer(1000, _ -> {
            tiempoTranscurrido++;
            labelTiempo.setText("Tiempo: " + tiempoTranscurrido + "s");
        });
        timerTiempo.start();

        // Timer para repintar la interfaz
        Timer timerRepaint = new Timer(100, _ -> {
            panelInterseccion.repaint();
            actualizarPanelInfo();
        });
        timerRepaint.start();
    }

    private void initializeGUI() {
        setTitle("Simulación de Semáforos - Vista en Tiempo Real");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel principal de la intersección
        panelInterseccion = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarInterseccion((Graphics2D) g);
            }
        };
        panelInterseccion.setPreferredSize(new Dimension(800, 600));
        panelInterseccion.setBackground(COLOR_FONDO);

        // Panel de información
        panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setPreferredSize(new Dimension(250, 600));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));

        labelTiempo = new JLabel("Tiempo: 0s");
        labelTiempo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(labelTiempo);

        add(panelInterseccion, BorderLayout.CENTER);
        add(panelInfo, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

    private void dibujarInterseccion(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = panelInterseccion.getWidth();
        int height = panelInterseccion.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int carreteraAncho = 120;
        int interseccionTamaño = 150;

        // Dibujar carreteras
        g2d.setColor(COLOR_CARRETERA);
        // Carretera horizontal
        g2d.fillRect(0, centerY - carreteraAncho / 2, width, carreteraAncho);
        // Carretera vertical
        g2d.fillRect(centerX - carreteraAncho / 2, 0, carreteraAncho, height);

        // Dibujar intersección central
        g2d.fillRect(centerX - interseccionTamaño / 2, centerY - interseccionTamaño / 2,
                interseccionTamaño, interseccionTamaño);

        // Dibujar líneas divisorias
        g2d.setColor(COLOR_LINEA);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[] { 10 }, 0));

        // Línea horizontal central
        g2d.drawLine(0, centerY, centerX - interseccionTamaño / 2, centerY);
        g2d.drawLine(centerX + interseccionTamaño / 2, centerY, width, centerY);

        // Línea vertical central
        g2d.drawLine(centerX, 0, centerX, centerY - interseccionTamaño / 2);
        g2d.drawLine(centerX, centerY + interseccionTamaño / 2, centerX, height);

        // Dibujar semáforos
        dibujarSemaforo(g2d, centerX, 50, "Norte"); // Norte
        dibujarSemaforo(g2d, centerX, height - 50, "Sur"); // Sur
        dibujarSemaforo(g2d, 50, centerY, "Este"); // Este
        dibujarSemaforo(g2d, width - 50, centerY, "Oeste"); // Oeste

        // Dibujar filas de autos
        dibujarFilasAutos(g2d, centerX, centerY, carreteraAncho);

        // Dibujar autos cruzando
        synchronized (autosCruzando) {
            for (AutoCruzando auto : autosCruzando) {
                dibujarAuto(g2d, auto.x, auto.y, auto.color);
            }
        }

    }

    private void dibujarSemaforo(Graphics2D g2d, int x, int y, String direccion) {
        SemaforoVisual semaforo = semaforosVisuales.get(direccion);

        // Fondo del semáforo
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x - 15, y - 40, 30, 80);
        g2d.setColor(Color.GRAY);
        g2d.drawRect(x - 15, y - 40, 30, 80);

        // Luces del semáforo
        Color colorRojo = semaforo.color == ColorSemaforo.ROJO ? Color.RED : Color.DARK_GRAY;
        Color colorAmarillo = semaforo.color == ColorSemaforo.AMARILLO ? Color.YELLOW : Color.DARK_GRAY;
        Color colorVerde = semaforo.color == ColorSemaforo.VERDE ? Color.GREEN : Color.DARK_GRAY;

        g2d.setColor(colorRojo);
        g2d.fillOval(x - 10, y - 35, 20, 20);

        g2d.setColor(colorAmarillo);
        g2d.fillOval(x - 10, y - 10, 20, 20);

        g2d.setColor(colorVerde);
        g2d.fillOval(x - 10, y + 15, 20, 20);

        // Etiqueta de dirección
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(direccion);
        g2d.drawString(direccion, x - textWidth / 2, y + 60);
    }

    private void dibujarFilasAutos(Graphics2D g2d, int centerX, int centerY, int carreteraAncho) {
        int autoSize = 20;
        int spacing = 25;

        // Norte - autos esperando hacia abajo
        List<String> autosNorte = filasAutos.get("Norte");
        for (int i = 0; i < autosNorte.size(); i++) {
            int x = centerX - 30; // Ligeramente a la izquierda
            int y = centerY - 100 - (i * spacing);
            dibujarAuto(g2d, x, y, Color.BLUE);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(autosNorte.get(i), x - 10, y - 5);
        }

        // Sur - autos esperando hacia arriba
        List<String> autosSur = filasAutos.get("Sur");
        for (int i = 0; i < autosSur.size(); i++) {
            int x = centerX + autoSize;
            int y = centerY + 100 + (i * spacing);
            dibujarAuto(g2d, x, y, Color.RED);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(autosSur.get(i), x - 10, y + 25);
        }

        // Este - autos esperando hacia la derecha
        List<String> autosEste = filasAutos.get("Este");
        for (int i = 0; i < autosEste.size(); i++) {
            int x = centerX - 100 - (i * spacing);
            int y = centerY + autoSize;
            dibujarAuto(g2d, x, y, Color.GREEN);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(autosEste.get(i), x - 15, y - 5);
        }

        // Oeste - autos esperando hacia la izquierda
        List<String> autosOeste = filasAutos.get("Oeste");
        for (int i = 0; i < autosOeste.size(); i++) {
            int x = centerX + 100 + (i * spacing);
            int y = centerY - autoSize;
            dibujarAuto(g2d, x, y, Color.ORANGE);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(autosOeste.get(i), x - 10, y + 25);
        }
    }

    private void dibujarAuto(Graphics2D g2d, int x, int y, Color color) {
        // Cuerpo del auto
        g2d.setColor(color);
        g2d.fillRect(x, y, 20, 15);

        // Borde del auto
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, 20, 15);

        // Ruedas del auto
        g2d.fillOval(x + 2, y + 12, 4, 4); // Rueda izquierda
        g2d.fillOval(x + 14, y + 12, 4, 4); // Rueda derecha
    }

    private void actualizarPanelInfo() {
        panelInfo.removeAll();

        panelInfo.add(labelTiempo);
        panelInfo.add(Box.createVerticalStrut(10));

        // Información de semáforos
        JLabel labelSemaforos = new JLabel("Estado de Semáforos:");
        labelSemaforos.setFont(new Font("Arial", Font.BOLD, 12));
        panelInfo.add(labelSemaforos);

        for (String direccion : semaforosVisuales.keySet()) {
            ColorSemaforo color = semaforosVisuales.get(direccion).color;
            JLabel labelDireccion = new JLabel(direccion + ": " + color.getIcono() + " " + color.name());
            panelInfo.add(labelDireccion);
        }

        panelInfo.add(Box.createVerticalStrut(10));

        // Información de filas
        JLabel labelFilas = new JLabel("Autos en Espera:");
        labelFilas.setFont(new Font("Arial", Font.BOLD, 12));
        panelInfo.add(labelFilas);

        for (String direccion : filasAutos.keySet()) {
            int cantidad = filasAutos.get(direccion).size();
            JLabel labelFila = new JLabel(direccion + ": " + cantidad + " autos");
            panelInfo.add(labelFila);
        }

        panelInfo.add(Box.createVerticalStrut(10));

        // Autos cruzando
        JLabel labelCruzando = new JLabel("Autos Cruzando: " + autosCruzando.size());
        labelCruzando.setFont(new Font("Arial", Font.BOLD, 12));
        panelInfo.add(labelCruzando);

        panelInfo.revalidate();
        panelInfo.repaint();
    }

    // Métodos públicos para actualizar desde la simulación
    public void actualizarSemaforo(String direccion, ColorSemaforo color) {
        SwingUtilities.invokeLater(() -> {
            semaforosVisuales.get(direccion).color = color;
        });
    }

    public void agregarAutoEnFila(String direccion, String nombreAuto) {
        SwingUtilities.invokeLater(() -> {
            filasAutos.get(direccion).add(nombreAuto);
        });
    }

    public void removerAutoDeFile(String direccion) {
        SwingUtilities.invokeLater(() -> {
            if (!filasAutos.get(direccion).isEmpty()) {
                filasAutos.get(direccion).remove(0);
            }
        });
    }

    public void iniciarCruceAuto(String nombreAuto, String direccion) {
        SwingUtilities.invokeLater(() -> {
            int centerX = panelInterseccion.getWidth() / 2;
            int centerY = panelInterseccion.getHeight() / 2;

            Color colorAuto;
            int startX, startY, endX, endY;

            int offset = 20;

            switch (direccion) {
                case "Norte":
                    colorAuto = Color.BLUE;
                    startX = centerX - 30; // Ligeramente a la izquierda
                    startY = centerY - 75;
                    endX = centerX - offset;
                    endY = centerY + 90;
                    break;
                case "Sur":
                    colorAuto = Color.RED;
                    startX = centerX + offset; // Ligeramente a la derecha
                    startY = centerY + 75;
                    endX = centerX + offset;
                    endY = centerY - 90;
                    break;
                case "Este":
                    colorAuto = Color.GREEN;
                    startX = centerX - 75;
                    startY = centerY + offset; // Ligeramente hacia abajo
                    endX = centerX + 90;
                    endY = centerY + offset;
                    break;
                case "Oeste":
                    colorAuto = Color.ORANGE;
                    startX = centerX + 75;
                    startY = centerY - offset; // Ligeramente hacia arriba
                    endX = centerX - 90;
                    endY = centerY - offset;
                    break;
                default:
                    return;
            }

            AutoCruzando auto = new AutoCruzando(nombreAuto, startX, startY, endX, endY, colorAuto);
            synchronized (autosCruzando) {
                autosCruzando.add(auto);
            }

            // Animar el cruce
            Timer animacion = new Timer(50, null);
            animacion.addActionListener(_ -> {
                if (auto.mover()) {
                    synchronized (autosCruzando) {
                        autosCruzando.remove(auto);
                    }
                    animacion.stop();
                }
                panelInterseccion.repaint();
            });
            animacion.start();
        });
    }

}