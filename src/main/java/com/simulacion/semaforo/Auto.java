package com.simulacion.semaforo;

public class Auto extends Thread {
    private final Semaforo semaforo;
    private final String nombre;
    private final int intentos;

    public Auto(Semaforo semaforo, String nombre, int intentos) {
        this.semaforo = semaforo;
        this.nombre = nombre;
        this.intentos = intentos;
        this.setName(nombre); // Establecer el nombre del hilo para identificación
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= intentos; i++) {
                // Registrar el auto en el semáforo (se pone en la fila)
                semaforo.registrarAuto(this);

                // Esperar su turno y la luz verde
                semaforo.cruzarAuto();

                System.out.println(nombre + " está cruzando por " + semaforo.getNombre());

                // Simular el tiempo de cruce (la animación se maneja en la GUI)
                Thread.sleep(2000);

                System.out.println(nombre + " terminó de cruzar el semáforo en intento #" + i);

                // Si no es el último intento, esperar antes de volver a intentar
                if (i < intentos) {
                    Thread.sleep(3000 + (int) (Math.random() * 2000)); // Tiempo variable antes de volver
                }
            }

            System.out.println(nombre + " completó todos sus cruces y se retiró.");

        } catch (InterruptedException e) {
            System.out.println(nombre + " fue interrumpido.");
            Thread.currentThread().interrupt();
        }
    }

    public String getNombre() {
        return nombre;
    }

    public Semaforo getSemaforo() {
        return semaforo;
    }

    public int getIntentos() {
        return intentos;
    }
}