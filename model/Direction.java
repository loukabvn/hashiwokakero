package model;

import java.util.EnumMap;

/**
 * Types (enumération) des directions.
 * Chaque direction possède une direction opposé et les valeurs incX et incY.
 * incX (resp. incY) correspond à la valeur d'incrément de x, le numéro de la
 * ligne (resp. y, le numéro de la colonne) si l'on veut ce déplacer dans 
 * cette direction dans une grille.
 */
public enum Direction {
	NORTH(-1, 0),
	EAST(0, 1),
	SOUTH(1, 0),
	WEST(0, -1);
	
	private int incX;
	private int incY;
	private static final EnumMap<Direction, Direction> map;
	
	private Direction(int incX, int incY) {
		this.incX = incX;
		this.incY = incY;
	}
	
	static {
		map = new EnumMap<Direction, Direction>(Direction.class);
		map.put(NORTH, SOUTH);
		map.put(EAST, WEST);
		map.put(SOUTH, NORTH);
		map.put(WEST, EAST);
	}
	
	public int incX() {
		return incX;
	}
	
	public int incY() {
		return incY;
	}
	
	public boolean isVertical() {
		return this == NORTH || this == SOUTH;
	}
	
	public boolean isHorizontal() {
		return this == EAST || this == WEST;
	}
	
	public Direction opposite() {
		return map.get(this);
	}
}
