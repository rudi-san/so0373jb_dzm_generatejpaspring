package de.kbs.so0373jb.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import de.kbs.so0373jb.main.Main;

public class GenerView extends JFrame {

	private static final 	long 			serialVersionUID = 1L;
	private					JTextArea 		input;
	private					JTextArea 		editArea;

	
	public GenerView (final GenerControl control) {
		
		Border blackline 			= BorderFactory.createLineBorder(Color.black);
		
		addWindowListener (new WindowAdapter (){
			public void windowClosing(java.awt.event.WindowEvent e){
				System.exit(0);
			}
		});

		JPanel panel				= new JPanel(new BorderLayout());
		JPanel sPanel				= new JPanel(new FlowLayout());
		
		input						= new JTextArea(control.getInput());
		input.setEditable			(false);
		input.setBorder				(BorderFactory.createTitledBorder(blackline,"Configurations-XML"));
		panel.add					(input,BorderLayout.NORTH);

		editArea					= new JTextArea();
		panel.add					(editArea, BorderLayout.CENTER);

		sPanel.add					(new JButton(new AbstractAction("Open/Create") {
			private static final long serialVersionUID = 1L;			
			@Override
			public void actionPerformed(ActionEvent e) {
				File newFile			= FileDialog.getFile(JFileChooser.OPEN_DIALOG);
				if (newFile!=null) {
					control.setInput	(newFile.getAbsolutePath());
					input.setText		(newFile.getAbsolutePath());
					try {
						if 	(!newFile.exists()) {
							Path templFile			= Paths.get("resources", "files", "SO0373JB.xml");
							Files.copy				(templFile, newFile.toPath());
						}
						editArea.read(new FileReader(newFile), null);
					} catch (IOException e1) {}
				}
			}
		}));
		sPanel.add					(new JButton(new AbstractAction("Save") {
			private static final long serialVersionUID = 1L;						
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName			= control.getInput();
				if (fileName!=null) {
					try {
						editArea.write(new FileWriter(fileName));
					} catch (IOException e1) {}
				}				
			}
		}));
		
		sPanel.add					(new JButton(new AbstractAction("Generierung") {
			private static final long serialVersionUID = 1L;			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Main.doGener(control.getInput());
				} catch (SQLException exc) {
					JOptionPane.showMessageDialog(null, exc.getMessage(), "JPA-Gener", JOptionPane.ERROR_MESSAGE);
				}
			}
		}));
		
		sPanel.add					(new JButton(new AbstractAction("Close") {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit		(0);
			}
		}));
		
		panel.add					(sPanel, BorderLayout.SOUTH);
		
		add							(panel);
		
		setSize						(600, 600);
	   	setLocationRelativeTo		(null);
		setVisible					(true);
	}
	
	public void setInput (String fileName) {
		input.setText				(fileName);
	}
}