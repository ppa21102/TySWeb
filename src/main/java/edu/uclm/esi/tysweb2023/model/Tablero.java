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

    public abstract void poner(Map<String, Object> movimiento, String idUser) throws MovimientoIlegalException;

    public abstract void iniciar();
}
