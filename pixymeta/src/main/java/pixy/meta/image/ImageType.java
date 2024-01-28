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

package pixy.meta.image;

import java.util.Map;
import java.util.HashMap;

/**
 * Image types supported by ImageReader and ImageWriter.
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 10/09/2012
 */
public enum ImageType {
	
	GIF("Gif") {
	},
	
    PNG("Png") {
	},
	
    JPG("Jpeg") {
	},
	
	JPG2000("Jpeg2000") {
	},
	
    BMP("Bitmap") {
	},
	
    TGA("Targa") {
	},
	
	TIFF("Tiff") {
	},
	
    PCX("Pcx") {
	},
	
	UNKNOWN("Unknown") {
	};
    
    private static final Map<String, ImageType> stringMap = new HashMap<String, ImageType>();
   
    static
    {
      for(ImageType type : values())
          stringMap.put(type.toString(), type);
    }
   
    public static ImageType fromString(String name)
    {
      return stringMap.get(name);
    }
   
    private final String name;
   
    private ImageType(String name)
    {
      this.name = name;
    }

	@Override
    public String toString()
    {
      return name;
    }
}
