/*
 * Copyright (c) 2014-2021 by Wen Yu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later version.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 */

package pixy.meta.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Endian-aware InputStream backed up by ReadStrategy
 *  
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 02/03/2014 
 */ 
public class EndianAwareInputStream extends InputStream implements DataInput {
	
    private final InputStream src;
    private final ReadStrategy strategy = ReadStrategyMM.getInstance();
    
	public EndianAwareInputStream(InputStream is) {
	      this.src = is;
	}
	
	public int read() throws IOException {
    	return src.read();
    }

	public boolean readBoolean() throws IOException {
		int ch = this.read();
		if (ch < 0)
		    throw new EOFException();
		return (ch != 0);
	}

	public byte readByte() throws IOException {
	    int ch = this.read();
		if (ch < 0)
		   throw new EOFException();
		return (byte)ch;
	}
	
	public char readChar() throws IOException {
		return (char)(readShort()&0xffff);
	}
	
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}
	
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}	
	
    public void readFully(byte[] b, int off, int len) throws IOException {
		int n = 0;
		do {
			int count = src.read(b, off + n, len - n);
		    if (count < 0)
		        throw new EOFException();
		    n += count;
		} while (n < len);
	}

    public int readInt() throws IOException {
		byte[] buf = new byte[4];
    	readFully(buf);
    	return strategy.readInt(buf, 0);
	}

	@Deprecated
	public String readLine() {
		throw new UnsupportedOperationException(
			"readLine is not supported by RandomAccessInputStream."
		);
	}

	public long readLong() throws IOException {
		byte[] buf = new byte[8];
        readFully(buf);
    	return strategy.readLong(buf, 0);
	}

	public short readShort() throws IOException {
		byte[] buf = new byte[2];
        readFully(buf);
    	return strategy.readShort(buf, 0);
	}

	public int readUnsignedByte() throws IOException {
		int ch = this.read();
		if (ch < 0) {
			throw new EOFException();
		}
	    return ch;
	}

	public int readUnsignedShort() throws IOException {
		return readShort()&0xffff;
	}

	/**
	 *  Due to the current implementation, writeUTF and readUTF are the
	 *  only methods which are machine or byte sequence independent as
	 *  they are actually both Motorola byte sequence under the hood.
	 * <p>
	 *  Whereas the following static method is byte sequence dependent
	 *  as it calls readUnsignedShort of RandomAccessInputStream.
	 * <p>
	 *  <code>DataInputStream.readUTF(this)</code>;
	 */
	public String readUTF() throws IOException {
		return new DataInputStream(this).readUTF();	
	}

	public int skipBytes(int n) throws IOException {
        /* return the actual number of bytes skipped */
		return src.read(new byte[n], 0, n);
	}
	
	public void close() throws IOException {
		src.close();
	}
}
