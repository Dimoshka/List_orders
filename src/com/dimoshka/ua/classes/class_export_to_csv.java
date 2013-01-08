package com.dimoshka.ua.classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class class_export_to_csv extends AsyncTask<Cursor, Void, Boolean> {

	private class_function funct = new class_function();

	protected Boolean doInBackground(Cursor... arg) {
		if (funct.ExternalStorageState()) {
			StringBuilder stringBuilder = new StringBuilder();
			Cursor cur = arg[0];
			for (int j = 0; j < cur.getColumnCount(); j++) {
				if (j < cur.getColumnCount() & j > 0)
					stringBuilder.append(",");
				stringBuilder.append("\"" + cur.getColumnName(j) + "\"");
			}

			stringBuilder.append("\n");
			cur.moveToFirst();
			for (int i = 0; i < cur.getCount(); i++) {
				for (int j = 0; j < cur.getColumnCount(); j++) {
					if (j < cur.getColumnCount() & j > 0)
						stringBuilder.append(",");
					stringBuilder.append("\"" + cur.getString(j) + "\"");
				}
				stringBuilder.append("\n");
				cur.moveToNext();
			}

			File gpxfile = new File(Environment.getExternalStorageDirectory(),
					"eport.csv");
			FileWriter writer;
			try {
				writer = new FileWriter(gpxfile);
				writer.append(stringBuilder.toString());
				writer.flush();
				writer.close();
				return true;
			} catch (IOException e) {
				Log.d("Dimoshka", e.toString());
				return false;
			}
		} else
			return false;
	}

	protected void onPreExecute() {
		// super.onPreExecute();
		// tvInfo.setText("Begin");
	}

	protected void onPostExecute(Void result) {
		// super.onPostExecute(result);
		// tvInfo.setText("End");
	}

}
