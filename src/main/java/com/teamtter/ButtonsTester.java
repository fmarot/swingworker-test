package com.teamtter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ButtonsTester {

	private JTextArea textArea;

	public ButtonsTester() {
	}

	public static void main(String[] args) {
		final ButtonsTester buttonsTester = new ButtonsTester();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				buttonsTester.buildGUI();
			}
		});
	}

	protected void buildGUI() {
		JFrame f = new JFrame("Test Swingworkers");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		textArea = new JTextArea();
		JScrollPane scrollpane = new JScrollPane(textArea);
		f.getContentPane().add(scrollpane, BorderLayout.CENTER);

		f.getContentPane().add(buildButtonsPanel(), BorderLayout.NORTH);

		f.pack();
		f.setSize(600, 600);
		f.setVisible(true);
	}

	private Component buildButtonsPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.CYAN);

		panel.add(buildButton());
		panel.add(buildButton());

		return panel;
	}

	private Component buildButton() {
		JButton button = new JButton("clickme");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new BasicSwingWorker(textArea).execute();
			}
		});
		return button;
	}

}