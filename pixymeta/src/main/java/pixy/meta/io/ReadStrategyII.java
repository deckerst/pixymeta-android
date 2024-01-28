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

import java.io.InputStream;
import java.io.IOException;

/**
 * Read strategy for Intel byte order LITTLE-ENDIAN stream.
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 12/27/2012
 */
public class ReadStrategyII implements ReadStrategy {

	 private static final ReadStrategyII instance = new ReadStrategyII();
	 
	 public static ReadStrategyII getInstance() 
	 {
		 return instance;
	 }
	 
	 private ReadStrategyII(){}
	 
	 public int readInt(byte[] buf, int start_idx)
	 { 
		 return ((buf[start_idx++]&0xff)|((buf[start_idx++]&0xff)<<8)|
			               ((buf[start_idx++]&0xff)<<16)|((buf[start_idx++]&0xff)<<24));
	 }
	 
	 public int readInt(InputStream is) throws IOException
	 {
		 byte[] buf = new byte[4];
		 IOUtils.readFully(is, buf);
		 
		 return (((buf[3]&0xff)<<24)|((buf[2]&0xff)<<16)|((buf[1]&0xff)<<8)|(buf[0]&0xff));
	 }
	 
	 public long readLong(byte[] buf, int start_idx) 
     {    	 
         return ((buf[start_idx++]&0xffL)|(((buf[start_idx++]&0xffL)<<8)|((buf[start_idx++]&0xffL)<<16)|
        		 ((buf[start_idx++]&0xffL)<<24)|((buf[start_idx++]&0xffL)<<32)|((buf[start_idx++]&0xffL)<<40)|
        		   ((buf[start_idx++]&0xffL)<<48)|(buf[start_idx]&0xffL)<<56));
     }

	public short readShort(byte[] buf, int start_idx)
	 { 
		 return (short)((buf[start_idx++]&0xff)|((buf[start_idx]&0xff)<<8));
	 }

	 public short readShort(InputStream is) throws IOException
	 { 
		 byte[] buf = new byte[2];
		 IOUtils.readFully(is, buf);
		
		 return (short)(((buf[1]&0xff)<<8)|(buf[0]&0xff));
	 }

	public int readUnsignedShort(byte[] buf, int start_idx)
	 { 
		 return ((buf[start_idx++]&0xff)|((buf[start_idx]&0xff)<<8));
	 }

}
