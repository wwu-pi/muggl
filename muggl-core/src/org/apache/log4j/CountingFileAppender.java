package org.apache.log4j;

import java.io.IOException;

import org.apache.log4j.spi.LoggingEvent;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.configuration.Options;

/**
 * This FileAppender extends the normal log4j appender for files. It will count the number
 * of events logged. If maximumEventsToLog is not set to -1, once the maximum value is
 * reached it will invoke a method provided by the Constants class to continue logging in
 * a new log file. It also offers the possibility to have logging stopped without further
 * attempts to log leading to exceptions. Furthermore, it overrides writeFooter() in order
 * to provide public access to it. This is needed to finish a log file before starting with
 * a new one.<br />
 * <br />
 * Last modified: 2010-03-10
 * 
 * @author Tim Majchrzak
 * @version 1.0.0
 */
public class CountingFileAppender extends FileAppender {
	// Fields.
	private boolean loggingStopped = false;
	private long eventsLogged = 0;
	private long maximumEventsToLog = -1;
	
	/**
	 * Constructor that just invoked the super constructor.
	 */
	public CountingFileAppender() {
		super();
	}

	/**
	 * Constructor that just invokes the super constructor.
	 * @param arg0 The first argument.
	 * @param arg1 The second argument.
	 * @param arg2 The third argument.
	 * @param arg3 The fourth argument.
	 * @param arg4 The fifth argument.
	 * @throws IOException On errors writing the data
	 * @see FileAppender#FileAppender(Layout, String, boolean, boolean, int)
	 */
	public CountingFileAppender(Layout arg0, String arg1, boolean arg2,
			boolean arg3, int arg4) throws IOException {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	/**
	 * Constructor that just invoked the super constructor.
	 * @param arg0 The first argument.
	 * @param arg1 The second argument.
	 * @param arg2 The third argument.
	 * @throws IOException On errors writing the data.
	 * @see FileAppender#FileAppender(Layout, String, boolean)
	 */
	public CountingFileAppender(Layout arg0, String arg1, boolean arg2) throws IOException  {
		super(arg0, arg1, arg2);
	}

	/**
	 * Constructor that just invoked the super constructor.
	 * @param arg0 The first argument.
	 * @param arg1 The second argument.
	 * @throws IOException On errors writing the data.
	 * @see FileAppender#FileAppender(Layout, String)
	 */
	public CountingFileAppender(Layout arg0, String arg1) throws IOException {
		super(arg0, arg1);
	}
	
	/**
	 * Append the current log file if logging has not been stopped. To do that
	 * the super implementation is called. Afterwards the number of logged
	 * events is increased by one. Finally it is checked whether the limit
	 * for entries has been reached and the creation of a new log file triggered
	 * if needed.
	 * 
	 * @param event A LoggingEvent.
	 */
	@Override
	public synchronized void doAppend(LoggingEvent event) {
		if (this.loggingStopped) return;
		super.doAppend(event);
		this.eventsLogged++;
		if (this.maximumEventsToLog != -1 && this.maximumEventsToLog <= this.eventsLogged)
			startANewLogfile();
	}
	
	/**
	 * Setter for the maximum number of events to log.
	 * @param maximumEventsToLog The maximum number of events to log.
	 */
	public void setMaximumEventsToLog(long maximumEventsToLog) {
		this.maximumEventsToLog = maximumEventsToLog;
	}
	
	/**
	 * Start a new log file. To do this, make enable further logging to this file
	 * (so the last entry can be written and reads will know logging is continued
	 * in another file), invoke Constants.getInst().continueWithNewLogfile(),
	 * reset the event counter and reset the current value for the maximum entries.
	 *
	 */
	private void startANewLogfile() {
		this.maximumEventsToLog = -1;
		Globals.getInst().continueWithNewLogfile(false);
		this.eventsLogged = 0;
		this.maximumEventsToLog = Options.getInst().maximumLogEntries;
	}
	
	/**
	 * Mark that no more logging will be performed.
	 */
	public void stopLogging() {
		this.loggingStopped = true;
		super.close();
	}
	
	/**
	 * Write the footer. Do to this, invoke the super implementation.
	 */
	@Override
	public void writeFooter() {
		super.writeFooter();
	}

}
