package graphic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import model.Bridge;
import model.Coord;
import util.Contract;

@SuppressWarnings("serial")
/**
 * Composant graphique permettant de dessiner des ponts.
 */
public class GraphicBridge extends JComponent {
	
	// CONSTANTES
	
	private static final int BRIDGE_THICK = 4;
	private static final int BRIDGE_LENGTH = 50;
	private static final int OFFSET = 6;
	
	// ATTRIBUTS
	
	private Bridge bridge;
	private Coord coord;
	
	// CONSTRUCTEURS
	
	public GraphicBridge(Bridge b, Coord c) {
		Contract.checkCondition(b != null && c != null);
		
		bridge = b;
		coord = c;
		setPreferredSize(new Dimension(BRIDGE_LENGTH, BRIDGE_LENGTH));
	}
	
	// REQUETES
	
	public Bridge getBridge() {
		return bridge;
	}
	
	public Coord getCoord() {
		return coord;
	}
	
	// COMMANDES
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		if (bridge.nbBridges() == 2) {
			drawDoubleBridge(g);
		} else {
			drawSimpleBridge(g);
		}
	}
	
	// OUTILS
	
	/**
	 * Dessine un pont simple.
	 */
	private void drawSimpleBridge(Graphics g) {
		int size = GraphicGrid.CELL_SIZE;
		switch (bridge.getDir()) {
		case NORTH:
		case SOUTH:
			g.fillRect(coord.y * size + size / 2 - BRIDGE_THICK / 2,
					coord.x * size, BRIDGE_THICK, BRIDGE_LENGTH);
			break;
		case EAST:
		case WEST:
			g.fillRect(coord.y * size, coord.x * size + size / 2
					- BRIDGE_THICK / 2, BRIDGE_LENGTH, BRIDGE_THICK);
		}
	}
	
	/**
	 * Dessine un double pont.
	 */
	private void drawDoubleBridge(Graphics g) {
		int size = GraphicGrid.CELL_SIZE;
		switch (bridge.getDir()) {
		case NORTH:
		case SOUTH:
			g.fillRect(coord.y * size + size / 2 - OFFSET - BRIDGE_THICK / 2,
					coord.x * size, BRIDGE_THICK, BRIDGE_LENGTH);
			g.fillRect(coord.y * size + size / 2 + OFFSET - BRIDGE_THICK / 2,
					coord.x * size, BRIDGE_THICK, BRIDGE_LENGTH);
			break;
		case EAST:
		case WEST:
			g.fillRect(coord.y * size, coord.x * size + size / 2 - OFFSET
					- BRIDGE_THICK / 2, BRIDGE_LENGTH, BRIDGE_THICK);
			g.fillRect(coord.y * size, coord.x * size + size / 2 + OFFSET
					- BRIDGE_THICK / 2, BRIDGE_LENGTH, BRIDGE_THICK);
		}
	}
}
