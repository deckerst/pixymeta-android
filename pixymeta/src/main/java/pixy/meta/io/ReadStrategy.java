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
import java.io.InputStream;

/**
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 12/27/2012
 */
public interface ReadStrategy {
	int readInt(byte[] buf, int start_idx);

	int readInt(InputStream is) throws IOException;

	long readLong(byte[] buf, int start_idx);

	short readShort(byte[] buf, int start_idx);

	short readShort(InputStream is) throws IOException;

	int readUnsignedShort(byte[] buf, int start_idx);
}
