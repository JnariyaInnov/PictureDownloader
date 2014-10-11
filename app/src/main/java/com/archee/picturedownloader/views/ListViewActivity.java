package com.archee.picturedownloader.views;

import android.app.Activity;
import android.app.ListActivity;
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
import com.archee.picturedownloader.storage.domain.Entry;

import java.util.List;

public class ListViewActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Entry> history = getIntent().getParcelableArrayListExtra(PictureDownloader.EXTRA_HISTORY);
        setListAdapter(new ArrayAdapter<Entry>(this, R.layout.list_item, history)); // Will this get called with an up-to-date history object each time this activity is started?
                                                                                    // Or do I need to make some sort of call such as notifyDataSetChanged()?
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = (Entry) parent.getItemAtPosition(position);
                Intent intent = new Intent().putExtra(PictureDownloader.EXTRA_URL, entry.getUrl());
                setResult(Activity.RESULT_OK, intent);
                finish();
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
