package edu.uclm.esi.tysweb2023.model;

import java.util.Map;
import java.util.Random;

import edu.uclm.esi.tysweb2023.exceptions.MovimientoIlegalException;

public class Tablero4R extends Tablero {

    private char ultimoColor;
    private char winner;

    public Tablero4R() {
        super();
        this.casillas = new char[6][7];
    }
    
    public void poner(Map<String, Object> movimiento, String idUser) throws MovimientoIlegalException {

        int columna = (int) movimiento.get("columna");
        System.out.println("columna" + columna);

        if (this.winner != Character.MIN_VALUE) {
            throw new MovimientoIlegalException("La partida ha finalizado");
        }

        if (!this.jugadorConElTurno.getId().equals(idUser))
            throw new MovimientoIlegalException("No es tu turno");

        char[] col = new char[6];
        for (int i = 5; i >= 0; i--) {
            col[i] = this.casillas[i][columna];
        }

        if (col[0] != 'D') {
            throw new MovimientoIlegalException("La columna esta llena");
        }

        for (int i = 5; i >= 0; i--)
            if (this.casillas[i][columna] == 'D') {
                this.casillas[i][columna] = this.ultimoColor;
                //System.out.println("MOVIMIENTO" + this.casillas[i][columna]);
                comprobarFin();
                this.ultimoColor = this.ultimoColor == 'R' ? 'A' : 'R';
                this.jugadorConElTurno = this.jugadorConElTurno == this.players.get(0) ? this.players.get(1) : this.players.get(0);
                break;
            }
    }

    public void comprobarFin(){

        char resultado = comprobarFinJuego(this.casillas);
        if (resultado == 'R' || resultado == 'A') {
            setGanador(this.jugadorConElTurno.getName());
            setPerdedor(getGanador().equals(this.players.get(0).getName()) ? this.players.get(1).getName() : this.players.get(0).getName());
            System.out.println("¡" + getGanador() + " ha ganado!");

        } else if (resultado == 'E') {
            System.out.println("¡Empate! El juego ha terminado sin ganadores.");
        } else {
            System.out.println("El juego continúa.");
        }
    }

    public static char comprobarFinJuego(char[][] casillas) {
        // Comprobar filas, columnas y diagonales
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                // Comprobar filas
                if (casillas[i][j] == 'A' && casillas[i][j + 1] == 'A' &&
                        casillas[i][j + 2] == 'A' && casillas[i][j + 3] == 'A') {
                    return 'A';
                } else if (casillas[i][j] == 'R' && casillas[i][j + 1] == 'R' &&
                        casillas[i][j + 2] == 'R' && casillas[i][j + 3] == 'R') {
                    return 'R';
                }

                // Comprobar columnas
                if (casillas[j][i] == 'A' && casillas[j + 1][i] == 'A' &&
                        casillas[j + 2][i] == 'A' && casillas[j + 3][i] == 'A') {
                    return 'A';
                } else if (casillas[j][i] == 'R' && casillas[j + 1][i] == 'R' &&
                        casillas[j + 2][i] == 'R' && casillas[j + 3][i] == 'R') {
                    return 'R';
                }
            }
        }

        // Comprobar diagonales
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (casillas[i][j] == 'A' && casillas[i + 1][j + 1] == 'A' &&
                        casillas[i + 2][j + 2] == 'A' && casillas[i + 3][j + 3] == 'A') {
                    return 'A';
                } else if (casillas[i][j] == 'R' && casillas[i + 1][j + 1] == 'R' &&
                        casillas[i + 2][j + 2] == 'R' && casillas[i + 3][j + 3] == 'R') {
                    return 'R';
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 3; j < 6; j++) {
                if (casillas[i][j] == 'A' && casillas[i + 1][j - 1] == 'A' &&
                        casillas[i + 2][j - 2] == 'A' && casillas[i + 3][j - 3] == 'A') {
                    return 'A';
                } else if (casillas[i][j] == 'R' && casillas[i + 1][j - 1] == 'R' &&
                        casillas[i + 2][j - 2] == 'R' && casillas[i + 3][j - 3] == 'R') {
                    return 'R';
                }
            }
        }

        // Comprobar empate
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (casillas[i][j] == 'D') {
                    return 'C'; // El juego continúa
                }
            }
        }

        return 'E'; // Empate
    }

    public void iniciar() {
        this.jugadorConElTurno = this.players.get(new Random().nextInt(this.players.size()));
        this.ultimoColor = 'R';

        for (int i=0; i<6; i++)
            for (int j=0; j<7; j++)
                this.casillas[i][j] = 'D';

    }
}


