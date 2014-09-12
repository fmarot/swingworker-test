package com.teamtter;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockingEDTWithCountDownLatchSwingWorker extends SwingWorker<Void, String> {

	@Getter
	private CountDownLatch latch = new CountDownLatch(1);
	private JTextArea textArea;
	public static String newLine = System.getProperty("line.separator");
	private int	id;

	public BlockingEDTWithCountDownLatchSwingWorker(int id, JTextArea textArea) {
		this.id = id;
		this.textArea = textArea;
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
		latch.countDown();	// must be done outside the EDT because the caller is already waiting actively in the EDT
		return null;
	}

	@Override
	protected void process(List<String> strings) {
		for (String currString : strings) {
			textArea.append(newLine + id + " " + this.getClass().getSimpleName() + " " + currString );
		}
	}

}
