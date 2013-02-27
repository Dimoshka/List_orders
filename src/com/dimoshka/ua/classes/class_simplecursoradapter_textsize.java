package com.dimoshka.ua.classes;

import com.dimoshka.ua.list_orders.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class class_simplecursoradapter_textsize extends SimpleCursorAdapter {
	private int layout;
	private String[] from;
	private int[] to;
	private String size_text;

	public class_simplecursoradapter_textsize(Context context, int layout,
			Cursor c, String[] from, int[] to, String size_text) {
		super(context, layout, c, from, to);
		this.layout = layout;
		this.from = from;
		this.to = to;
		this.size_text = size_text;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {

		for (int i = 0; i < from.length; i++) {
			TextView t = (TextView) v.findViewById(to[i]);
			t.setText(c.getString(c.getColumnIndex(from[i])));

			if (size_text.equals("1")) {
				t.setTextSize(context.getResources().getDimension(
						R.dimen.text_very_small));
			} else if (size_text.equals("2")) {
				t.setTextSize(context.getResources().getDimension(
						R.dimen.text_small));
			} else if (size_text.equals("3")) {
				t.setTextSize(context.getResources().getDimension(
						R.dimen.text_medium));
			} else {
				t.setTextSize(context.getResources().getDimension(
						R.dimen.text_large));
			}
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(layout, parent, false);
	}
}
