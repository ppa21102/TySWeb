package edu.uclm.esi.tysweb2023.exceptions;

public class MovimientoIlegalException  extends Exception {
	public MovimientoIlegalException (String errorMensaje) {
		super (errorMensaje);
	}
}