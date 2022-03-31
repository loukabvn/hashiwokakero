package model;

import util.Contract;

/**
 * Type des ponts.
 * Un pont contient un nombre, 1 ou 2, pour signifier si c'est un simple ou un
 * double pont, et une direction.
 *
 */
public class Bridge {
	
	// ATTRIBUTS
	
	private final Direction dir;
	private int number;
	
	// CONSTRUCTEURS
	
	public Bridge(Direction d, int value) {
		Contract.checkCondition(d != null && value == 1 || value == 2);
		
		dir = d;
		number = value;
	}
	
	// REQUETES
	
	public Direction getDir() {
		return dir;
	}
	
	public int nbBridges() {
		return number;
	}
	
	// COMMANDES
	
	public void addBridge() {
		Contract.checkCondition(number == 1);
		
		number++;
	}
	
	public void removeBridge() {
		Contract.checkCondition(number == 2);
		
		number--;
	}
}
