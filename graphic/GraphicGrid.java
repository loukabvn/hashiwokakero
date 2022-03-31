package graphic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import model.Bridge;
import model.Coord;
import model.HashiGrid;
import model.Island;
import util.Contract;

@SuppressWarnings({ "serial" })
/**
 * Composant graphique permettant des dessiner des grilles de Hashiwokakero.
 */
public class GraphicGrid extends JComponent {
	
	// CONSTANTES
	
	public static final int CELL_SIZE = 50;
	
	// ATTRIBUTS
	
	private HashiGrid model;
	private List<GraphicIsland> islands;
	private List<GraphicBridge> bridges;
	
	// CONSTRUCTEURS
	
	public GraphicGrid() {
		// rien ici
	}
	
	public GraphicGrid(HashiGrid grid) {
		Contract.checkCondition(grid != null);
		
		setAttributes(grid);
	}
	
	// REQUETES
	
	public HashiGrid getModel() {
		return model;
	}
	
	// COMMANDES
	
	public void changeGrid(HashiGrid grid) {
		Contract.checkCondition(grid != null);
		
		setAttributes(grid);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if (model != null) {
			super.paintComponent(g);
			paintGrid(g);
			drawIslands(g);
			drawBridges(g);
		}
	}
	
	// OUTILS
	
	/**
	 * Initialise les attributs de la grille avec le modèle grid.
	 */
	private void setAttributes(HashiGrid grid) {
		model = grid;
		islands = createGraphicIslands(grid);
		bridges = createGraphicBridges(grid);
		int size = model.getSize() * CELL_SIZE + 1;
		setPreferredSize(new Dimension(size, size));
	}
	
	/**
	 * Crée les GraphicIsland associés aux îles du modèle.
	 */
	private List<GraphicIsland> createGraphicIslands(HashiGrid model) {
		List<GraphicIsland> l = new ArrayList<GraphicIsland>();
		for (int x = 0; x < model.getSize(); x++) {
			for (int y = 0; y < model.getSize(); y++) {
				Coord c = new Coord(x, y);
				Island i = model.getIslandAt(c);
				if (i != null) {
					l.add(new GraphicIsland(i, c));
				}
			}
		}
		return l;
	}
	
	/**
	 * Crée les GraphicBridge associés aux ponts du modèle.
	 */
	private List<GraphicBridge> createGraphicBridges(HashiGrid model) {
		List<GraphicBridge> l = new ArrayList<GraphicBridge>();
		for (int x = 0; x < model.getSize(); x++) {
			for (int y = 0; y < model.getSize(); y++) {
				Coord c = new Coord(x, y);
				Bridge b = model.getBridgeAt(c);
				if (b != null) {
					l.add(new GraphicBridge(b, c));
				}
			}
		}
		return l;
	}
	
	/**
	 * Dessine le grille de fond.
	 */
	private void paintGrid(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getHeight(), getWidth());
		g.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i <= model.getSize(); i++) {
			g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, getWidth());
			g.drawLine(0, i * CELL_SIZE, getHeight(), i * CELL_SIZE);
		}
	}
	
	/**
	 * Dessine les îles du modèle.
	 */
	private void drawIslands(Graphics g) {
		for (GraphicIsland i : islands) {
			i.paintComponent(g);
		}
	}
	
	/**
	 * Dessine les ponts du modèle.
	 */
	private void drawBridges(Graphics g) {
		bridges = createGraphicBridges(model);
		for (GraphicBridge b : bridges) {
			b.paintComponent(g);
		}
	}
}
