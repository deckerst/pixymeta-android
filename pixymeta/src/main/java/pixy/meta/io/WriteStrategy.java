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
public interface WriteStrategy {
	void writeInt(byte[] buf, int start_idx, int value) throws IOException;

	void writeInt(OutputStream os, int value) throws IOException;

	void writeLong(byte[] buf, int start_idx, long value) throws IOException;

	void writeShort(byte[] buf, int start_idx, int value) throws IOException;

	void writeShort(OutputStream os, int value) throws IOException;
}
