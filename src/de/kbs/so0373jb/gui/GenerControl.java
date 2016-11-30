package de.kbs.so0373jb.gui;

import java.io.File;

public class GenerControl {

	private static final String	CONFPATH	= "z:/config/SO0373JB";
	private String				input	= null;
	
	public GenerControl () {
	}
	
	public String getInput() {
		if (input==null)  {
			File confpath		= new File(CONFPATH);
			confpath.mkdir		();
			if (confpath.isDirectory()) {
				File[] files		= confpath.listFiles();
				if (files.length==1) {
					return 			files[0].getAbsolutePath();
				}
			}
			input			= "";
		}
		return input;
	}

	public void setInput(String input) {
		this.input		= input;
	}
	
	public static void main(String[] args) {
		try	{
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
			}
		catch (Exception e)	{}
		new GenerView			(new GenerControl());
	}

}