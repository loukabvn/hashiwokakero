package model;

import util.Contract;

public class Island {
	
	// CONSTANTES
	
	private static final int DIRECTION_NUMBER = Direction.values().length;
	private static final int BRIDGE_NB_LIMIT = 2;
	
	// ATTRIBUTS
	
	private final int bridgesNb;
	private int allBridgesPlaced;
	private int[] bridgesPlaced;
	private Island[] neighbors;
	private boolean isChanged;
	
	// CONSTRUCTEURS
	
	public Island(int bridgesNb) {
		Contract.checkCondition(1 <= bridgesNb && bridgesNb <= 8);
		
		this.bridgesNb = bridgesNb;
		allBridgesPlaced = 0;
		bridgesPlaced = new int[DIRECTION_NUMBER];
		neighbors = new Island[DIRECTION_NUMBER];
		for (Direction d : Direction.values()) {
			bridgesPlaced[d.ordinal()] = 0;
			neighbors[d.ordinal()] = null;
		}
		isChanged = false;
	}
	
	// REQUETES
	
	/**
	 * Nombre de ponts initial.
	 */
	public int bridgesNb() {
		return bridgesNb;
	}
	
	/**
	 * Total du nombre de ponts placé.
	 */
	public int allBridgesPlaced() {
		return allBridgesPlaced;
	}
	
	/**
	 * Nombre de ponts restants à placés.
	 */
	public int bridgesToPlace() {
		return bridgesNb - allBridgesPlaced;
	}
	
	/**
	 * Nombre de ponts placés dans la direction d.
	 */
	public int bridgesPlaced(Direction d) {
		Contract.checkCondition(d != null);
		
		return bridgesPlaced[d.ordinal()];
	}
	
	/**
	 * Nombre total de voisins.
	 */
	public int neighborsNb() {
		int n = 0;
		for (Direction d : Direction.values()) {
			if (neighbors[d.ordinal()] != null) {
				n++;
			}
		}
		return n;
	}
	
	/**
	 * Renvoie true si le nombre de ponts placés est égale au nombre de ponts à
	 * placer.
	 */
	public boolean isComplete() {
		return allBridgesPlaced == bridgesNb;
	}
	
	/**
	 * Renvoie true si l'île courante possède i comme voisin dans la
	 * direction d.
	 */
	public boolean isNeighbor(Direction d, Island i) {
		Contract.checkCondition(i != null && d != null);
		
		return neighbors[d.ordinal()] == i;
	}
	
	/**
	 * Renvoie true si l'on peut construire un pont entre l'île courante et i,
	 * dans la direction d.
	 */
	public boolean canBuildBridge(Direction d, Island i) {
		Contract.checkCondition(i != null && d != null);
		
		return this.allBridgesPlaced() < this.bridgesNb()
				&& i.allBridgesPlaced() < i.bridgesNb()
				&& this.bridgesPlaced(d) < BRIDGE_NB_LIMIT
				&& i.bridgesPlaced(d.opposite()) < BRIDGE_NB_LIMIT;
	}
	
	/**
	 * Renvoie le voisin de l'île dans la direction d et null s'il n'y en a pas.
	 */
	public Island neighbor(Direction d) {
		Contract.checkCondition(d != null);
		
		return neighbors[d.ordinal()];
	}
	
	// COMMANDES
	
	/**
	 * Construit un pont entre l'île courante et neighbor dans la direction d.
	 */
	public void buildBridge(Direction d, Island neighbor) {
		Contract.checkCondition(d != null && neighbor != null);
		
		if (!isChanged) {
			this.neighbors[d.ordinal()] = neighbor;
			this.bridgesPlaced[d.ordinal()] += 1;
			this.allBridgesPlaced += 1;
			isChanged = true;
			neighbor.buildBridge(d.opposite(), this);
		}
		isChanged = false;
	}
	
	/**
	 * Retire un pont entre l'île courante et neighbor dans la direction d.
	 */
	public void removeBridge(Direction d, Island neighbor) {
		Contract.checkCondition(d != null && neighbor != null
				&& isChanged || isNeighbor(d, neighbor));
		
		if (!isChanged) {
			this.bridgesPlaced[d.ordinal()] -= 1;
			if (this.bridgesPlaced[d.ordinal()] == 0) {
				this.neighbors[d.ordinal()] = null;
			}
			this.allBridgesPlaced -= 1;
			isChanged = true;
			neighbor.removeBridge(d.opposite(), this);
		}
		isChanged = false;
	}
	
	/**
	 * Supprime tous les ponts de l'île.
	 */
	public void clearBridges() {
		allBridgesPlaced = 0;
		for (Direction d : Direction.values()) {
			bridgesPlaced[d.ordinal()] = 0;
			neighbors[d.ordinal()] = null;
		}
	}
}
