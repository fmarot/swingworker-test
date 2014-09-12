package com.teamtter;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NonBlockingEDTButWaitEndSwingWorker extends SwingWorker<Void, String> {

	private JTextArea textArea;
	public static String newLine = System.getProperty("line.separator");
	private int	id;
	private BlockUnblockGUI	blockUnblockGUI;

	public NonBlockingEDTButWaitEndSwingWorker(int id, JTextArea textArea, final BlockUnblockGUI blockUnblockGUI) {
		this.id = id;
		this.textArea = textArea;
		this.blockUnblockGUI = blockUnblockGUI;
		if (SwingUtilities.isEventDispatchThread()) {
			blockUnblockGUI.blockGUI();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					blockUnblockGUI.blockGUI();
				}
			});
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		Thread.currentThread().setName(this.getClass().getSimpleName() + "-id");
		int i = 0;
		while (i < 10) {
			publish("working");
			i++;
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error("Thread.sleep exception...");
			}
		}
		return null;
	}

	@Override
	protected void process(List<String> strings) {
		for (String currString : strings) {
			textArea.append(newLine + id + " " + this.getClass().getSimpleName() + " " + currString );
		}
	}

	@Override
	protected void done() {
		/** RUNS IN THE EDT **/
		blockUnblockGUI.unblockGUI();
		super.done();
	}
	
	

}
