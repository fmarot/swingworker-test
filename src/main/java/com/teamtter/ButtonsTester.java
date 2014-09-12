package com.teamtter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ButtonsTester {

	private int maxIdWorkers = 0;
	private JTextArea textArea;
	private List<Component> buttons = new ArrayList<Component>();

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
		f.setSize(1000, 600);
		f.setVisible(true);
	}

	private Component buildButtonsPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.CYAN);

		Component button = buildBasicSwingWorkerButton();
		buttons.add(button);
		panel.add(button);
		button = buildBlockingEDTWithCountDownLatchSwingWorkerButton();
		buttons.add(button);
		panel.add(button);
		button = buildBlockingEDTSwingWorkerButton();
		buttons.add(button);
		panel.add(button);
		button = buildNonBlockingEDTButWaitEndSwingWorkerButton();
		buttons.add(button);
		panel.add(button);
		return panel;
	}

	private Component buildBasicSwingWorkerButton() {
		JButton button = new JButton("Basic");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new BasicSwingWorker(maxIdWorkers++, textArea).execute();
			}
		});
		return button;
	}
	
	private Component buildBlockingEDTWithCountDownLatchSwingWorkerButton() {
		JButton button = new JButton("BlockingEDTWithCountDownLatch");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				BlockingEDTWithCountDownLatchSwingWorker blockingWorker = new BlockingEDTWithCountDownLatchSwingWorker(maxIdWorkers++, textArea);
				CountDownLatch latch = blockingWorker.getLatch();
				blockingWorker.execute();
				try {
					textArea.append("\n" + "Will await BlockingEDTSwingWorker...");
					latch.await();
				} catch (InterruptedException e) {
					textArea.append("\n" + "Exception in await BlockingEDTSwingWorker");
					log.error("", e);
				}
			}
		});
		return button;
	}
	
	private Component buildBlockingEDTSwingWorkerButton() {
		JButton button = new JButton("BlockingEDT");
		button.addActionListener(new ActionListener() {

			@SneakyThrows
			@Override
			public void actionPerformed(ActionEvent event) {
				BlockingEDTSwingWorker blockingWorker = new BlockingEDTSwingWorker(maxIdWorkers++, textArea);
				blockingWorker.execute();
				textArea.append("\n" + "Will await BlockingEDTSwingWorker...");
				blockingWorker.get();
			}
		});
		return button;
	}
	
	/** We build a process that will not block the EDT but will force the user to wait until task is done */
	private Component buildNonBlockingEDTButWaitEndSwingWorkerButton() {
		JButton button = new JButton("NonBlockingEDTButWaitEnd");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				
				BlockUnblockGUI blockUnblockGUI = new BlockUnblockGUI() {

					@Override
					public void blockGUI() {
						for (Component comp : buttons) {
							comp.setEnabled(false);
						}
					}

					@Override
					public void unblockGUI() {
						for (Component comp : buttons) {
							comp.setEnabled(true);
						}
					}
					
				};
				
				NonBlockingEDTButWaitEndSwingWorker worker = new NonBlockingEDTButWaitEndSwingWorker(maxIdWorkers++, textArea, blockUnblockGUI);
				textArea.append("\n" + "Will await NonBlockingEDTButWaitEnd...");
				worker.execute();
			}
		});
		return button;
	}

}