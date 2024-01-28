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

package pixy.meta.util;

import java.io.PrintStream;

/**
 * A common language utility class
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 09/19/2012
 */
public class LangUtils {
	private LangUtils(){} // Prevents instantiation

	public static void log(String message, PrintStream out) {
		StackTraceElement se = Thread.currentThread().getStackTrace()[2];
		out.println("; " + message + " - [" + se.getClassName() + "." + se.getMethodName() +"(): line " + se.getLineNumber() + "]");
	}

	/**
	 * Converts long value to int hash code.
	 *
	 * @param value long value
	 * @return int hash code for the long
	 */
	public static int longToIntHashCode(long value) {
		return Long.valueOf(value).hashCode();
	}
}
