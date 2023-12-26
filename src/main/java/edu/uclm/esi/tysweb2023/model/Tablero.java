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

    public Tablero() {
        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
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

    public abstract void poner(Map<String, Object> movimiento, String idUser) throws MovimientoIlegalException;

    public abstract void iniciar();
}
