package com.simulacion.semaforo;

public enum ColorSemaforo {
    ROJO("🔴", -1), // Se calcula en tiempo de ejecución
    VERDE("🟢", 5),
    AMARILLO("🟡", 2);

    private final String icono;
    private final int duracionSegundos;

    ColorSemaforo(String icono, int duracionSegundos) {
        this.icono = icono;
        this.duracionSegundos = duracionSegundos;
    }

    public String getIcono() {
        return icono;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    @Override
    public String toString() {
        return icono + " (" + name() + ")";
    }
}
