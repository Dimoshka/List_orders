package com.dimoshka.ua.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.dimoshka.ua.list_orders.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class class_function extends Activity {

	public boolean ExternalStorageState() {
		try {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				return true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				return false;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

    public void send_bug_report(Context context, Exception ex,
                                String class_name, Integer num_row) {
        Log.e(context.getString(R.string.app_name) + " - error " + class_name,
                ex.toString() + " - " + num_row);
        BugSenseHandler.addCrashExtraData("class_name", class_name.toString());
        BugSenseHandler.addCrashExtraData("num_row", num_row.toString());
        BugSenseHandler.sendException(ex);
    }


	@SuppressLint("SimpleDateFormat")
	public void file_backup(Context context, String file_name) {
		try {
			if (ExternalStorageState()) {
				String currentTimeString = new SimpleDateFormat("yyyy_MM_dd")
						.format(new Date());

				String dir = Environment.getExternalStorageDirectory()
						+ "/Backup/" + context.getPackageName() + "/";

				File destinationFile = new File(dir + currentTimeString + "_"
						+ file_name);
				File Directory = new File(dir);
				if (!Directory.isDirectory()) {
					Directory.mkdirs();
				}

				InputStream in = null;
				try {

					in = new BufferedInputStream(new FileInputStream(
							"/data/data/" + context.getPackageName()
									+ "/databases/" + file_name));

					FileOutputStream f = new FileOutputStream(destinationFile);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						f.write(buffer, 0, len);
					}
					f.close();

				} catch (Exception e) {
					Log.w("Dimoshka copy backup", e.toString());
				} finally {
					if (in != null) {
						in.close();
					}
					Toast.makeText(context, R.string.backup,
							Toast.LENGTH_SHORT).show();
				}

			}

		} catch (Exception e) {
			Log.w("Dimoshka copy backip", "Erros storage");
		}
	}

	public void send_sms1(Context context, String phoneNumber, String message) {
		SmsManager smsManager = SmsManager.getDefault();
		if (message.length() > 160) {
			ArrayList<String> mArray = smsManager.divideMessage(message);
			smsManager.sendMultipartTextMessage(phoneNumber, null, mArray,
					null, null);
		} else
			smsManager.sendTextMessage(phoneNumber, null, message, null, null);
		Toast.makeText(this, R.string.all_done, Toast.LENGTH_SHORT)
				.show();
	}

	public void file_restory(Context context, String file, String db_name) {
		try {
			if (ExternalStorageState()) {

				InputStream in = null;
				try {
					in = new BufferedInputStream(new FileInputStream(file));
					FileOutputStream f = new FileOutputStream(new File(
							"/data/data/" + context.getPackageName()
									+ "/databases/" + db_name));
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						f.write(buffer, 0, len);
					}
					f.close();
				} catch (Exception e) {
					Log.w("Dimoshka copy backup", e.toString());
				} finally {
					if (in != null) {
						in.close();
					}
					Toast.makeText(context, R.string.restoryed,
							Toast.LENGTH_SHORT).show();
				}
			}

		} catch (Exception e) {
			Log.w("Dimoshka copy backup", "Erros storage");
		}
	}

	public void file_delete(Context context, String file) {
		try {
			if (ExternalStorageState()) {
				try {
					File f = new File(file);
					f.delete();
				} catch (Exception e) {
					Log.w("Dimoshka delete backup", e.toString());
				} finally {
					Toast.makeText(context, R.string.all_done,
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			Log.w("Dimoshka delete backup", "Erros storage");
		}
	}

	public void file_delete_old(File aDirectory) {

		try {

			if (aDirectory.isDirectory()) {

				File[] files = aDirectory.listFiles();

				if (files.length > 5) {

					Arrays.sort(files, new Comparator<File>() {
						public int compare(File f1, File f2) {
							return Long.valueOf(f1.lastModified()).compareTo(
									f2.lastModified());
						}
					});

					int a = 0;
					int b = files.length - 5;
					for (File file : files) {
						a++;
						if (b >= a) {
							file.delete();
							Log.w("Dimoshka delete backup", file.getName()
									.toString());
						}
					}
				}

			}
		} catch (Exception e) {
			Log.w("Dimoshka delete backups all", e.toString());
		}

	}

	public void progress_dialog() {

		ProgressDialog pd;

		pd = new ProgressDialog(this);
		pd.setTitle("Title");
		pd.setMessage(getText(R.string.all_done));
		pd.show();

	}

}
