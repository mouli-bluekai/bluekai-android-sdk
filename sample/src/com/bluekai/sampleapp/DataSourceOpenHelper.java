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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bluekai.sdk.utils.Logger;

public class DataSourceOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "sampleapp";
	private static final int DATABASE_VERSION = 1;
	public static final String DEVSETTINGS_BKURL = "bkurl";
	public static final String DEVSETTINGS_DEVMODE = "devmode";
	public static final String DEVSETTINGS_USEHTTPS = "useHttps";

	private final String DEV_SETTINGS_CREATE = "create table devsettings (_id integer primary key autoincrement, "
			+ DEVSETTINGS_BKURL + " text, " + DEVSETTINGS_DEVMODE + " boolean, " + DEVSETTINGS_USEHTTPS + " boolean);";

	public DataSourceOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DatasourceOpenHelper", "Creating table with --> " + DEV_SETTINGS_CREATE);
		db.execSQL(DEV_SETTINGS_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.warn(SQLiteOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS settings");
		db.execSQL("DROP TABLE IF EXISTS params");
		db.execSQL("DROP TABLE IF EXISTS devsettings");
		onCreate(db);
	}

}
