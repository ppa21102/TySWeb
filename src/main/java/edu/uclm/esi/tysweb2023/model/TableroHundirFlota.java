package edu.uclm.esi.tysweb2023.model;

import java.util.Map;
import java.util.Random;

import edu.uclm.esi.tysweb2023.exceptions.MovimientoIlegalException;

public class TableroHundirFlota extends Tablero {

    public TableroHundirFlota() {
        super();
        this.casillas = new char[10][10];
        this.movimientosRestantesJugador1 = 10;
        this.movimientosRestantesJugador2 = 10;
        this.barcosRestantes = 10;
        this.barcosHundidosJugador1 = 0;
        this.barcosHundidosJugador2 = 0;
    }

    @Override
    public void poner(Map<String, Object> movimiento, String idUser) throws MovimientoIlegalException {
        int fila = (int) movimiento.get("fila");
        int columna = (int) movimiento.get("columna");

        if (this.status.equals("COMPLETED")) {
            throw new MovimientoIlegalException("La partida ha finalizado");
        }

        if (!this.jugadorConElTurno.getId().equals(idUser)) {
            throw new MovimientoIlegalException("No es tu turno");
        }

        if (this.casillas[fila][columna] == 'X' || this.casillas[fila][columna] == 'O') {
            throw new MovimientoIlegalException("Ya has disparado en esta posición");
        }

        // JUGADOR 1: A --> BARCO y O --> AGUA
        // JUGADOR 1: R --> BARCO y F --> AGUA
        // Realiza el disparo y actualiza el tablero
        if (this.casillas[fila][columna] == 'B') {
        	//A para un disparo exitoso de un jugador Y R para disparo exitoso del otro jugador
            this.casillas[fila][columna] = (this.jugadorConElTurno == this.players.get(0)) ? 'A' : 'R';
            this.barcosRestantes--;
            incrementarBarcosHundidos();
        } else {
        	//O para un disparo exitoso de un jugador Y F para disparo exitoso del otro jugador
            this.casillas[fila][columna] = (this.jugadorConElTurno == this.players.get(0)) ? 'O' : 'F';
        }
        
        System.out.println("Barcos restantes: " + barcosRestantes);
        System.out.println("movimientosRestantesJugador1: " + movimientosRestantesJugador1);
        System.out.println("movimientosRestantesJugador2: " + movimientosRestantesJugador2);

        comprobarFin();
    }

    private void incrementarBarcosHundidos() {
        if (this.jugadorConElTurno == this.players.get(0)) {
            barcosHundidosJugador1++;
        } else {
            barcosHundidosJugador2++;
        }
    }

    @Override
    public void iniciar() {
        this.jugadorConElTurno = this.players.get(new Random().nextInt(this.players.size()));
        this.movimientosRestantesJugador1 = 10;
        this.movimientosRestantesJugador2 = 10;
        this.barcosRestantes = 10;
        this.barcosHundidosJugador1 = 0;
        this.barcosHundidosJugador2 = 0;

        // Inicializa el tablero y coloca los barcos de manera aleatoria
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.casillas[i][j] = 'D'; // 'D' representa agua
            }
        }

        colocarBarcosAleatorios();
    }

    private void colocarBarcosAleatorios() {
        Random random = new Random();

        // Coloca 10 barcos de manera aleatoria
        for (int i = 0; i < 10; i++) {
            int fila;
            int columna;

            do {
                fila = random.nextInt(10);
                columna = random.nextInt(10);
            } while (this.casillas[fila][columna] == 'B'); // Si ya hay un barco en esta posición, busca otra

            this.casillas[fila][columna] = 'B'; // 'B' representa un barco
        }
    }

    private void actualizarTurnoYMovimientos() {
        if (this.jugadorConElTurno == this.players.get(0)) {
            this.jugadorConElTurno = this.players.get(1);
            setMovimientosRestantesJugador1(getMovimientosRestantesJugador1() - 1);
        } else {
            this.jugadorConElTurno = this.players.get(0);
            this.movimientosRestantesJugador2--;
        }
    }

    private void determinarGanador() {
        if (barcosHundidosJugador1 > barcosHundidosJugador2) {
            setGanador(this.players.get(0).getId());
            setPerdedor(this.players.get(1).getId());
            System.out.println("¡" + getGanador() + " ha ganado!");
        } else if (barcosHundidosJugador1 < barcosHundidosJugador2) {
            setGanador(this.players.get(1).getId());
            setPerdedor(this.players.get(0).getId());
            System.out.println("¡" + getGanador() + " ha ganado!");
        } else {
            System.out.println("La partida ha terminado en empate.");
        }


    }
    
    private void comprobarFin() {
        // Verifica si se han hundido todos los barcos o si ambos jugadores han agotado sus movimientos
        if (barcosRestantes == 0 || (movimientosRestantesJugador1 == 0 && movimientosRestantesJugador2 == 0)) {
            determinarGanador();
            setStatus("COMPLETED");
        } else {
            // Cambia de turno y actualiza los movimientos restantes
            actualizarTurnoYMovimientos();
        }
    }
}
