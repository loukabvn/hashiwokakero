package model;

import util.Contract;

/**
 * Types des coordonnées.
 * Les attributs x et y sont public et final pour y faciliter l'accès sans
 * pouvoir les modifier.
 *
 */
public class Coord {
	
	// ATTRIBUTS
	
	public final int x;
	public final int y;
	
	// CONSTRUCTEURS
	
	public Coord(int x, int y) {
		Contract.checkCondition(x >= 0 && y >= 0);
		
		this.x = x;
		this.y = y;
	}
	
	// REQUETES
	
	@Override
	public String toString() {
		return "x : " + x + ", y : " + y;
	}
}
