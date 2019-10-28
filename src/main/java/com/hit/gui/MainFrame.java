package com.hit.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = -1273935236783273211L;
	
	public MainFrame(String appTitle) {
		super(appTitle);
		super.setLayout(new BorderLayout()); 
		
		JButton remove = new JButton("Remove");
		JButton save = new JButton("Save");
		JButton createNew = new JButton("Add new");
		
		
		JToolBar appToolbar = new JToolBar();
		appToolbar.add(createNew);
		appToolbar.add(save);
		appToolbar.add(remove);
		
		JPanel appPanel = new tablePanel();
		super.add(appToolbar, BorderLayout.NORTH);
		super.add(appPanel);
		
		createNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("createNew clicked!");
				((tablePanel) appPanel).addNewBook();
			}
		});
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((tablePanel) appPanel).saveSelected();
			}
		});
		
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					((tablePanel) appPanel).removeSelected();
				} catch (IOException | InterruptedException | ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	

}
