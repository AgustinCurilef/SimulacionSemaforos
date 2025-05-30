package com.simulacion.semaforo;

import java.awt.Color;

class AutoCruzando {
    String nombre;
    int x, y;
    int endX, endY;
    int stepX, stepY;
    Color color;
    int pasos;
    int pasosTotales;

    AutoCruzando(String nombre, int startX, int startY, int endX, int endY, Color color) {
        this.nombre = nombre;
        this.x = startX;
        this.y = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;

        this.pasosTotales = 40; // Número de pasos para completar el cruce
        this.stepX = (endX - startX) / pasosTotales;
        this.stepY = (endY - startY) / pasosTotales;
        this.pasos = 0;
    }

    boolean mover() {
        if (pasos >= pasosTotales) {
            return true; // Terminó de cruzar
        }

        x += stepX;
        y += stepY;
        pasos++;

        return false;
    }
}