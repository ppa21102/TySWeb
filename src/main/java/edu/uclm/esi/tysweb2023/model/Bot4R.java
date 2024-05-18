package edu.uclm.esi.tysweb2023.model;

import java.util.Random;

public class Bot4R {
	public int getNextMove(Tablero4R tablero) {
        Random random = new Random();
        int column;
        do {
            column = random.nextInt(7); // Assuming there are 7 columns
        } while (!isValidMove(tablero, column));
        return column;
    }

    private boolean isValidMove(Tablero4R tablero, int column) {
        for (int i = 0; i < 6; i++) { // Assuming there are 6 rows
            if (tablero.getCasillas()[i][column] == 'D') {
                return true;
            }
        }
        return false;
    }
}
