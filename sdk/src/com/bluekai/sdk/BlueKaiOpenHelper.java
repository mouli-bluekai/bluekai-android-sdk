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
package com.bluekai.sdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bluekai.sdk.utils.Logger;

public class BlueKaiOpenHelper extends SQLiteOpenHelper {

	private final String TAG = "BlueKaiOpenHelper";

	private static final String DATABASE_NAME = "bluekai";
	private static final int DATABASE_VERSION = 5;

	public static final String SETTINGS_DATA_POST = "post_data";
	public static final String PARAMS_KEY = "key";
	public static final String PARAMS_VALUE = "value";
	public static final String PARAMS_TRIES = "tries";
	

	private final String SETTINGS_CREATE = "create table settings " + "( _id integer primary key autoincrement, "
			+ SETTINGS_DATA_POST + " boolean);";
	private final String PARAMS_CREATE = "create table params " + "( _id integer primary key autoincrement, "
			+ PARAMS_KEY + " text, " + PARAMS_VALUE + " text, " + PARAMS_TRIES + " integer);";
	

	public BlueKaiOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Logger.debug(TAG, "Creating table with --> " + SETTINGS_CREATE);
		db.execSQL(SETTINGS_CREATE);
		Logger.debug(TAG, "Creating table with --> " + PARAMS_CREATE);
		db.execSQL(PARAMS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.warn(SQLiteOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS settings");
		db.execSQL("DROP TABLE IF EXISTS params");
		onCreate(db);
	}
}
