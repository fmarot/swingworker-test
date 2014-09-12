package com.teamtter.workers;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicSwingWorker extends SwingWorker<Void, String> {

	private JTextArea textArea;
	public static String newLine = System.getProperty("line.separator");
	private int	id;

	public BasicSwingWorker(int id, JTextArea textArea) {
		this.id = id;
		this.textArea = textArea;
	}

	@Override
	protected Void doInBackground() throws Exception {
		Thread.currentThread().setName(this.getClass().getSimpleName() + "-id");
		int i = 0;
		while (i < 7) {
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
}
