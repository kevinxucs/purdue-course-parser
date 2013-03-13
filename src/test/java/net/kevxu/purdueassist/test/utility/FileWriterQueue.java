package net.kevxu.purdueassist.test.utility;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileWriterQueue implements Runnable {

	private boolean started = false;
	private boolean stopped = false;

	private final BlockingQueue<FileWriterEntry> queue;

	// private final BufferedWriter writer;

	public class FileWriterEntry {

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

	private synchronized void start() {
		this.started = true;
		this.stopped = false;

		new Thread(this).start();
	}

	private synchronized void stop() {
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

	@Override
	public void run() {
		while (!isStopped()) {

		}
	}

}
