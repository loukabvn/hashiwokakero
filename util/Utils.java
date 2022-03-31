package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class Utils {
	
	// FONCTIONS PUBLIQUES ET STATIQUES
	
	/**
	 * Retire tous les caractères qui ne sont pas des chiffres dans la chaine
	 * de caractères line.
	 */
	private static String removeNonDigit(String line) {
		Contract.checkCondition(line != null);
		
		StringBuffer b = new StringBuffer();
		char[] array = line.toCharArray();
		for (char c : array) {
			if (Character.isDigit(c)) {
				b.append(c);
			}
		}
		return new String(b);
	}
	
	/**
	 * Charge une grille d'Hashiwokakero depuis le fichier filename et renvoie
	 * le resultat sous forme de tableau d'entiers. Lève une IOException en cas
	 * d'erreur durant l'ouverture du fichier, et une BadSyntaxException si une
	 * ligne du fichier est syntaxiquement incorrect.
	 */
	public static int[][] loadGridFrom(String filename)
			throws IOException, BadSyntaxException {
		Contract.checkCondition(filename != null, "The file path is incorrect");
		
		File f = new File(filename);
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line = removeNonDigit(r.readLine());
		int size = line.length();
		int[][] tab = new int[size][size];
		int x = 0;
		
		while (line != null && x < size) {
			line = removeNonDigit(line);
			if (line.length() != size) {
				r.close();
				throw new BadSyntaxException("Your file is incorrect");
			}
			char[] c = line.toCharArray();
			for (int y = 0; y < size; y++) {
				int n = Character.getNumericValue(c[y]);
				tab[x][y] = n;
			}
			x++;
			line = r.readLine();
		}
		r.close();
		return tab;
	}
	
	/**
	 * Sauvegarde le tableau d'île islands dans un fichier de nom filename.
	 * Si le tableau n'est pas valide lève une AssertionError, et s'il y a un
	 * problème d'écriture, lève une IOException.
	 */
	public static void createFileFrom(String filename, int[][] islands)
			throws IOException {
		Contract.checkCondition(isValidTab(islands), "Invalid grid");
		
		File f = new File(new File("puzzles/"), filename);
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		for (int[] line : islands) {
			w.write(intArrayToCharArray(line));
			w.newLine();
		}
		w.close();
	}
	
	/**
	 * Vérifie si islands et un tableau d'île valide.
	 */
	public static boolean isValidTab(int[][] islands) {
		for (int[] t : islands) {
			for (int i : t) {
				if (i < 0 || i > 8) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static char[] intArrayToCharArray(int[] array) {
		char[] r = new char[array.length];
		for (int i = 0; i < array.length; i++) {
			r[i] = (char) ('0' + array[i]);
		}
		return r;
	}
	
	// CONSTRUCTEUR (PRIVES, NON INSTANCIABLE)

	private Utils() {
		// rien ici
	}
}
