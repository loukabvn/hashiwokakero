package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import util.Contract;

@SuppressWarnings("deprecation")
public class HashiSolver extends Observable {
	
	// ATTRIBUTS
	
	private HashiGrid game;
	private boolean isSolved;
	private boolean hasChange;
	private MyBridge lastBridge;
	private final List<MyBridge> invalidBridges;
	
	// CLASSES INTERNES
	
	// Classe interne pour simplifier l'utilisation des ponts
	private class MyBridge {
		
		// ATTRIBUTS
		
		private Coord island;
		private Direction direction;
		private Coord neighbor;
		
		// CONSTRUCTEURS
		
		private MyBridge(Coord c, Direction d, Coord n) {
			Contract.checkCondition(c != null && d != null && n != null);
			
			island = c;
			direction = d;
			neighbor = n;
		}
		
		// REQUETES
		
		private boolean canBuildBridge() {
			return game.canBuildBridge(island, direction, neighbor);
		}
		
		// COMMANDES
		
		private void buildBridge() {
			game.buildBridge(island, direction, neighbor);
		}
		
		private void removeBridge() {
			game.removeBridge(island, direction, neighbor);
		}
	}
	
	// CONSTRUCTEURS
	
	public HashiSolver(HashiGrid grid) {
		Contract.checkCondition(grid != null);
		
		game = grid;
		isSolved = false;
		hasChange = false;
		invalidBridges = new ArrayList<MyBridge>();
	}
	
	// REQUETES
	
	public HashiGrid getGrid() {
		return game;
	}
	
	public boolean isSolved() {
		return isSolved;
	}
	
	// COMMANDES
	
	/**
	 * Modifie la grille courante du solveur
	 */
	public void setGrid(HashiGrid grid) {
		Contract.checkCondition(grid != null);
		
		game = grid;
		isSolved = false;
		hasChange = false;
		invalidBridges.clear();
	}
	
	/**
	 * Lance le solveur basique
	 */
	public void tryEasySolver() {
		Coord start = game.findFirstIsland();
		if (!(isSolved = upgradeEasySolver(start))) {
			game.clear();
		}
		invalidBridges.clear();
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Lance le solveur par back tracking
	 */
	public void tryBackTrackSolver() {
		Coord start = game.findFirstIsland();
		if (!(isSolved = backTrack(start))) {
			game.clear();
		}
		setChanged();
		notifyObservers();
	}
	
	// OUTILS
	
//--- EASY-SOLVER --------------------------------------------------------------
 	
	/**
	 * Solveur de grilles simple utilisant les règles basiques du jeu
	 */
	private boolean easySolver(Coord start) {
		while (start != null) {
			buildEasyBridges(start);
			start = game.findNextIsland(start);
		}
		if (game.isValidGrid()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Construit les ponts pour le easySolver
	 */
	private void buildEasyBridges(Coord start) {
		hasChange = false;
		int value = game.getIslandAt(start).bridgesToPlace();
		int neighborNb = game.accessibleNeighborsNb(start);
		int bridgesToBuild = 0;
		if (value % 2 == 0 && value / 2 == neighborNb) {
			bridgesToBuild = 2;
		}
		if (value % 2 == 1 && (value + 1) / 2 == neighborNb) {
			bridgesToBuild = 1;
		}
		if (bridgesToBuild > 0) {
			for (Direction d : Direction.values()) {
				Coord neighbor = game.findNeighborFrom(start, d);
				if (neighbor != null && (value != 1
						|| game.getIslandAt(neighbor).bridgesNb() != 1)) {
					MyBridge b = new MyBridge(start, d, neighbor);
					if (b.canBuildBridge()) {
						hasChange = true;
						b.buildBridge();
						if (bridgesToBuild > 1) {
							b.buildBridge();
						}
					}
				}
			}
		}
	}
	
//--- EASY-SOLVER AMELIORE -----------------------------------------------------
	
	/**
	 * Amélioration du solveur de grilles utilisant uniquement les règles
	 * basiques du jeu
	 */
	private boolean upgradeEasySolver(Coord start) {
		if (game.notConnectedGrid()) {
			return false;
		}
		hasChange = true;
		while (hasChange) {
			if (easySolver(start)) {
				return true;
			}
		}
		if (createRandomBridge(start)) {
			MyBridge bridge = lastBridge;
			if (upgradeEasySolver(start)) {
				return true;
			}
			invalidBridges.add(bridge);
			bridge.removeBridge();
			if (upgradeEasySolver(start)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Construit le premier pont possible en partant de la coordonnée start. Si
	 * un pont a été construit de cette manière renvoie true, et false sinon.
	 */
	private boolean createRandomBridge(Coord start) {
		if (start == null) {
			return false;
		}
		for (Direction d : Direction.values()) {
			Coord neighbor = game.findNeighborFrom(start, d);
			if (neighbor != null) {
				MyBridge b = new MyBridge(start, d, neighbor);
				if (!invalidBridges.contains(b) && b.canBuildBridge()) {
					b.buildBridge();
					lastBridge = b;
					return true;
				}
			}
		}
		if (createRandomBridge(game.findNextIsland(start))) {
			return true;
		}
		return false;
	}
	
//--- BACK-TRACKING ------------------------------------------------------------

	/**
	 * Algorithme de back tracking utilisé pour résoudre la grille
	 */
	private boolean backTrack(Coord start) {
		if (game.isValidGrid()) {
			return true;
		}
		lock(start);
		for (int i = 0; i < 2; i++) {
			for (Direction d : Direction.values()) {
				Coord next = game.findNeighborFrom(start, d);
				if (next != null && !game.isEvaluated(next)) {
					MyBridge b = new MyBridge(start, d, next);
					b.buildBridge();
					if (backTrack(next)) {
						return true;
					}
					if (i > 0) {
						b.removeBridge();
					}
				}
			}
		}
		unlock(start);
		return false;
	}
	
	/**
	 * Bloque l'île de coordonnée c pour éviter une boucle infini dans
	 * l'algorithme de back tracking
	 */
	private void lock(Coord c) {
		game.setEvaluated(true, c);
	}
	
	/**
	 * Débloque l'île
	 */
	private void unlock(Coord c) {
		game.setEvaluated(false, c);
	}
}
