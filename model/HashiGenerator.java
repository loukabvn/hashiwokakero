package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.Contract;

public class HashiGenerator {
	
	// CONSTANTES
	
	public static final List<Integer> AVAILABLE_SIZES;
	private static final Map<Integer, Integer> RANGE_MIN;
	private static final Map<Integer, Integer> RANGE_MAX;
	static {
		AVAILABLE_SIZES = new ArrayList<Integer>();
		RANGE_MIN = new HashMap<Integer, Integer>();
		RANGE_MAX = new HashMap<Integer, Integer>();
	
		AVAILABLE_SIZES.add(7);
		AVAILABLE_SIZES.add(10);
		AVAILABLE_SIZES.add(13);
		RANGE_MIN.put(7, 9);
		RANGE_MAX.put(7, 14);
		RANGE_MIN.put(10, 16);
		RANGE_MAX.put(10, 28);
		RANGE_MIN.put(13, 25);
		RANGE_MAX.put(13, 40);
	}
	
	private static final int UNKNOWN_ISLAND = -1;
	private static final int SIMPLE = 1;
	private static final int DOUBLE = 2;
	
	// ATTRIBUTS
	
	private final Random random;
	private int islandsNb;
	private int size;
	private int[][] islands;
	private Bridge[][] bridges;
	
	private HashiGrid lastGeneratedGrid;
	private HashiGrid lastGeneratedSolution;
	
	// CONSTRUCTEURS
	
	public HashiGenerator() {
		random = new Random(new Date().getTime());
		islandsNb = 0;
	}
	
	public HashiGenerator(int size) {
		this();
		initAttributes(size);
	}
	
	// REQUETES
	
	public int getSize() {
		return size;
	}
	
	public int[][] getIslands() {
		return islands.clone();
	}
	
	public HashiGrid getLastGridSolution() {
		return lastGeneratedSolution;
	}
	
	public HashiGrid getLastGeneratedGrid() {
		return lastGeneratedGrid;
	}
	
	// COMMANDES
	
	public void setSize(int size) {
		initAttributes(size);
	}
	
	public void generateRandomGrid() {
		if (size == 0) {
			return;
		}
		islandsNb = 0;
		int wantedIslandsNb = computeNbIslands();
		createRandomIsland();
		int i = 0;
		int maxIter = 1000;
		// La variable maxIter permet d'empêcher la fonction de boucler
		// indéfiniment. En général moins de 100 itérations suffisent pour
		// construire la grille, mais dans certains cas, si on ne peut plus
		// placer d'îles, la fonction bouclerait indéfiniment sans cette
		// variable.
		while (islandsNb <= wantedIslandsNb && i < maxIter) {
			buildRandomBridgeFrom(chooseRandomNode());
			i++;
		}
		computeIslandsValue();
		lastGeneratedSolution = new HashiGrid(islands, bridges);
		lastGeneratedGrid = new HashiGrid(islands, null);
	}
	
	// OUTILS
	
	private void initAttributes(int size) {
		Contract.checkCondition(AVAILABLE_SIZES.contains(size));
		
		this.size = size;		
		islands = new int[size][size];
		bridges = new Bridge[size][size];
	}
	
	private int computeNbIslands() {
		int min = RANGE_MIN.get(size);
		int max = RANGE_MAX.get(size);
		return min + random.nextInt(max - min);
	}
	
	private void createRandomIsland() {
		int x = random.nextInt(size);
		int y = random.nextInt(size);
		islands[x][y] = UNKNOWN_ISLAND;
		islandsNb++;
	}
	
	private Coord chooseRandomNode() {
		List<Coord> availableCoords = new ArrayList<Coord>();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (islands[x][y] != 0) {
					availableCoords.add(new Coord(x, y));
				}
			}
		}
		return availableCoords.get(random.nextInt(availableCoords.size()));
	}
	
	private void buildRandomBridgeFrom(Coord c) {
		Direction[] dirs = Direction.values();
		Direction randDir = dirs[random.nextInt(dirs.length)];
		int x = c.x + randDir.incX();
		int y = c.y + randDir.incY();
		if (!isValidCoord(x, y) || !isValidBridge(randDir, new Coord(x, y))
				|| islands[x][y] != 0 || bridges[x][y] != null) {
			return;
		}
		int bridgeLenght = 0;
		int bridgeType = headOrTail() ? SIMPLE : DOUBLE;
		while (isValidBridge(randDir, new Coord(x, y))) {
			bridges[x][y] = new Bridge(randDir, bridgeType);
			bridgeLenght++;
			x += randDir.incX();
			y += randDir.incY();
			if (islands[x][y] != 0) {
				return;
			}
			if (bridges[x][y] != null) {	
				if (!canPutIsland(new Coord(x, y), randDir)) {
					int prevX = x - randDir.incX();
					int prevY = y - randDir.incY();
					bridges[prevX][prevY] = null;
					if (bridgeLenght >= 2) {
						islands[prevX][prevY] = UNKNOWN_ISLAND;
						islandsNb++;
					}
					return;
				}
				bridges[x][y] = null;
				islands[x][y] = UNKNOWN_ISLAND;
				islandsNb++;
				return;
			}
			if (headOrTail()) {
				break;
			}
		}
		islands[x][y] = UNKNOWN_ISLAND;
		islandsNb++;
	}
	
	private boolean isValidCoord(int x, int y) {
		return x >= 0 && x < size && y >= 0 && y < size;
	}
	
	private boolean isValidBridge(Direction d, Coord c) {
		if (d.isVertical()) {
			return c.x > 0 && c.x < size - 1;
		} else {
			return c.y > 0 && c.y < size - 1;
		}
	}
	
	private boolean headOrTail() {
		return Math.random() >= 0.5;
	}
	
	private boolean canPutIsland(Coord c, Direction dir) {
		Direction n = Direction.NORTH;
		Direction s = Direction.SOUTH;
		Direction e = Direction.EAST;
		Direction w = Direction.WEST;
		if (dir.isHorizontal()) {
			if (islands[c.x + n.incX()][c.y + n.incY()] != 0
					|| islands[c.x + s.incX()][c.y + s.incY()] != 0) {
				return false;
			}
		} else if (islands[c.x + e.incX()][c.y + e.incY()] != 0
					|| islands[c.x + w.incX()][c.y + w.incY()] != 0) {
			return false;
		}
		return true;
	}
	
	private void computeIslandsValue() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (islands[x][y] == UNKNOWN_ISLAND) {
					islands[x][y] = nearBridgesNb(new Coord(x, y));
				}
			}
		}
	}
	
	private int nearBridgesNb(Coord c) {
		int bridgesNb = 0;
		Bridge b;
		for (Direction d : Direction.values()) {
			int x = c.x + d.incX();
			int y = c.y + d.incY();
			if (isValidCoord(x, y) && (b = bridges[x][y]) != null) {
				if (b.getDir() == d || b.getDir() == d.opposite()) {
					bridgesNb += b.nbBridges();
				}
			}
		}
		return bridgesNb;
	}
}
