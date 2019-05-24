package de.wwu.muli.searchtree;

public class Exception extends ST {
    public final Object exception;

    /**
     * Represent an exception solution leaf.
     * @param exception Type is object since exceptions returned from symbolic execution could be of type `Objectref'.
     */
    public Exception(Object exception) {
        this.exception = exception;
    }
}
