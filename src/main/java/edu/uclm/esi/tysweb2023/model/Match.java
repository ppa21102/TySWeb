package edu.uclm.esi.tysweb2023.model;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmatches")
    private int id;

    @JoinColumn(name = "id_user")
    private String idUser;
    private int partidasJugadas;
    private int partidasGanadas;
    private int partidasPerdidas;
    private int partidasEmpatadas;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setUser(String idUser) {
		this.idUser = idUser;
	}

	public int getPartidasJugadas() {
		return partidasJugadas;
	}

	public void setPartidasJugadas(int partidasJugadas) {
		this.partidasJugadas = partidasJugadas;
	}

	public int getPartidasGanadas() {
		return partidasGanadas;
	}

	public void setPartidasGanadas(int partidasGanadas) {
		this.partidasGanadas = partidasGanadas;
	}

	public int getPartidasPerdidas() {
		return partidasPerdidas;
	}

	public void setPartidasPerdidas(int partidasPerdidas) {
		this.partidasPerdidas = partidasPerdidas;
	}

	public int getPartidasEmpatadas() {
		return partidasEmpatadas;
	}

	public void setPartidasEmpatadas(int partidasEmpatadas) {
		this.partidasEmpatadas = partidasEmpatadas;
	}


}
