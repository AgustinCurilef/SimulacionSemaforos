package com.simulacion.semaforo;

public class ControladorSemaforos implements Runnable {

    private final Semaforo semNorte;
    private final Semaforo semSur;
    private final Semaforo semEste;
    private final Semaforo semOeste;

    private final int duracionVerde;
    private final int duracionAmarillo;

    public ControladorSemaforos(Semaforo semNorte, Semaforo semSur, Semaforo semEste, Semaforo semOeste,
            int duracionVerde, int duracionAmarillo) {
        this.semNorte = semNorte;
        this.semSur = semSur;
        this.semEste = semEste;
        this.semOeste = semOeste;
        this.duracionVerde = duracionVerde;
        this.duracionAmarillo = duracionAmarillo;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Norte-Sur en verde, Este-Oeste en rojo
                semNorte.cambiarColor(ColorSemaforo.VERDE);
                semSur.cambiarColor(ColorSemaforo.VERDE);
                semEste.cambiarColor(ColorSemaforo.ROJO);
                semOeste.cambiarColor(ColorSemaforo.ROJO);
                Thread.sleep(duracionVerde * 1000);

                semNorte.cambiarColor(ColorSemaforo.AMARILLO);
                semSur.cambiarColor(ColorSemaforo.AMARILLO);
                Thread.sleep(duracionAmarillo * 1000);

                semNorte.cambiarColor(ColorSemaforo.ROJO);
                semSur.cambiarColor(ColorSemaforo.ROJO);

                // Ahora Este-Oeste en verde, Norte-Sur en rojo
                semEste.cambiarColor(ColorSemaforo.VERDE);
                semOeste.cambiarColor(ColorSemaforo.VERDE);
                Thread.sleep(duracionVerde * 1000);

                semEste.cambiarColor(ColorSemaforo.AMARILLO);
                semOeste.cambiarColor(ColorSemaforo.AMARILLO);
                Thread.sleep(duracionAmarillo * 1000);

                semEste.cambiarColor(ColorSemaforo.ROJO);
                semOeste.cambiarColor(ColorSemaforo.ROJO);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
