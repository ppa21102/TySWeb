package edu.uclm.esi.tysweb2023.model;

import edu.uclm.esi.tysweb2023.exceptions.MovimientoIlegalException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Tablero {

    protected List<User> players;
    protected User jugadorConElTurno;
    private String id;

    private String ganador;
    private String perdedor;
    protected char[][] casillas;
    protected String status; 
    

	protected int movimientosRestantesJugador1;
    protected int movimientosRestantesJugador2;
    protected int barcosRestantes;
    protected int barcosHundidosJugador1;
    protected int barcosHundidosJugador2;

    public Tablero() {
        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
        this.status = "CREATED"; 
    }

    public String getId() {
        return id;
    }

    public void addUser(User user) {
        this.players.add(user);
    }

    public List<User> getPlayers() {
        return players;
    }

    public User getJugadorConElTurno() {
        return jugadorConElTurno;
    }

    public String getGanador() {
        return ganador;
    }
    
    public char[][] getCasillas() {
        return casillas;
    }
    
    public String getStatus() {
        return status;
    }


    public String getPerdedor() {return perdedor;}

    public void setGanador(String winner) {this.ganador = winner;}

    public void setPerdedor(String loser) {this.perdedor = loser;}
    
    public void setStatus(String status) {this.status = status;}
    
    public int getMovimientosRestantesJugador1() {
		return movimientosRestantesJugador1;
	}

	public void setMovimientosRestantesJugador1(int movimientosRestantesJugador1) {
		this.movimientosRestantesJugador1 = movimientosRestantesJugador1;
	}

	public int getMovimientosRestantesJugador2() {
		return movimientosRestantesJugador2;
	}

	public void setMovimientosRestantesJugador2(int movimientosRestantesJugador2) {
		this.movimientosRestantesJugador2 = movimientosRestantesJugador2;
	}

	public int getBarcosRestantes() {
		return barcosRestantes;
	}

	public void setBarcosRestantes(int barcosRestantes) {
		this.barcosRestantes = barcosRestantes;
	}

	public int getBarcosHundidosJugador1() {
		return barcosHundidosJugador1;
	}

	public void setBarcosHundidosJugador1(int barcosHundidosJugador1) {
		this.barcosHundidosJugador1 = barcosHundidosJugador1;
	}

	public int getBarcosHundidosJugador2() {
		return barcosHundidosJugador2;
	}

	public void setBarcosHundidosJugador2(int barcosHundidosJugador2) {
		this.barcosHundidosJugador2 = barcosHundidosJugador2;
	}


    public abstract void poner(Map<String, Object> movimiento, String idUser) throws MovimientoIlegalException;

    public abstract void iniciar();

}
