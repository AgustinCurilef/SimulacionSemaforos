package com.simulacion.semaforo;

import java.util.LinkedList;
import java.util.Queue;

public class Semaforo {
    private ColorSemaforo color;
    private final String nombre;
    private final Queue<Auto> colaAutos = new LinkedList<>();
    private SemaforoGUI gui; // Referencia a la GUI

    public Semaforo(String nombre) {
        this.nombre = nombre;
        this.color = ColorSemaforo.ROJO;
    }

    // Método para establecer la referencia a la GUI
    public void setGUI(SemaforoGUI gui) {
        this.gui = gui;
    }

    public synchronized void cambiarColor(ColorSemaforo nuevoColor) {
        this.color = nuevoColor;
        System.out.println("🔄 Semáforo " + nombre + " cambió a: " + color);

        // Actualizar la GUI
        if (gui != null) {
            gui.actualizarSemaforo(nombre, nuevoColor);
        }

        if (nuevoColor == ColorSemaforo.VERDE) {
            notifyAll(); // Despertar autos esperando
        }
    }

    public synchronized void registrarAuto(Auto auto) {
        colaAutos.add(auto);
        System.out.println(auto.getNombre() + " llegó al semáforo " + nombre + " y se puso en la fila.");

        // Actualizar la GUI
        if (gui != null) {
            gui.agregarAutoEnFila(nombre, auto.getNombre());
        }
    }

    public synchronized void cruzarAuto() {
        while (color != ColorSemaforo.VERDE || colaAutos.isEmpty() || colaAutos.peek() != Thread.currentThread()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        // Es su turno y está en verde
        Auto auto = (Auto) Thread.currentThread();
        colaAutos.poll(); // Sale de la fila
        // Esperar un poco antes de cruzar para que no se encimen visualmente
        try {
            Thread.sleep(800); // Ajustá el tiempo (milisegundos) para verlos separados
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Actualizar la GUI - remover de la fila y iniciar animación de cruce
        if (gui != null) {
            gui.removerAutoDeFile(nombre);
            gui.iniciarCruceAuto(auto.getNombre(), nombre);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public ColorSemaforo getColor() {
        return color;
    }

    public int getTamañoFila() {
        return colaAutos.size();
    }
}