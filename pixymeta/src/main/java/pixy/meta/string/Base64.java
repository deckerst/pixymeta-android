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

package pixy.meta.string;

import java.nio.charset.StandardCharsets;

/**
 * A simple base64 encoding and decoding utility class. It can also
 * encode and decode non ASII characters such as Chinese.
 * <p>
 * Changed decode method to remove potential problem when decoding 
 * concatenated encoded strings.
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.01 04/18/2012
 */
public final class Base64
{
	public static byte[] decodeToByteArray(String s) {
	    if(s.isEmpty()) {
            return null;
        }
        byte[] buf = s.getBytes(StandardCharsets.ISO_8859_1) ;
        byte[] debuf = new byte[buf.length*3/4] ;
        byte[] tempBuf = new byte[4] ;
        int index = 0;
        int index1 = 0;
        int temp;

        // Decode to byte array
        for (byte b : buf) {
            if (b >= 65 && b < 91)
                tempBuf[index++] = (byte) (b - 65);
            else if (b >= 97 && b < 123)
                tempBuf[index++] = (byte) (b - 71);
            else if (b >= 48 && b < 58)
                tempBuf[index++] = (byte) (b + 4);
            else if (b == '+')
                tempBuf[index++] = 62;
            else if (b == '/')
                tempBuf[index++] = 63;
            else if (b == '=') {
                tempBuf[index++] = 0;
            } else { // Discard line breaks and other non-significant characters
                if (b == '\n' || b == '\r' || b == ' ' || b == '\t')
                    continue;
                throw new RuntimeException("Illegal character found in encoded string!");
            }

            if (index == 4) {
                temp = ((tempBuf[0] << 18)) | ((tempBuf[1] << 12)) | ((tempBuf[2] << 6)) | (tempBuf[3]);
                debuf[index1++] = (byte) (temp >> 16);
                debuf[index1++] = (byte) ((temp >> 8) & 255);
                debuf[index1++] = (byte) (temp & 255);
                index = 0;
            }
        }
		return debuf;
    }
}
