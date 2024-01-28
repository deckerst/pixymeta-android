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

package pixy.meta.meta.iptc;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import pixy.meta.string.StringUtils;

/**
 * Defines DataSet tags for IPTC FotoStation Record - Record number 240.
 * * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 03/13/2015
 */
public enum IPTCFotoStationTag implements IPTCTag  {
	// No record available
	UNKNOWN(999, "Unknown");
	 
	IPTCFotoStationTag(int tag, String name) {
		this.tag = tag;
		this.name = name;
	}
	 
	public static IPTCFotoStationTag fromTag(int value) {
		IPTCFotoStationTag record = recordMap.get(value);
	   	if (record == null) {
			return UNKNOWN;
		}
		return record;
	}
	
	 public boolean allowMultiple() {
		 return false;
	 }
	 
	 // Default implementation. Could be replaced by individual ENUM
	 public String getDataAsString(byte[] data) {
         String strVal = new String(data, StandardCharsets.UTF_8).trim();
         if(!strVal.isEmpty()) return strVal;
         // Hex representation of the data
		 return StringUtils.byteArrayToHexString(data, 0, MAX_STRING_REPR_LEN);
	 }
	
	public String getName() {
		return name;
	}
	
	public int getRecordNumber() {
		return IPTCRecord.FOTOSTATION.getRecordNumber();
	}
	
	public int getTag() {
		return tag;
	}	
 
	@Override public String toString() {
		return name;
	}
	 
	private static final Map<Integer, IPTCFotoStationTag> recordMap = new HashMap<>();
	  
	static
	{
	    for(IPTCFotoStationTag record : values()) {
	        recordMap.put(record.getTag(), record);
	    }
	}	    
	
	private final int tag;
	private final String name;
}
