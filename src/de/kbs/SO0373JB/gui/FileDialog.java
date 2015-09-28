package de.kbs.SO0373JB.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/** Vereinfachte Ableitung der JFileChooser-Klasse. Es werden, neben Directories, nur 
 * Dateien mit einer bestimmten Extension angezeigt. Zurückgegeben werden die Dateinamen auch
 * immer nur mit der Extension, die vorgegeben wurde.
 * @author rschneid
 *
 */
public class FileDialog extends JFileChooser {

	private static final long serialVersionUID = 1L;
	String extension;
	
	
	/** Konstruktor. Es wird der Start-Path und die Extension gesetzt. Außerdem wird der FileFilter
	 * auf die Extension gesetzt.
	 * @param path Start-Path
	 * @param filter Auswahl-Extension
	 */
	public FileDialog(String path) {
		super(path);
		setFileFilter(new FileFilter() {
		    public boolean accept(File f) {
		        if (f.isDirectory())  return true;
				if (f.getName().toLowerCase().endsWith(".xml"))	
					return true; 
				return false;
		    }
		    public String getDescription() {
		        return "nur \".xml\"";
		    }		
		}); 
	}
	
	/** Aufruf des Save-Dialogs und Aufbereitung des gelieferten Dateinamens.
	 * @return Ausgewähltes File
	 */
	public static File getFile(int option) {
		try {
			Files.createDirectories(Paths.get("z:/", "config", "SO0373JB"));
		} catch (IOException e) {}
		FileDialog dialog		= new FileDialog("z:/config/SO0373JB");
		dialog.setDialogType	(option);
		int returnVal 			= dialog.showDialog(null, null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String name		= dialog.getSelectedFile().getAbsolutePath();
			name			= (name.toLowerCase().endsWith(".xml")) ? name : name+".xml";
			return 			new File (name);
		}
		return null;
	}
	
	/** main-Methode zum Testen.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(FileDialog.getFile(JFileChooser.SAVE_DIALOG));
		System.out.println(FileDialog.getFile(JFileChooser.OPEN_DIALOG));
	}
}