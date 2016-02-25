package com.hf.pagerlistview;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hf.fragment.PagedGridFragment;
import com.hf.view.PagedGridView;

public class MainActivity extends AppCompatActivity {
    ListAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView == null) {
                view = new TextView(MainActivity.this);
            } else {
                view = (TextView) convertView;
            }
            view.setText("Item " + position);
            return view;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagedGridFragment fragment = new PagedGridFragment();
        fragment.setAdapter(mAdapter);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment);
        ft.commit();

        // init PagedGridView
        PagedGridView pgv = (PagedGridView) findViewById(R.id.pagedgridview);
        pgv.setAdapter(getSupportFragmentManager(), mAdapter);
    }
}
