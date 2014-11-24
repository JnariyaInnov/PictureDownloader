package com.archee.picturedownloader.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.archee.picturedownloader.PictureDownloader;
import com.archee.picturedownloader.R;
import com.archee.picturedownloader.storage.StorageFactory;
import com.archee.picturedownloader.storage.domain.Entry;

import java.util.List;

public class ListViewActivity extends ListActivity {

    private List<Entry> history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        history = getIntent().getParcelableArrayListExtra(PictureDownloader.EXTRA_HISTORY);
        setListAdapter(new ArrayAdapter<Entry>(this, R.layout.list_item, history));

        final ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getItemAtPosition(position);
                Intent intent = new Intent().putExtra(PictureDownloader.EXTRA_URL, entry.getUrl());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Entry entry = (Entry) parent.getItemAtPosition(position);

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ListViewActivity.this);
                alertBuilder.setTitle(R.string.delete_entry_dialog_title)
                        .setIcon(android.R.drawable.ic_delete)
                        .setView(getLayoutInflater().inflate(R.layout.delete_entry_message, null))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StorageFactory.getStorage().deleteEntry(entry.getUrl());
                                history.remove(entry);
                                ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                            }
                        }).
                        setNegativeButton("Cancel", null)
                        .create().show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
