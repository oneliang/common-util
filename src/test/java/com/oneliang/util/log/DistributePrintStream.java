package com.oneliang.util.log;

import java.io.OutputStream;
import java.io.PrintStream;

public class DistributePrintStream extends PrintStream{
	private PrintStream[] appendPrintStreams=null;
	public DistributePrintStream(OutputStream outputStream,PrintStream[] appendPrintStreams) {
		super(outputStream);
		this.appendPrintStreams=appendPrintStreams;
	}
	public void write(byte[] buf, int off, int len) {
		if(this.appendPrintStreams!=null){
			for(PrintStream printStream:this.appendPrintStreams){
				if(printStream!=null){
					printStream.write(buf, off, len);
				}
			}
		}
		super.write(buf, off, len);
	}
}