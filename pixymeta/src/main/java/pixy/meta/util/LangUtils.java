/**
 * Copyright (C) 2014-2019 by Wen Yu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Any modifications to this file must keep this entire header intact.
 */

package pixy.meta.util;

import java.io.PrintStream;

import pixy.meta.log.Logger;
import pixy.meta.log.LoggerFactory;

/**
 * A common language utility class
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 09/19/2012
 */
public class LangUtils {
	// Obtain a logger instance
	private static final Logger LOGGER = LoggerFactory.getLogger(LangUtils.class);

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
