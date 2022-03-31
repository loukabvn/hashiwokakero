package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.BadSyntaxException;
import util.Contract;
import util.Utils;

public class HashiGrid {
	
	// ATTRIBUTS
	
	private int size;
	private int nbIslands;
	private Island[][] grid;
	private Bridge[][] bridges;
	private boolean[][] isEvaluated;
	private Map<Island, Integer> map;
	private Graph graph;
	
	// CONSTRUCTEURS
	
	/**
	 * Crée une grille à partir d'un fichier source file.
	 */
	public HashiGrid(String file) throws IOException, BadSyntaxException {
		this(Utils.loadGridFrom(file), null);	
	}
	
	/**
	 * Crée une grille à partir d'un tableau d'entier, les îles (islands), et
	 * un tableau de ponts (bridges). Si bridges == null, une grille sans pont
	 * est crée.
	 */
	public HashiGrid(int [][] islands, Bridge[][] bridges) {
		Contract.checkCondition(islands != null && Utils.isValidTab(islands));
		
		initAttributes(islands, bridges);
	}
	
	// REQUETES
	
	public int getSize() {
		return size;
	}
	
	public int nbIslands() {
		return nbIslands;
	}
	
	/**
	 * Teste si la coordonnée c à déjà été évalué.
	 */
	public boolean isEvaluated(Coord c) {
		Contract.checkCondition(isValidCoord(c));
		
		return isEvaluated[c.x][c.y];
	}
	
	public boolean isBridge(Coord c) {
		Contract.checkCondition(isValidCoord(c));
		
		return bridges[c.x][c.y] != null;
	}
	
	/**
	 * Teste si c et d sont des coordonnées voisines dans la grilles, c'est à
	 * dire de même ligne ou de même colonne.
	 */
	public boolean areNeighbors(Coord c, Coord d) {
		Contract.checkCondition(isValidCoord(c) && isValidCoord(d));
		
		return c.x == d.x || c.y == d.y;
	}
	
	/**
	 * Teste s'il on peut construire un pont entre c et d, dans la direction dir
	 */
	public boolean canBuildBridge(Coord c, Direction dir, Coord d) {
		Contract.checkCondition(isValidCoord(c) && isValidCoord(d)
				&& dir != null);
		
		if (!areNeighbors(c, d)) {
			return false;
		}
		if (getIslandAt(c).bridgesPlaced(dir) == 1) {
			return getIslandAt(c).canBuildBridge(dir, getIslandAt(d));
		}
		for (Coord e : searchInternalCoords(c, dir, d)) {
			if (bridges[e.x][e.y] != null) {
				return false;
			}
		}
		return getIslandAt(c).canBuildBridge(dir, getIslandAt(d));
	}
	
	/**
	 * Teste s'il on peut retirer un pont entre c et d, dans la direction dir
	 */
	public boolean canRemoveBridge(Coord c, Direction dir, Coord d) {
		Contract.checkCondition(isValidCoord(c) && isValidCoord(d)
				&& dir != null);
		
		return getIslandAt(c).isNeighbor(dir, getIslandAt(d));
	}
	
	/**
	 * Teste si la grille est valide, c'est à dire chaque île possède le nombre
	 * de ponts demandés et les îles sont toutes connectées
	 */
	public boolean isValidGrid() {
		return validNbOfBridges() && graph.isConnected();
	}
	
	/**
	 * Teste si tous les ponts sont construits et toutes les îles ne sont pas
	 * connectées
	 */
	public boolean notConnectedGrid() {
		return validNbOfBridges() && !graph.isConnected();
	}

	public Island getIslandAt(Coord c) {
		Contract.checkCondition(isValidCoord(c));
		
		return grid[c.x][c.y];
	}
	
	public Bridge getBridgeAt(Coord c) {
		Contract.checkCondition(isValidCoord(c));
		
		return bridges[c.x][c.y];
	}
	
	/**
	 * Trouve un voisin dans la direction d, depuis la coordonnée c.
	 * Si aucun voisin n'est trouvé ou si un pont est rencontré et qu'il ne
	 * s'agit pas d'un pont déjà présent entre c et le voisin trouvé dans cette
	 * direction, renvoie null. Renvoie la coordonnée du voisin sinon.
	 */
	public Coord findNeighborFrom(Coord c, Direction d) {
		Contract.checkCondition(isValidCoord(c) && d != null);
		
		boolean isBridge = false;
		int x = c.x + d.incX();
		int y = c.y + d.incY();
		while (0 <= x && x < size && 0 <= y && y < size) {
			if (bridges[x][y] != null) {
				isBridge = true;
			}
			if (grid[x][y] != null) {
				if (isBridge && getIslandAt(c).bridgesPlaced(d) == 0) {
					return null;
				}
				return new Coord(x, y);
			}
			x += d.incX();
			y += d.incY();
		}
		return null;
	}
	
	/**
	 * Retourne le nombre de voisins accessibles depuis une île de coordonnée c
	 */
	public int accessibleNeighborsNb(Coord c) {
		int n = 0;
		for (Direction d : Direction.values()) {
			Coord neighbor;
			if ((neighbor = findNeighborFrom(c, d)) != null
					&& !getIslandAt(neighbor).isComplete()) {
				n++;
			}
		}
		return n;
	}
	
	/**
	 * Trouve la première île dans la grille en la parcourant de gauche à droite
	 * et de haut en bas.
	 */
	public Coord findFirstIsland() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (grid[x][y] != null) {
					return new Coord(x, y);
				}
			}
		}
		return null;
	}
	
	/**
	 * Trouve l'île suivante dans la grille en la parcourant de gauche à droite
	 * et de haut en bas à partir de la coordonée c.
	 */
	public Coord findNextIsland(Coord c) {
		int x = c.x;
		int y = c.y + 1;
		while (x < size) {
			while (y < size) {
				if (grid[x][y] != null) {
					return new Coord(x, y);
				}
				y++;
			}
			y = 0;
			x++;
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(
				"HashiGrid : " + size + "x" + size + " :\n");
		for (Island[] t : grid) {
			for (Island i : t) {
				if (i == null) {
					buf.append("0");
				} else {
					buf.append(i.bridgesNb());
				}
			}
			buf.append('\n');
		}
		return new String(buf);
	}
	
	// COMMANDES
	
	/**
	 * Modifie l'état d'évaluation de la coordonnée c à b.
	 */
	public void setEvaluated(boolean b, Coord c) {
		Contract.checkCondition(isValidCoord(c));
		
		isEvaluated[c.x][c.y] = b;
	}
	
	/**
	 * Construit un pont, si possible, entre c et d, dans la direction dir.
	 */
	public void buildBridge(Coord c, Direction dir, Coord d) {
		Contract.checkCondition(isValidCoord(c) && isValidCoord(d)
				&& d != null);
		
		if (areNeighbors(c, d) && canBuildBridge(c, dir, d)) {
			Island i = getIslandAt(c);
			Island j = getIslandAt(d);
			i.buildBridge(dir, j);
			addBridges(c, dir, d);
			graph.addEdge(map.get(i), map.get(j));
			graph.addEdge(map.get(j), map.get(i));
		}
	}
	
	/**
	 * Retire un pont, si possible, entre c et d, dans la direction dir.
	 */
	public void removeBridge(Coord c, Direction dir, Coord d) {
		Contract.checkCondition(isValidCoord(c) && isValidCoord(d)
				&& dir != null);
		
		if (areNeighbors(c, d) && canRemoveBridge(c, dir, d)) {
			Island i = getIslandAt(c);
			Island j = getIslandAt(d);
			i.removeBridge(dir, j);
			removeBridges(c, dir, d);
			if (i.bridgesPlaced(dir) == 0) {
				graph.removeEdge(map.get(i), map.get(j));
				graph.removeEdge(map.get(j), map.get(i));
			}
		}
	}
	
	public void putIslandAt(Island i, Coord c) {
		Contract.checkCondition(i != null && isValidCoord(c));
		
		grid[c.x][c.y] = i;
	}
	
	/**
	 * Remet à zéro la grille en retirant les ponts.
	 */
	public void clear() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				bridges[x][y] = null;
				isEvaluated[x][y] = false;
				if (grid[x][y] != null) {
					grid[x][y].clearBridges();
				}
			}
		}
		graph.clear();
	}

	// OUTILS
	
	/**
	 * Vérifie la validité de la coordonnée c.
	 */
	private boolean isValidCoord(Coord c) {
		return c != null && 0 <= c.x && c.x < size && 0 <= c.y && c.y <= size;
	}
	
	/**
	 * Vérifie si le nombre de ponts placés sur chaque île et le nombre de ponts
	 * attendues.
	 */
	private boolean validNbOfBridges() {
		for (Island[] tab : grid) {
			for (Island i : tab) {
				if (i != null && i.bridgesNb() != i.allBridgesPlaced()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Initialise les attributs de l'objet
	 */
	private void initAttributes(int[][] islands, Bridge[][] bridges) {
		this.size = islands[0].length;
		this.nbIslands = 0;
		this.grid = new Island[size][size];
		this.isEvaluated = new boolean[size][size];
		this.bridges = bridges == null ? new Bridge[size][size] : bridges;
		this.map = new HashMap<Island, Integer>();
		int index = 0;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (islands[x][y] > 0) {
					grid[x][y] = new Island(islands[x][y]);
					map.put(grid[x][y], index);
					index++;
					nbIslands++;
				}
			}
		}
		this.graph = new Graph(nbIslands);
	}
	
	/**
	 * Retire un pont entre c et d, dans la direction dir.
	 */
	private void removeBridges(Coord c, Direction dir, Coord d) {
		for (Coord e : searchInternalCoords(c, dir, d)) {
			if (bridges[e.x][e.y].nbBridges() == 2) {
				bridges[e.x][e.y].removeBridge();
			} else {
				bridges[e.x][e.y] = null;
			}
		}
	}
	
	/**
	 * Ajoute un pont entre c et d, dans la direction dir.
	 */
	private void addBridges(Coord c, Direction dir, Coord d) {
		for (Coord e : searchInternalCoords(c, dir, d)) {
			if (bridges[e.x][e.y] != null) {
				bridges[e.x][e.y].addBridge();
			} else {
				bridges[e.x][e.y] = new Bridge(dir, 1);
			}
		}
	}
	
	/**
	 * Retourne la liste des coordonnées comprises entre c et d, exclus, dans
	 * la direction dir.
	 */
	private List<Coord> searchInternalCoords(Coord c, Direction dir, Coord d) {
		List<Coord> l = new ArrayList<Coord>();
		if (dir.isVertical()) {
			for (int x = c.x + dir.incX(); x != d.x; x += dir.incX()) {
				l.add(new Coord(x, c.y));
			}
		} else {
			for (int y = c.y + dir.incY(); y != d.y; y += dir.incY()) {
				l.add(new Coord(c.x, y));
			}
		}
		return l;
	}
}
