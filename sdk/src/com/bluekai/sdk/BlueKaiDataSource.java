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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bluekai.sdk.listeners.SettingsChangedListener;
import com.bluekai.sdk.model.Params;
import com.bluekai.sdk.model.ParamsList;
import com.bluekai.sdk.model.Settings;
import com.bluekai.sdk.utils.Logger;

public class BlueKaiDataSource {

	private SettingsChangedListener listener;

	private final String TAG = "BlueKaiDataSource";

	public static final int WRITTABLE_MODE = 1;
	public static final int READABLE_MODE = 2;

	private SQLiteDatabase database;
	private BlueKaiOpenHelper dbHelper;

	private static BlueKaiDataSource instance = null;

	private BlueKaiDataSource(Context context) {
		dbHelper = new BlueKaiOpenHelper(context);
	}

	public static BlueKaiDataSource getInstance(Context context) {
		if (instance == null) {
			instance = new BlueKaiDataSource(context);
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

	public void setSettingsChangedListener(SettingsChangedListener listener) {
		this.listener = listener;
	}

	public boolean createSettings(Settings settings) {
		try {
			open(WRITTABLE_MODE);
			database.delete("settings", null, null);
			ContentValues values = new ContentValues();
			values.put(BlueKaiOpenHelper.SETTINGS_DATA_POST, settings.isAllowDataPosting());
			long insertId = database.insert("settings", null, values);
			if (insertId > 0) {
				Logger.debug(TAG, "listener --> " + listener);
				if (listener != null) {
					settings.setFirstTime(false);
					listener.onSettingsChanged(settings);
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			Logger.error(TAG, "Error while creating settings --> ", ex);
			return false;
		} finally {
			close();
		}
	}

	public Settings getSettings() {
		open(READABLE_MODE);
		Cursor cursor = database.query("settings", null, null, null, null, null, null);
		Settings settings = new Settings();
		if (cursor.getCount() > 0) {
			settings.setFirstTime(false);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				settings.setAllowDataPosting(getBoolean(cursor, BlueKaiOpenHelper.SETTINGS_DATA_POST));
				cursor.moveToNext();
			}
		}
		cursor.close();
		close();
		return settings;
	}

	private boolean getBoolean(Cursor cursor, String field) {
		return cursor.getInt(cursor.getColumnIndex(field)) == 0 ? false : true;
	}

	public boolean writeParams(Map<String, String> paramsMap) {
		open(WRITTABLE_MODE);
		InsertHelper ih = new InsertHelper(database, "params");
		try {
			int keyColIndex = ih.getColumnIndex(BlueKaiOpenHelper.PARAMS_KEY);
			int valueColIndex = ih.getColumnIndex(BlueKaiOpenHelper.PARAMS_VALUE);
			int triesColIndex = ih.getColumnIndex(BlueKaiOpenHelper.PARAMS_TRIES);
			database.execSQL("PRAGMA synchronous=OFF");
			database.setLockingEnabled(false);
			database.beginTransaction();
			Iterator<String> it = paramsMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = paramsMap.get(key);
				if (key != null && !key.trim().equals("") && value != null && !value.trim().equals("")) {
					ih.prepareForInsert();
					ih.bind(keyColIndex, key);
					ih.bind(valueColIndex, value);
					ih.bind(triesColIndex, 1);
					ih.execute();
				}
			}
			database.setTransactionSuccessful();
			return true;
		} catch (Exception ex) {
			Logger.error(TAG, "Error while inserting params in table", ex);
			return false;
		} finally {
			database.endTransaction();
			database.setLockingEnabled(true);
			database.execSQL("PRAGMA synchronous=NORMAL");
			ih.close();
			close();
		}
	}

	public synchronized ParamsList getParams() {
		open(WRITTABLE_MODE);
		database.execSQL("delete from params where tries >= 5");
		ParamsList paramsList = new ParamsList();
		Cursor cursor = database.query("params", null, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String key = cursor.getString(cursor.getColumnIndex(BlueKaiOpenHelper.PARAMS_KEY));
				String value = cursor.getString(cursor.getColumnIndex(BlueKaiOpenHelper.PARAMS_VALUE));
				int tries = cursor.getInt(cursor.getColumnIndex(BlueKaiOpenHelper.PARAMS_TRIES));
				Params params = new Params();
				params.setId(id);
				params.setKey(key);
				params.setValue(value);
				params.setTries(tries);
				paramsList.add(params);
				cursor.moveToNext();
			}
		}
		cursor.close();
		close();
		return paramsList;
	}

	public synchronized void clearParams() {
		open(WRITTABLE_MODE);
		database.delete("params", null, null);
		close();
	}

	public synchronized void clearParams(ParamsList paramsList) {
		open(WRITTABLE_MODE);
		String whereClause = paramsList.getWhereClause();
		Log.d("DataSource", "clearParams() Where Clause --> " + whereClause);
		database.execSQL("delete from params" + whereClause);
		close();
	}

	public synchronized void persistData(ParamsList paramsList) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		for (int i = 0; i < paramsList.size(); i++) {
			Params params = paramsList.get(i);
			paramsMap.put(params.getKey(), params.getValue());
		}
		writeParams(paramsMap);
	}

	public synchronized void updateData(ParamsList paramsList) {
		open(WRITTABLE_MODE);
		String whereClause = paramsList.getWhereClause();
		database.execSQL("update params set tries = tries+1" + whereClause);
		close();
	}
}
