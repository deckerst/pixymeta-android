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
 * MetadataUtils.java
 *
 * Who   Date       Description
 * ====  =========  ==============================================================
 * WY    13Mar2015  Initial creation
 */

package pixy.meta.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import pixy.meta.log.Logger;
import pixy.meta.log.LoggerFactory;

import pixy.meta.io.PeekHeadInputStream;
import pixy.meta.io.RandomAccessInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import pixy.meta.image.ImageType;

/** 
 * This utility class contains static methods 
 * to help with image manipulation and IO. 
 * <p>
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.1.2 04/02/2012
 */
public class MetadataUtils {
	// Image magic number constants
	private static final byte[] BM = {0x42, 0x4d}; // BM
	private static final byte[] GIF = {0x47, 0x49, 0x46, 0x38}; // GIF8
	private static final byte[] PNG = {(byte)0x89, 0x50, 0x4e, 0x47}; //.PNG
	private static final byte[] TIFF_II = {0x49, 0x49, 0x2a, 0x00}; // II*.
	private static final byte[] TIFF_MM = {0x4d, 0x4d, 0x00, 0x2a}; //MM.*
	private static final byte[] JPG = {(byte)0xff, (byte)0xd8, (byte)0xff};
	private static final byte[] PCX = {0x0a};
	private static final byte[] JPG2000 = {0x00, 0x00, 0x00, 0x0C};
	
	public static final int IMAGE_MAGIC_NUMBER_LEN = 4; 
	
	// Obtain a logger instance
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataUtils.class);

	public static ImageType guessImageType(PeekHeadInputStream is) throws IOException {
		// Read the first ImageIO.IMAGE_MAGIC_NUMBER_LEN bytes
		byte[] magicNumber = is.peek(IMAGE_MAGIC_NUMBER_LEN);
        return guessImageType(magicNumber);
	}
	
	public static ImageType guessImageType(byte[] magicNumber) {
		ImageType imageType = ImageType.UNKNOWN;
		// Check image type
		if(Arrays.equals(magicNumber, TIFF_II) || Arrays.equals(magicNumber, TIFF_MM))
			imageType = ImageType.TIFF;
		else if(Arrays.equals(magicNumber, PNG))
			imageType = ImageType.PNG;
		else if(Arrays.equals(magicNumber, GIF))
			imageType = ImageType.GIF;
		else if(magicNumber[0] == JPG[0] && magicNumber[1] == JPG[1] && magicNumber[2] == JPG[2])
			imageType = ImageType.JPG;
		else if(magicNumber[0] == BM[0] && magicNumber[1] == BM[1])
			imageType = ImageType.BMP;
		else if(magicNumber[0] == PCX[0])
			imageType = ImageType.PCX;
		else if(Arrays.equals(magicNumber, JPG2000)) {
			imageType = ImageType.JPG2000;
		} else if(magicNumber[1] == 0 || magicNumber[1] == 1) {
			switch(magicNumber[2]) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 9:
				case 10:
				case 11:
				case 32:
				case 33:
					imageType = ImageType.TGA;					
			}
		} else {
			LOGGER.error("Unknown format!");		
		}
		
		return imageType;
	}
	
	public static Bitmap createThumbnail(InputStream is) throws IOException {
		Bitmap original;
		if(is instanceof RandomAccessInputStream) {
			RandomAccessInputStream rin = (RandomAccessInputStream)is;
			long streamPointer = rin.getStreamPointer();
			rin.seek(streamPointer);
			original = BitmapFactory.decodeStream(rin);
			// Reset the stream pointer
			rin.seek(streamPointer);
		} else {
			original = BitmapFactory.decodeStream(is);
		}		
		int imageWidth = original.getWidth();
		int imageHeight = original.getHeight();
		int thumbnailWidth = 160;
		int thumbnailHeight = 120;
		if(imageWidth < imageHeight) { 
			// Swap thumbnail width and height to keep a relative aspect ratio
			int temp = thumbnailWidth;
			//noinspection SuspiciousNameCombination
			thumbnailWidth = thumbnailHeight;
			thumbnailHeight = temp;
		}			
		if(imageWidth < thumbnailWidth) {
			thumbnailWidth = imageWidth;
		}
		if(imageHeight < thumbnailHeight) {
			thumbnailHeight = imageHeight;
		}

        return Bitmap.createScaledBitmap(original, thumbnailWidth, thumbnailHeight, false);
	}
	
	public static int[] toARGB(byte[] rgb) {
		int[] argb = new int[rgb.length / 3];
		int index = 0;
		for(int i = 0; i < argb.length; i++) {
			argb[i] = 0xFF << 24 | (rgb[index++] & 0xFF) << 16 | (rgb[index++] & 0xFF) << 8 | (rgb[index++] & 0xFF);
		}
		
		return argb;
	}
	
	public static int[] bgr2ARGB(byte[] bgr) {
		int[] argb = new int[bgr.length / 3];
		int index = 0;
		for(int i = 0; i < argb.length; i++) {
			argb[i] = 0xFF << 24 | (bgr[index++] & 0xFF) |  (bgr[index++] & 0xFF) << 8 | (bgr[index++] & 0xFF) << 16;
		}
		
		return argb;
	}
	
	// Prevent from instantiation
	private MetadataUtils(){}
}
