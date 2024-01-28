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

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 01/29/2013
 */
public class WriteStrategyII implements WriteStrategy {

	private static final WriteStrategyII instance = new WriteStrategyII();	
	 
	public static WriteStrategyII getInstance() 
	{
		return instance;
	}
	 
	private WriteStrategyII(){}
	
	public void writeInt(byte[] buf, int start_idx, int value)
			throws IOException {
		
		byte[] tmp = {(byte)value, (byte)(value>>>8), (byte)(value>>>16), (byte)(value>>>24)};

		System.arraycopy(tmp, 0, buf, start_idx, 4);
	}

	public void writeInt(OutputStream os, int value) throws IOException {
		os.write(new byte[] {
	        (byte)value,
	        (byte)(value>>>8),
	        (byte)(value>>>16),
	        (byte)(value>>>24)});
	}
	
	public void writeLong(byte[] buf, int start_idx, long value) {
		
		byte[] tmp = {(byte)value, (byte)(value>>>8), (byte)(value>>>16),
		           (byte)(value>>>24), (byte)(value>>>32), (byte)(value>>>40),
			       (byte)(value>>>48), (byte)(value>>>56)};
		
		System.arraycopy(tmp, 0, buf, start_idx, 8);
	}

	public void writeShort(byte[] buf, int start_idx, int value)
			throws IOException {
		buf[start_idx] = (byte)value;
		buf[start_idx + 1] = (byte)(value>>>8);
	}

	public void writeShort(OutputStream os, int value) throws IOException {
		os.write(new byte[] {
			  (byte)value,
			  (byte)(value >>> 8)
			  });
	}

}
