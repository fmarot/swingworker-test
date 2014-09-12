package com.teamtter.workers;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.teamtter.interfaces.IBlockUnblockGUI;
import com.teamtter.interfaces.IWhenWorkerFinished;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NonBlockingEDTButWaitEndSwingWorker extends SwingWorker<Void, String> {

	private JTextArea textArea;
	public static String newLine = System.getProperty("line.separator");
	private int	id;
	private IBlockUnblockGUI	blockUnblockGUI;
	private IWhenWorkerFinished	onfinish;

	public NonBlockingEDTButWaitEndSwingWorker(int id, JTextArea textArea, final IBlockUnblockGUI blockUnblockGUI, IWhenWorkerFinished onfinish) {
		this.id = id;
		this.textArea = textArea;
		this.blockUnblockGUI = blockUnblockGUI;
		this.onfinish = onfinish;
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
		while (i < 7) {	// wait 7 seconds
			publish("working");
			i++;
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error("Thread.sleep exception...");
			}
		}
		onfinish.afterWorkInBackgroundthread();
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
		/** By definition of a SwingWorker, done() RUNS IN THE EDT **/
		blockUnblockGUI.unblockGUI();
		onfinish.afterWorkInEDT();
		super.done();
	}
	
	

}
