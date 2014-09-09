package com.teamtter;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicSwingWorker extends SwingWorker<Void, String> {

	private JTextArea textArea;
	public static String newLine = System.getProperty("line.separator");

	public BasicSwingWorker(JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	protected Void doInBackground() throws Exception {
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
			textArea.append(newLine + this.getClass().getSimpleName() + " " + currString + " " + hashCode());
		}
	}
}
