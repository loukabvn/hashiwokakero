package graphic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

import model.Coord;
import model.Island;
import util.Contract;

@SuppressWarnings("serial")
/**
 * Composant graphique permettant de dessiner des îles.
 */
public class GraphicIsland extends JComponent {
	
	// CONSTANTES
	
	private static final int RADIUS = 50;
	private static final Color LIGHT_BLUE = new Color(102, 178, 255);
	
	// ATTRIBUTS
	
	private Island island;
	private Coord coord;
	
	// CONSTRUCTEURS
	
	public GraphicIsland(Island i, Coord c) {
		Contract.checkCondition(i != null && c != null);
		
		island = i;
		coord = c;
		setPreferredSize(new Dimension(RADIUS, RADIUS));
	}
	
	// REQUETES
	
	public Island getIsland() {
		return island;
	}
	
	public Coord getCoord() {
		return coord;
	}
	
	// COMMANDES
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawCircle(g);
		drawNumber(g);
	}
	
	// OUTILS
	
	/**
	 * Dessine un cercle de diamètre RADIUS et le rempli de la couleur
	 * LIGHT_GRAY à la position coord.
	 */
	private void drawCircle(Graphics g) {
		g.setColor(LIGHT_BLUE);
		g.fillOval(coord.y * GraphicGrid.CELL_SIZE,
				coord.x * GraphicGrid.CELL_SIZE, RADIUS, RADIUS);
		g.setColor(Color.BLACK);
		g.drawOval(coord.y * GraphicGrid.CELL_SIZE,
				coord.x * GraphicGrid.CELL_SIZE, RADIUS, RADIUS);
	}
	
	/**
	 * Dessine le nombre de ponts de l'île.
	 */
	private void drawNumber(Graphics g) {
		g.setColor(Color.BLACK);
		String nb = String.valueOf(island.bridgesNb());
		FontMetrics fm = g.getFontMetrics();
		int nbWidth = fm.stringWidth(nb);
		int offset_y = GraphicGrid.CELL_SIZE / 2 - nbWidth / 2;
		int offset_x = GraphicGrid.CELL_SIZE / 2 + nbWidth / 2;
		g.drawString(nb, coord.y * GraphicGrid.CELL_SIZE + offset_y,
				coord.x * GraphicGrid.CELL_SIZE + offset_x);
	}
}
