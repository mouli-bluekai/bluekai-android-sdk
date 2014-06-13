/*
 * Copyright 2013-present BlueKai, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluekai.sdk.utils;

import android.util.Log;

public class Logger {
	private static boolean debug = false;
	
	public static void debug(String tag, String message) {
		if (debug) {
			Log.d(getTag(tag), message);
		}
	}

	public static void info(String tag, String message) {
		if (debug) {
			Log.i(getTag(tag), message);
		}
	}

	public static void warn(String tag, String message) {
		Log.w(getTag(tag), message);
	}

	public static void warn(String tag, String message, Throwable ex) {
		Log.w(getTag(tag), message, ex);
	}

	public static void error(String tag, String message) {
		Log.e(getTag(tag), message);
	}

	public static void error(String tag, String message, Throwable ex) {
		Log.e(getTag(tag), message, ex);
	}
	
	private static String getTag(String tag) {
		if (tag == null) {
			tag = "BlueKaiLogger";
		}
		if(Thread.currentThread().getStackTrace() != null && Thread.currentThread().getStackTrace().length > 4) {
			String fullCName = Thread.currentThread().getStackTrace()[4].getClassName();
			String cName = fullCName.substring(fullCName.lastIndexOf(".") + 1);
			String method = Thread.currentThread().getStackTrace()[4].getMethodName();
			int line = Thread.currentThread().getStackTrace()[4].getLineNumber();

			tag += "--" + cName + "." + method + "():" + line;
		}
		return tag;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Logger.debug = debug;
	}

}
