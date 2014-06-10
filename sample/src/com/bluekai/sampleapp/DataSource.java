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
package com.bluekai.sampleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bluekai.sdk.model.DevSettings;
import com.bluekai.sdk.utils.Logger;

public class DataSource {
	public static final int WRITTABLE_MODE = 1;
	public static final int READABLE_MODE = 2;

	private SQLiteDatabase database;
	private DataSourceOpenHelper dbHelper;
	private static DataSource instance = null;

	private DataSource(Context context) {
		dbHelper = new DataSourceOpenHelper(context);
	}

	public static DataSource getInstance(Context context) {
		if (instance == null) {
			instance = new DataSource(context);
		}
		return instance;
	}

	private void open(int mode) {
		if (mode == WRITTABLE_MODE) {
			database = dbHelper.getWritableDatabase();
		} else if (mode == READABLE_MODE) {
			database = dbHelper.getReadableDatabase();
		}
	}

	private void close() {
		dbHelper.close();
	}

	public void writeDevSettings(DevSettings devSettings) {
		try {
			open(WRITTABLE_MODE);
			database.delete("devsettings", null, null);
			ContentValues values = new ContentValues();
			values.put(DataSourceOpenHelper.DEVSETTINGS_BKURL, devSettings.getBkurl());
			values.put(DataSourceOpenHelper.DEVSETTINGS_DEVMODE, devSettings.isDevMode());
			values.put(DataSourceOpenHelper.DEVSETTINGS_USEHTTPS, devSettings.isUseHttps());
			database.insert("devsettings", null, values);
		} catch (Exception ex) {
			Logger.error("DataSource", "Error while creating settings --> ", ex);
		} finally {
			close();
		}
	}

	public DevSettings getDevSettings() {
		open(READABLE_MODE);
		DevSettings devSettings = null;
		Cursor cursor = database.query("devsettings", null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			devSettings = new DevSettings();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				devSettings.setBkurl(cursor.getString(cursor.getColumnIndex(DataSourceOpenHelper.DEVSETTINGS_BKURL)));
				devSettings.setDevMode(getBoolean(cursor, DataSourceOpenHelper.DEVSETTINGS_DEVMODE));
				devSettings.setUseHttps(getBoolean(cursor, DataSourceOpenHelper.DEVSETTINGS_USEHTTPS));
				cursor.moveToNext();
			}
		}
		cursor.close();
		close();
		return devSettings;
	}
	
	private boolean getBoolean(Cursor cursor, String field) {
		return cursor.getInt(cursor.getColumnIndex(field)) == 0 ? false : true;
	}
}
