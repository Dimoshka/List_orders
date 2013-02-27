package com.dimoshka.ua.list_orders;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class preferences extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(preferences.this, R.xml.preferences,
				true);

		Preference filtr_it_t = (Preference) findPreference("filtr_it_t");
		filtr_it_t
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference arg0) {
						Intent i = new Intent(getBaseContext(), filtr.class);
						i.putExtra("table", "items_type");
						startActivity(i);
						return true;
					}
				});

		Preference filtr_st = (Preference) findPreference("filtr_st");
		filtr_st.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference arg0) {
				Intent i = new Intent(getBaseContext(), filtr.class);
				i.putExtra("table", "status");
				startActivity(i);
				return true;
			}
		});

		Preference b_db_backups = (Preference) findPreference("b_db_backups");
		b_db_backups
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference arg0) {
						Intent i = new Intent(getBaseContext(), backup_db.class);
						startActivity(i);
						return true;
					}
				});

		Preference users_group = (Preference) findPreference("users_group");
		users_group
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference arg0) {
						Intent i = new Intent(getBaseContext(),
								managment_select.class);
						i.putExtra("id_type", 1);
						startActivity(i);
						return true;
					}
				});

		Preference items_type = (Preference) findPreference("items_type");
		items_type
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference arg0) {
						Intent i = new Intent(getBaseContext(),
								managment_select.class);
						i.putExtra("id_type", 2);
						startActivity(i);
						return true;
					}
				});

		Preference orders_status = (Preference) findPreference("orders_status");
		orders_status
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference arg0) {
						Intent i = new Intent(getBaseContext(),
								managment_select.class);
						i.putExtra("id_type", 3);
						startActivity(i);
						return true;
					}
				});

	}

}
