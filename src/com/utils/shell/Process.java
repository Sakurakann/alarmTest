package com.utils.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class Process extends TimerTask {
	private static Logger logger = Logger.getLogger(Process.class
			.getCanonicalName());
	private Timer checker;
	private String name;
	private String command;
	private java.lang.Process process;

	private static String toString(List<String> args) {
		StringBuilder builder = new StringBuilder();
		for (String s : args) {
			builder.append(s);
			builder.append(" ");
		}
		return builder.toString().trim();
	}

	public void run() {
		if (isExit())
			startup();
	}

	private void merge(final InputStream stream) {

		Thread t = new Thread(new Runnable() {
			public void run() {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(stream));
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(Process.this.name + "> " + line);
					}
				} catch (IOException e) {
					e.printStackTrace();
					try {
						if (reader != null)
							reader.close();
					} catch (Exception localException) {
					}
				} finally {
					try {
						if (reader != null)
							reader.close();
					} catch (Exception localException1) {
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	private void startup() {
		try {
			logger.info("startup " + this.name);
			this.process = Runtime.getRuntime().exec(this.command);
			merge(this.process.getInputStream());
			merge(this.process.getErrorStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isExit() {
		if (this.process == null)
			return true;
		try {
			this.process.exitValue();
			return true;
		} catch (IllegalThreadStateException localIllegalThreadStateException) {
		}
		return false;
	}

	public void close() {
		if (this.process != null) {
			logger.info("destroy " + this.name);
			this.process.destroy();
			this.process = null;
		}
		if (this.checker != null) {
			this.checker.cancel();
			this.checker = null;
		}
	}

	public Process(String name, long period, List<String> args) {
		this(name, period, toString(args));
	}

	public Process(String name, long period, String command) {
		this.name = name;
		this.command = command;
		this.checker = new Timer();
		this.checker.schedule(this, 1000L, period);
	}
}