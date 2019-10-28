package de.wwu.muggl.util;

/*
 * Obtained from http://www.java2s.com/Code/Java/File-Input-Output/TeePrintStreamteesallPrintStreamoperationsintoafileratherliketheUNIXtee1command.htm
 *
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java
 * language and environment is gratefully acknowledged.
 *
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * TeePrintStream tees all PrintStream operations into a file, rather like the
 * UNIX tee(1) command. It is a PrintStream subclass. The expected usage would
 * be something like the following:
 *
 * <PRE>
 *
 * ... TeePrintStream ts = new TeePrintStream(System.err, "err.log");
 * System.setErr(ts); // ...lots of code that occasionally writes to
 * System.err... ts.close(); ...
 *
 * <PRE>
 *
 * <P>
 * I only override Constructors, the write(), check() and close() methods, since
 * any of the print() or println() methods must go through these. Thanks to
 * Svante Karlsson for help formulating this.
 *
 * @author Ian F. Darwin, http://www.darwinsys.com/
 * @version $Id: TeePrintStream.java,v 1.5 2004/02/08 23:57:29 ian Exp $
 */
public class TeePrintStream extends PrintStream {
    protected PrintStream parent;

    protected String fileName;

    /** A simple test case. */
    public static void main(String[] args) throws IOException {
        TeePrintStream ts = new TeePrintStream(System.err, "err.log", true);
        System.setErr(ts);
        System.err.println("An imitation error message");
        ts.close();
    }

    /**
     * Construct a TeePrintStream given an existing PrintStream, an opened
     * OutputStream, and a boolean to control auto-flush. This is the main
     * constructor, to which others delegate via "this".
     */
    public TeePrintStream(PrintStream orig, OutputStream os, boolean flush)
            throws IOException {
        super(os, true);
        fileName = "(opened Stream)";
        parent = orig;
    }

    /**
     * Construct a TeePrintStream given an existing PrintStream and an opened
     * OutputStream.
     */
    public TeePrintStream(PrintStream orig, OutputStream os) throws IOException {
        this(orig, os, true);
    }

    /*
     * Construct a TeePrintStream given an existing Stream and a filename.
     */
    public TeePrintStream(PrintStream os, String fn) throws IOException {
        this(os, fn, true);
    }

    /*
     * Construct a TeePrintStream given an existing Stream, a filename, and a
     * boolean to control the flush operation.
     */
    public TeePrintStream(PrintStream orig, String fn, boolean flush)
            throws IOException {
        this(orig, new FileOutputStream(fn), flush);
    }

    /** Return true if either stream has an error. */
    public boolean checkError() {
        return parent.checkError() || super.checkError();
    }

    /** override write(). This is the actual "tee" operation. */
    public void write(int x) {
        parent.write(x); // "write once;
        super.write(x); // write somewhere else."
    }

    /** override write(). This is the actual "tee" operation. */
    public void write(byte[] x, int o, int l) {
        parent.write(x, o, l); // "write once;
        super.write(x, o, l); // write somewhere else."
    }

    /** Close both streams. */
    public void close() {
        parent.close();
        super.close();
    }

    /** Flush both streams. */
    public void flush() {
        parent.flush();
        super.flush();
    }
}