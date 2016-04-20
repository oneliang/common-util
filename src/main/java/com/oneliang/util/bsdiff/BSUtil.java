package com.oneliang.util.bsdiff;


import java.io.IOException;
import java.io.InputStream;

public class BSUtil {

	// JBDiff extensions by Stefan.Liebig@compeople.de:
	//
	// - introduced a HEADER_SIZE constant here

	/**
	 * Length of the diff file header.
	 */
	public static final int HEADER_SIZE = 32;


	/**
	 * Read from input stream and fill the given buffer from the given offset up
	 * to length len.
	 * 
	 * @param in
	 * @param buf
	 * @param offset
	 * @param len
	 * @throws IOException
	 */
	public static final boolean readFromStream(InputStream in, byte[] buf, int offset, int len) throws IOException {

		int totalBytesRead = 0;
		while (totalBytesRead < len) {
			int bytesRead = in.read(buf, offset + totalBytesRead, len - totalBytesRead);
			if (bytesRead < 0) {
				return false;
			}
			totalBytesRead += bytesRead;
		}
		return true;
	}
}