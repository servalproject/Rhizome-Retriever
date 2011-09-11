package org.servalproject.rr;

import java.io.File;
import java.io.FilenameFilter;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Rhizome Retriever main activity. Extends ListActivity to be able to list the
 * files in a table.
 * 
 * @author rbochet
 */
public class Main extends ListActivity {

	/** TAG for debugging */
	public static final String TAG = "R2";

	/** The list of file names */
	String[] fList = null;

	/** The list of logical files */
	RhizomeFile[] rList = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "Launch the listing");
		listFiles();

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, fList));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text

				Log.v(TAG, "ID :: " + id);
				Log.v(TAG, rList[(int) id].getFile().toString());

				try {
					Intent myIntent = new Intent(
							android.content.Intent.ACTION_VIEW, Uri.parse("file://"
									+ rList[(int) id].getFile().getAbsolutePath()));

					startActivity(myIntent);
				} catch (Exception e) {
					Log.e(TAG, "Not possible to resolve this intent. Shit.");
					Toast.makeText(getApplicationContext(),
							"This file cannot be opened from here.", Toast.LENGTH_SHORT).show();

				}

			}
		});

	}

	/**
	 * List files of the directory serval on the SD Card
	 */
	private void listFiles() {

		File path = new File(Environment.getExternalStorageDirectory()
				+ "/serval-rhizome");
		Log.v(TAG, path.getAbsolutePath());

		// If the path exists, list all the non-hidden files (no dir)
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					return (sel.isFile() && !sel.isHidden());
				}
			};

			// List of the relative paths
			fList = path.list(filter);
			// List of the RhizomeFile
			rList = new RhizomeFile[fList.length];

			for (int i = 0; i < rList.length; i++) {
				rList[i] = new RhizomeFile(path, fList[i]);
				Log.v(TAG, rList[i].toString());
			}

		} else { // The pass does not exist
			Log.e(TAG, "No serval-rhizome path found on the SD card.");
		}

	}
}