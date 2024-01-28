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
 * StringUtils.java
 *
 * Who   Date       Description
 * ====  =========  ==============================================================
 * WY    03May2015  Added rationalToString()
 * WY    04Mar2015  Added toHexString()
 * WY    04Mar2015  Added generateMD5()
 * WY    07Feb201   Added decimalToDMS() and DMSToDecimal()
 * WY    23Jan2015  Moved XML related methods to XMLUtils
 * WY    10Jan2015  Added showXML() and printNode() to show XML document
 * WY    28Dec2014  Added isInCharset() to test if a String can be encoded with
 * 					certain character set.
 */

package pixy.meta.string;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.*;

/**
 * String utility class  
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 09/18/2012
 */
public class StringUtils {
	
	private static final char[] HEXES = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	/**
	 * Formats byte array.
	 * 
	 * @param bytes an array of byte.
	 * @return a hex string representation of the byte array.
	 */
	public static String byteArrayToHexString(byte[] bytes) {	    
	    return byteArrayToHexString(bytes, 0, bytes.length);
	}
	
	public static String byteArrayToHexString(byte[] bytes, int offset, int length) {		
		if ( bytes == null ) {
			return null;
		}
		
		if(bytes.length == 0) {
			return "[]";
		}
	    
	    if(offset < 0 || offset >= bytes.length) {
			throw new IllegalArgumentException("Offset out of array bound!");
		}
	    
	    int endOffset = offset + Math.min(length, bytes.length);
		 
	    if(endOffset > bytes.length) {
			length = bytes.length - offset;
		}
	    
	    StringBuilder hex = new StringBuilder(5*length + 2);	    
	    hex.append("[");
	    
	    for (int i = offset; i < endOffset; i++) {
	    	hex.append("0x").append(HEXES[(bytes[i] & 0xf0) >> 4])
	         .append(HEXES[bytes[i] & 0x0f]).append(",");
	    }
	    
	    // Remove the last ","
	    if(hex.length() > 1) {
			hex.deleteCharAt(hex.length() - 1);
		}
	    
	    if(endOffset < bytes.length) {
			hex.append(" ..."); // Partial output
		}

	    hex.append("]");
	    
	    return hex.toString();
	}
	
	/**
	 * Capitalizes the first character of the words in a string.
	 * 
	 * @param s the input string
	 * @return a string with the first character of all words capitalized
	 */
	public static String capitalize(String s) {   
		StringBuffer myStringBuffer = new StringBuffer();
		Pattern p = Pattern.compile("\\b(\\w)(\\w*)");
		Matcher m = p.matcher(s);
		
        while (m.find()) {
			if(!Character.isUpperCase(m.group().charAt(0)))
               m.appendReplacement(myStringBuffer, Objects.requireNonNull(m.group(1)).toUpperCase(Locale.ROOT)+"$2");
        }
        
        return m.appendTail(myStringBuffer).toString();
	}
	
	public static String capitalizeFully(String s) {   
		return capitalize(s.toLowerCase(Locale.ROOT));
	}

	/**
	 * Regular expression version of the String contains method.
	 * If used with a match from start or match from end regular expression,
	 * it becomes the regular expression version of the {@link String#
	 * startsWith(String prefix)} or {@link String#endsWith(String suffix)}
	 * methods.
	 * 
	 * @param input the input string
	 * @param regex the regular expression to which this string is to be matched
	 * @return true if a match is found, otherwise false
	 */
	public static boolean contains(String input, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
        return m.find();
    }

	/**
	 * Generate MD5 digest from a byte array
	 * 
	 * @param message byte array to generate MD5
	 * @return MD5 string
	 */
	public static String generateMD5(byte[] message) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No such algorithm: MD5");
		}
		
		return toHexString(md.digest(message));
    }

	public static String intToHexStringMM(int value) {
        return "0x" +
				HEXES[(value & 0xF0000000) >>> 28] +
				HEXES[(value & 0x0F000000) >>> 24] +
				HEXES[(value & 0x00F00000) >>> 20] +
				HEXES[(value & 0x000F0000) >>> 16] +
				HEXES[(value & 0x0000F000) >>> 12] +
				HEXES[(value & 0x00000F00) >>> 8] +
				HEXES[(value & 0x000000F0) >>> 4] +
				HEXES[(value & 0x0000000F)];
	}

	/**
	 * Checks if a string is null, empty, or consists only of white spaces
	 * 
	 * @param str the input CharSequence to check
	 * @return true if the input string is null, empty, or contains only white
	 * spaces, otherwise false
	 */
	public static boolean isNullOrEmpty(CharSequence str) {
		return ((str == null) || (str.length() == 0));
	}

	public static String longArrayToString(int[] data, int offset, int length, boolean unsigned) {
		if ( data == null ) {
		      return null;
		}
			
		if(data.length == 0) {
			return "[]";
		}
	    
	    if(offset < 0 || offset >= data.length) {
			throw new IllegalArgumentException("Offset out of array bound!");
		}
	    
	    int endOffset = offset + Math.min(length, data.length);
		 
	    if(endOffset > data.length) {
			length = data.length - offset;
		}
	    
	    StringBuilder longs = new StringBuilder();	    
	    longs.append("[");
		    
	    for (int i = offset; i < endOffset; i++)
		{
	    	if(unsigned) {
				// Convert it to unsigned integer
				longs.append(data[i]&0xffffffffL);
			} else {
				longs.append(data[i]);
			}
			longs.append(",");
		}
	    
	    // Remove the last ","
	    if(longs.length() > 1) {
			longs.deleteCharAt(longs.length() - 1);
		}

	    if(endOffset < data.length) {
			longs.append(" ..."); // Partial output
		}

	    longs.append("]");
	    
	    return longs.toString();	    
	}
	
	/**
	 * Formats TIFF rational data field.
	 * 
	 * @param data an array of int.
	 * @param unsigned true if the int value should be treated as unsigned,
	 * 		  otherwise false 
	 * @return a string representation of the int array.
	 */
	public static String rationalArrayToString(int[] data, boolean unsigned) {
		if(data.length%2 != 0)
			throw new IllegalArgumentException("Data length is odd number, expect even!");

		StringBuilder rational = new StringBuilder();
		rational.append("[");
		
		for (int i=0; i<data.length; i+=2)
		{
			long  numerator = data[i], denominator = data[i+1];
			
			//if(denominator == 0) throw new ArithmeticException("Divided by zero");
			
			if (unsigned) {
				// Converts it to unsigned integer
				numerator = (data[i]&0xffffffffL);
				denominator = (data[i+1]&0xffffffffL);
			}
			
			rational.append(numerator);			
			rational.append("/");
			rational.append(denominator);
			
			rational.append(",");
		}
		
		rational.deleteCharAt(rational.length()-1);
		rational.append("]");
		
		return rational.toString();
	}
	
	public static String rationalToString(DecimalFormat df, boolean unsigned, int ... rational) {
		if(rational.length < 2) throw new IllegalArgumentException("Input data length is too short");
		if(rational[1] == 0) throw new ArithmeticException("Divided by zero");
		
		long numerator = rational[0];
		long denominator= rational[1];
		
		if (unsigned) {
			// Converts it to unsigned integer
			numerator = (numerator&0xffffffffL);
			denominator = (denominator&0xffffffffL);
		}
		
		return df.format(1.0*numerator/denominator);
	}
	
	/**
	 * Replaces the last occurrence of the string represented by the regular expression
	 *  
	 * @param input input string
 	 * @param regex the regular expression to which this string is to be matched
	 * @param replacement the string to be substituted for the match
	 * @return the resulting String
	 */
	public static String replaceLast(String input, String regex, String replacement) {
		return input.replaceAll(regex+"(?!.*"+regex+")", replacement); // Using negative look ahead
	}
	
	public static String shortArrayToString(short[] data, int offset, int length, boolean unsigned) {
		if ( data == null ) {
		      return null;
		}
			
		if(data.length == 0) return "[]";
	    
	    if(offset < 0 || offset >= data.length)
	    	throw new IllegalArgumentException("Offset out of array bound!");
	    
	    int endOffset = offset + Math.min(length, data.length);
		 
	    if(endOffset > data.length)
	    	length = data.length - offset;
	    
	    StringBuilder shorts = new StringBuilder();	    
	    shorts.append("[");
		    
	    for (int i = offset; i < endOffset; i++)
		{
			if(unsigned) {
				// Convert it to unsigned short
				shorts.append(data[i]&0xffff);
			} else {
				shorts.append(data[i]);
			}
			shorts.append(",");
		}
	    
	    // Remove the last ","
	    if(shorts.length() > 1)
	    	shorts.deleteCharAt(shorts.length()-1);
	    
	    if(endOffset < data.length)
	    	shorts.append(" ..."); // Partial output
	    
	    shorts.append("]");
		
		return shorts.toString();
	}
	
	public static String shortToHexStringMM(short value) {

        return "0x" +
				HEXES[(value & 0xF000) >>> 12] +
				HEXES[(value & 0x0F00) >>> 8] +
				HEXES[(value & 0x00F0) >>> 4] +
				HEXES[(value & 0x000F)];
	}
	
	/**
	 * A read-only String iterator from stackoverflow.com
	 * 
	 * @param string input string to be iterated
	 * @return an iterator for the input string
	 */
	public static Iterator<Character> stringIterator(final String string) {
		// Ensure the error is found as soon as possible.
		if (string == null)
			throw new NullPointerException();

		return new Iterator<Character>() {
			private int index = 0;

			public boolean hasNext() {
				return index < string.length();
			}

			public Character next() {
				/*
				 * Throw NoSuchElementException as defined by the Iterator contract,
				 * not IndexOutOfBoundsException.
				 */
				if (!hasNext())
					throw new NoSuchElementException();
				return string.charAt(index++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public static String toHexString(byte[] bytes) {
		return toHexString(bytes, 0, bytes.length);
	}
	
	/**
	 * Convert byte array to hex string
	 * 
	 * @param bytes input byte array
	 * @param offset start offset
	 * @param length number of items to include
	 *  
	 * @return a hex string representation for the byte array without 0x prefix
	 */
	public static String toHexString(byte[] bytes, int offset, int length) {		
		if ( bytes == null )
			return null;
	    
		if(bytes.length == 0) return "";
	    
	    if(offset < 0 || offset >= bytes.length)
	    	throw new IllegalArgumentException("Offset out of array bound!");
	    
	    int endOffset = offset + Math.min(length, bytes.length);
		 
	    if(endOffset > bytes.length)
	    	length = bytes.length - offset;
	    
	    StringBuilder hex = new StringBuilder(5*length + 2);	    
		    
	    for (int i = offset; i < endOffset; i++) {
	    	hex.append(HEXES[(bytes[i] & 0xf0) >> 4])
	         .append(HEXES[bytes[i] & 0x0f]);
	    }
	    
	    return hex.toString();
	}
	
	public static String toUTF16BE(byte[] data, int start, int length) {
        return new String(data, start, length, StandardCharsets.UTF_16BE);
	}
	
	private StringUtils(){} // Prevents instantiation	
}
