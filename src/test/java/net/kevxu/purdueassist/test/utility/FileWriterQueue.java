package net.kevxu.purdueassist.test.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class FileWriterQueue implements Runnable {

	private boolean started = false;
	private boolean stopped = false;

	private final BlockingQueue<FileWriterEntry> queue;

	public static class FileWriterEntry {

		private final String mPath;
		private final String mValue;

		public FileWriterEntry(String path, String value) {
			mPath = path;
			mValue = value;
		}

		public String getPath() {
			return mPath;
		}

		public String getValue() {
			return mValue;
		}

	}

	public FileWriterQueue() {
		queue = new LinkedBlockingQueue<FileWriterQueue.FileWriterEntry>();
	}

	private synchronized boolean isStarted() {
		return started;
	}

	private synchronized boolean isStopped() {
		return stopped;
	}

	public synchronized void start() {
		this.started = true;
		this.stopped = false;

		new Thread(this).start();
	}

	public synchronized void stop() {
		this.started = false;
		this.stopped = true;
	}

	public synchronized void append(FileWriterEntry entry) {
		if (!isStarted()) {
			throw new IllegalStateException("start() should be called before appending data.");
		} else if (isStopped()) {
			throw new IllegalStateException("Already stopped.");
		} else {
			queue.add(entry);
		}
	}

	public synchronized int queueSize() {
		if (!isStarted()) {
			throw new IllegalStateException("start() should be called before appending data.");
		} else if (isStopped()) {
			throw new IllegalStateException("Already stopped.");
		} else {
			return queue.size();
		}
	}

	@Override
	public void run() {
		while (!isStopped()) {
			try {
				FileWriterEntry entry = queue.poll(10, TimeUnit.MILLISECONDS);
				if (entry != null) {
					File file = new File(entry.getPath());
					if (!file.exists()) {
						File dir = new File(file.getParent());
						if (!dir.exists()) {
							dir.mkdirs();
						}
						file.createNewFile();
					}
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					writer.write(entry.getValue());
					writer.flush();
					writer.close();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("IOException: " + e.getMessage());
			}
		}
	}

}
