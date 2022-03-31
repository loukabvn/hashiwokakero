package graphic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.HashiSolver;
import util.Utils;
import model.HashiGenerator;
import model.HashiGrid;

@SuppressWarnings("deprecation")
public class Hashiwokakero {
	
	// CONSTANTES
	
	private static final String DESCRIPTION = "\n"
			+ "    There is two way to resolve the grids :\n\n"
			+ "        - by using rules of the game\n"
			+ "        - by using a back tracking\n"
			+ "          algorithm\n\n"
			+ "    If one of the algorithm doesn't\n"	
			+ "    succed to resolve the grid, you may\n"
			+ "    try the other.";
	private static final String GRIDS_DIR = "puzzles/";
	private static final String DEFAULT_GRID = "hashi-6x6";
	
	// Solveur
	private static final int LEFT_PANEL_WIDTH = 250;
	private static final int LEFT_PANEL_HEIGHT = 320;
	private static final int LABEL_HEIGHT = 20;
	
	// Générateur
	private static final int WIDTH_SUPP = 200;
	private static final int HEIGHT_SUPP = 20;
	
	private static final int PANE_WIDTH = 20;
	private static final int PANE_HEIGHT = 50;

	// ATTRIBUTS
	
	private JFrame frame;
	private JTabbedPane panes;
	
	// Solveur
	private JLabel gridName;
	private JLabel gridState;
	private JButton easyResolve;
	private JButton backTrackResolve;
	private JComboBox<String> files;
	private GraphicGrid grid;
	private HashiSolver model;
	private String filename;
	
	// Générateur
	private JButton newGrid;
	private JButton resolve;
	private JButton save;
	private GraphicGrid genGrid;
	private HashiGenerator generator;
	
	// CONSTRUCTEURS
	
	public Hashiwokakero() {
		createModel();
        createView();
        placeComponents();
        createController();
	}
	
	// COMMANDES
	
	public void display() {
		refresh();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	// OUTILS
	
	private void createModel() {
		filename = DEFAULT_GRID;
		try {
			model = new HashiSolver(new HashiGrid(GRIDS_DIR + filename));
		} catch (Exception e) {
			throw new Error("new HashiGrid(): " + e.getMessage());
		}
		generator = new HashiGenerator();
	}
	
	private void createView() {
		frame = new JFrame("Hashiwokakero");
		panes = new JTabbedPane();
		
		// Solveur
		gridName = new JLabel();
		gridState = new JLabel();
		gridState.setVisible(false);
		grid = new GraphicGrid(model.getGrid());
		easyResolve = new JButton("Easy solver");
		backTrackResolve = new JButton("Back-tracking solver");
		String[] puzzles = new File(GRIDS_DIR).list();
		Arrays.sort(puzzles);
		files = new JComboBox<String>(puzzles);
		files.setSelectedItem(DEFAULT_GRID);
		
		// Générateur
		newGrid = new JButton("New grid");
		resolve = new JButton("Resolve");
		save = new JButton("Save");
		genGrid = new GraphicGrid();
		resolve.setEnabled(false);
		save.setEnabled(false);
	}
	
	private void placeComponents() {
		// Solveur
		JPanel p = new JPanel(); {
			JPanel q = new JPanel(new BorderLayout()); {
				JPanel r = new JPanel(); {
					r.add(files);
				}
				r.setBorder(BorderFactory.createTitledBorder(
						"Choose your grid"));
				q.add(r, BorderLayout.NORTH);
				JTextArea desc = new JTextArea(DESCRIPTION);
				desc.setEditable(false);
				desc.setBorder(BorderFactory.createEtchedBorder());
				q.add(desc, BorderLayout.CENTER);
				r = new JPanel(new GridLayout(2, 1)); {
					r.add(easyResolve);
					r.add(backTrackResolve);
				}
				r.setBorder(BorderFactory.createEtchedBorder());
				q.add(r, BorderLayout.SOUTH);
			}
			q.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH,
					LEFT_PANEL_HEIGHT));
			p.add(q);
			q = new JPanel(new BorderLayout()); {
				JPanel r = new JPanel(); {
					r.add(gridName);
					r.add(gridState);
				}
				r.setBorder(BorderFactory.createEtchedBorder());
				q.add(r, BorderLayout.NORTH);
				q.add(grid, BorderLayout.SOUTH);
			}
			p.add(q);
		}
		panes.add("Solver", p);
		// Générateur
		p = new JPanel(new BorderLayout()); {
			JPanel q = new JPanel(new GridLayout(3, 1)); {
				JPanel r = new JPanel(new BorderLayout()); {
					r.add(newGrid, BorderLayout.CENTER);
				}
				r.setBorder(BorderFactory.createTitledBorder(
						"Generate a new grid :"));
				q.add(r);
				r = new JPanel(new BorderLayout()); {
					r.add(resolve, BorderLayout.CENTER);
				}
				r.setBorder(BorderFactory.createTitledBorder(
						"Resolve the grid :"));
				q.add(r);
				r = new JPanel(new BorderLayout()); {
					r.add(save, BorderLayout.CENTER);
				}
				r.setBorder(BorderFactory.createTitledBorder(
						"Save the grid in a file :"));
				q.add(r);
			}
			q.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH - 100,
					LEFT_PANEL_HEIGHT - 50));
			p.add(q, BorderLayout.WEST);
			q = new JPanel(); {
				q.add(genGrid, BorderLayout.CENTER);
			}
			q.setBorder(BorderFactory.createTitledBorder("Generated grid :"));
			p.add(q, BorderLayout.CENTER);
		}
		panes.add("Generator", p);
		//
		frame.add(panes);
	}
	
	private void createController() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Solveur
		
		((Observable) model).addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				refresh();
			}
		});
		
		files.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filename = files.getItemAt(files.getSelectedIndex());
				try {
					model.setGrid(new HashiGrid(GRIDS_DIR + filename));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				grid.changeGrid(model.getGrid());
				gridState.setVisible(false);
				int size = GraphicGrid.CELL_SIZE
						* (model.getGrid().getSize() + 1);
				frame.setSize(new Dimension(
						LEFT_PANEL_WIDTH + PANE_WIDTH + size,
						LABEL_HEIGHT + PANE_HEIGHT + size
				));
				refresh();
			}
		});
		
		easyResolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.tryEasySolver();
				gridState.setVisible(true);
			}
		});
		
		backTrackResolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.tryBackTrackSolver();
				gridState.setVisible(true);
			}
		});
		
		// Générateur
		
		newGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Integer size = (Integer) JOptionPane.showInputDialog(frame,
						"Select a grid size :", "Size selection",
						JOptionPane.DEFAULT_OPTION, null,
						HashiGenerator.AVAILABLE_SIZES.toArray(), 7);
				if (size == null) {
					return;
				}
				generator.setSize(size);
				generator.generateRandomGrid();
				genGrid.changeGrid(generator.getLastGeneratedGrid());
				resolve.setEnabled(true);
				save.setEnabled(true);
				generatorRefresh();
			}
		});
		
		resolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				genGrid.changeGrid(generator.getLastGridSolution());
				generatorRefresh();
			}
		});
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = JOptionPane.showInputDialog(
						"Choose a name for the file :");
				if (filename == null) {
					JOptionPane.showMessageDialog(frame,
							"You must choose a name for the file");
					return;
				}
				try {
					Utils.createFileFrom(filename, generator.getIslands());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame,
							"An error occured during file saving");
					e1.printStackTrace();
				}
				files.addItem(filename);
			}
		});
	}
	
	private void refresh() {
		gridName.setText(filename + " :");
		if (model.isSolved()) {
			gridState.setText("Success, grid resolved !");
		} else {
			gridState.setText("Resolve failure");
		}
		grid.repaint();
	}
	
	private void generatorRefresh() {
		int size = GraphicGrid.CELL_SIZE * (generator.getSize() + 1);
		frame.setSize(new Dimension(
				WIDTH_SUPP + PANE_WIDTH + size,
				HEIGHT_SUPP + PANE_HEIGHT + size
		));
		genGrid.repaint();
	}	
	
	// POINT D'ENTREE
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	try {
            		UIManager.setLookAndFeel(
            				"com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            	} catch (Exception e) {
            		System.out.println("Look and feel not available");
            	}
                new Hashiwokakero().display();
            }
        });
    }
}
