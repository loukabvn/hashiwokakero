package model;

import util.Contract;

/**
 * Types des graphes représentées par matrice d'accessibilité.
 */
public class Graph {
	
	// ATTRIBUTS
	
	private boolean[][] accessMatrix;
	private int nbElem;
	
	// CONSTRUCTEURS
	
	public Graph(int size) {
		Contract.checkCondition(size > 0);
		
		accessMatrix = new boolean[size][size];
		nbElem = size;
	}
	
	// REQUETES
	
	public int getNbElem() {
		return nbElem;
	}
	
	/**
	 * Teste si i et j sont reliés par une arête.
	 */
	public boolean areAccessible(int i, int j) {
		Contract.checkCondition(validIndexs(i, j));
		
		return accessMatrix[i][j];
	}
	
	/**
	 * Fonction permettant de tester si le graphe est connexe.
	 */
	public boolean isConnected() {
		for (boolean[] t : royWarshall()) {
			for (boolean b : t) {
				if (!b) {
					return false;
				}
			}
		}
		return true;
	}
	
	// COMMANDES
	
	/**
	 * Ajoute une arête entre i et j.
	 */
	public void addEdge(int i, int j) {
		Contract.checkCondition(validIndexs(i, j));
		
		accessMatrix[i][j] = true;
	}
	
	/**
	 * Retire une arête entre i et j.
	 */
	public void removeEdge(int i, int j) {
		Contract.checkCondition(validIndexs(i, j));
		
		accessMatrix[i][j] = false;
	}
	
	/**
	 * Remet à zero le graphe.
	 */
	public void clear() {
		for (int i = 0; i < nbElem; ++i) {
			for (int j = 0; j < nbElem; ++j) {
				accessMatrix[i][j] = false;
			}
	    }
	}
	
	
	
	// OUTILS
	
	/**
	 * Vérifie la validité des indexs i et j.
	 */
	private boolean validIndexs(int i, int j) {
		return 0 <= i && i < nbElem && 0 <= j && j < nbElem;
	}
	
	/**
	 * Calcule la matrice d'accessibilité du graphe en utilisant l'algorithme
	 * de Roy-Warshall.
	 */
	private boolean[][] royWarshall() {
		boolean[][] ac = new boolean[nbElem][nbElem];
		for (int i = 0; i < nbElem; i++) {
			ac[i][i] = true;
			for (int j = 0; j < nbElem; j++) {
				ac[i][j] = accessMatrix[i][j];
			}
		}
		for (int k = 0; k < nbElem; ++k) {
			for (int i = 0; i < nbElem; ++i) {
				for (int j = 0; j < nbElem; ++j) {
					ac[i][j] = ac[i][j] || (ac[i][k] && ac[k][j]);
				}
		    }
		}
		return ac;
	}
}
