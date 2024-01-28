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
 *
 * Change History - most recent changes go on top of previous changes
 *
 * ArrayUtils.java
 *
 * Who   Date       Description
 * ====  =========  ======================================================================
 * WY    14Jun2015  Bug fix for toNBits() to use long data type internally
 * WY    04Jun2015  Rewrote all concatenation related methods
 * WY    02Jun2015  Bug fix for generic concatenate methods
 * WY    06Apr2015  Added reverse(byte[]) to reverse byte array elements
 * WY    06Jan2015  Added reverse() to reverse array elements
 * WY    10Dec2014  Moved reverseBits() from IMGUtils to here along with BIT_REVERSE_TABLE
 * WY    08Dec2014  Fixed bug for flipEndian() with more than 32 bit sample data
 * WY    07Dec2014  Changed method names for byte array to other array types conversion
 * WY    07Dec2014  Added new methods to work with floating point TIFF images
 * WY    03Dec2014  Added byteArrayToFloatArray() and byteArrayToDoubleArray()
 * WY    25Nov2014  Added removeDuplicates() to sort and remove duplicates from int arrays
 * WY    12Nov2014  Changed the argument sequence for flipEndian()
 * WY    11Nov2014  Changed flipEndian() to include scan line stride to skip bits
 * WY    11Nov2014  Added toNBits() to convert byte array to nBits data unit
 * WY    28Oct2014  Added flipEndian() to work with TIFTweaker mergeTiffImagesEx()
 */

package pixy.meta.util;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/**
 * Array utility class
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 09/18/2012
 */
public class ArrayUtils
{
	public static byte[] concat(byte[] first, byte[]... rest) {
  	 	if(first == null) {
			throw new IllegalArgumentException("Firt element is null");
		}
  	 	if(rest.length == 0) {
			return first;
		}

		// Now the real stuff
  	  	int totalLength = first.length;

		for (byte[] array : rest) {
			totalLength += array.length;
	 	}

		byte[] result = new byte[totalLength];

		int offset = first.length;

		System.arraycopy(first, 0, result, 0, offset);

		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}

		return result;
	}

    public static byte[] subArray(byte[] src, int offset, int len) {
		if(offset == 0 && len == src.length) return src;
		if((offset < 0 || offset >= src.length) || (offset + len > src.length))
			throw new IllegalArgumentException("Copy range out of array bounds");
		byte[] dest = new byte[len];
		System.arraycopy(src, offset, dest, 0, len);

		return dest;
	}

    public static byte[] toByteArray(int value) {
		return new byte[] {
	        (byte)value,
	        (byte)(value >>> 8),
	        (byte)(value >>> 16),
	        (byte)(value >>> 24)
	        };
	}

    public static byte[] toByteArray(int[] data, boolean bigEndian) {

		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);

		if (bigEndian) {
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
		} else {
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}

		IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(data);

        return byteBuffer.array();
	}

  	public static byte[] toByteArray(long[] data, boolean bigEndian) {

		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 8);

		if (bigEndian) {
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
		} else {
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}

		LongBuffer longBuffer = byteBuffer.asLongBuffer();
        longBuffer.put(data);

        return byteBuffer.array();
	}

	public static byte[] toByteArray(short value) {
		 return new byte[] {
				 (byte)value, (byte)(value >>> 8)};
	}

	public static byte[] toByteArray(short[] data, boolean bigEndian) {

		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 2);

		if (bigEndian) {
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
		} else {
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}

		ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(data);

        return byteBuffer.array();
	}

    public static byte[] toByteArrayMM(int value) {
    	return new byte[] {
	        (byte)(value >>> 24),
	        (byte)(value >>> 16),
	        (byte)(value >>> 8),
	        (byte)value};
	}

   	private ArrayUtils(){} // Prevents instantiation
}
