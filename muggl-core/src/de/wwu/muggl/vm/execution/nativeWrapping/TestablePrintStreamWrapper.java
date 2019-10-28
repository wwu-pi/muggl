package de.wwu.muggl.vm.execution.nativeWrapping;

import de.wwu.muggl.util.TeePrintStream;

import java.io.*;

/**
 * A print stream wrapper that encapsulates a PrintStream (such as System.out or System.err).
 * All output is first teed into a buffer and is then forwarded to the original PrintStream.
 *
 * It is used to inspect stream output from within tests. For that reason, the buffer can also
 * be reset in between tests, so that multiple tests do not interfere with each other.
 *
 * @author Jan C. Dageförde, based on code from http://www.java2s.com/Code/Java/File-Input-Output/TeePrintStreamteesallPrintStreamoperationsintoafileratherliketheUNIXtee1command.htm
 */
public class TestablePrintStreamWrapper {
    protected static TestablePrintStreamWrapper errorStream = null;
    protected static TestablePrintStreamWrapper outputStream = null;

    private final PrintStream teeingStream;
    private final ByteArrayOutputStream inspectableStream;

    public TestablePrintStreamWrapper(PrintStream output) {
        super();
        this.inspectableStream = new ByteArrayOutputStream();
        PrintStream teeingStream;
        try {
            teeingStream = new TeePrintStream(output, this.inspectableStream);
        } catch (IOException e) {
            teeingStream = output;
        }
        this.teeingStream = teeingStream;
    }

    public PrintStream getTeeingStream() {
        return teeingStream;
    }

    public String getBufferContents() {
        return this.inspectableStream.toString();
    }

    public void resetBuffer() {
        this.inspectableStream.reset();
    }

    public static TestablePrintStreamWrapper errorStream() {
        if (TestablePrintStreamWrapper.errorStream == null) {
            TestablePrintStreamWrapper.errorStream = new TestablePrintStreamWrapper(System.err);
        }
        return TestablePrintStreamWrapper.errorStream;
    }

    public static TestablePrintStreamWrapper outputStream() {
        if (TestablePrintStreamWrapper.outputStream == null) {
            TestablePrintStreamWrapper.outputStream = new TestablePrintStreamWrapper(System.out);
        }
        return TestablePrintStreamWrapper.outputStream;
    }

}