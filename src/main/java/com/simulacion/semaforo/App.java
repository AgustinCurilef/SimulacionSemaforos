package com.simulacion.semaforo;

import java.util.Random;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) throws InterruptedException {
        // Crear la GUI en el hilo de eventos de Swing
        SemaforoGUI gui = new SemaforoGUI();
        SwingUtilities.invokeLater(() -> gui.setVisible(true));

        // Esperar un poco para que se inicialice la GUI
        Thread.sleep(1000);

        // Crear semáforos
        Semaforo semNorte = new Semaforo("Norte");
        Semaforo semSur = new Semaforo("Sur");
        Semaforo semEste = new Semaforo("Este");
        Semaforo semOeste = new Semaforo("Oeste");

        // Conectar semáforos con la GUI
        semNorte.setGUI(gui);
        semSur.setGUI(gui);
        semEste.setGUI(gui);
        semOeste.setGUI(gui);

        int duracionVerde = 5;
        int duracionAmarillo = 2;

        // Crear y iniciar el controlador de semáforos
        ControladorSemaforos controlador = new ControladorSemaforos(
                semNorte, semSur, semEste, semOeste, duracionVerde, duracionAmarillo);
        Thread hiloControlador = new Thread(controlador);
        hiloControlador.setDaemon(true);
        hiloControlador.start();

        // Array de semáforos para selección aleatoria
        Semaforo[] semaforos = { semNorte, semSur, semEste, semOeste };
        Random random = new Random();

        // Crear autos de manera continua
        Thread creadorAutos = new Thread(() -> {
            try {
                for (int i = 1; i <= 50; i++) { // Incrementé el número de autos para una simulación más larga
                    Semaforo semaforoAleatorio = semaforos[random.nextInt(semaforos.length)];
                    new Auto(semaforoAleatorio, "Auto " + i, random.nextInt(3) + 1).start();
                    Thread.sleep(1000 + random.nextInt(3000)); // Intervalo aleatorio entre autos
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        creadorAutos.start();

        // Mostrar mensaje en consola
        System.out.println("Simulación iniciada. Observa la ventana gráfica.");
        System.out.println("Presiona Ctrl+C para terminar la simulación.");

        // Mantener el programa corriendo
        try {
            Thread.sleep(120000); // 2 minutos de simulación
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Simulación terminada.");
        System.exit(0);
    }
}