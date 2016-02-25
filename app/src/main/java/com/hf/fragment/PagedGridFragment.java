package com.hf.fragment;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.security.InvalidParameterException;

/**
 * Pager Grid Fragment
 * Created by Fan on 2016/2/24.
 */
public class PagedGridFragment extends Fragment {
    private static final int DEFAULT_MAX_COLUMN = 4;
    private static final int DEFAULT_MAX_ROW = 2;

    private PagerAdapter mPagerAdapter = null;
    private static SparseArray<ListAdapter> mAdapterList = new SparseArray<>();

    private static int sObjectCount = 0;
    private int mObjectId = 0;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mPagerAdapter.notifyDataSetChanged();
        }
    };

    public PagedGridFragment() {
        super();

        initObjectId();
    }

    public synchronized void initObjectId() {
        mObjectId = sObjectCount;
        sObjectCount ++;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPagerAdapter = new LocalFragmentPagerAdapter(getFragmentManager());

        ViewPager mViewPager = new ViewPager(getContext());
        mViewPager.setId(android.R.id.primary);
        mViewPager.setAdapter(mPagerAdapter);
        return mViewPager;
    }

    @Override
    public void onDestroy() {
        ListAdapter adapter = getAdapter();
        if (adapter != null) {
            adapter.unregisterDataSetObserver(mDataSetObserver);
            mAdapterList.remove(mObjectId);
        }
        super.onDestroy();
    }

    public void setAdapter(ListAdapter adapter) {
        adapter.registerDataSetObserver(mDataSetObserver);
        mAdapterList.put(mObjectId, adapter);

        if (mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    public ListAdapter getAdapter() {
        return mAdapterList.get(mObjectId);
    }

    public static ListAdapter getAdapter(int id) {
        return mAdapterList.get(id);
    }

    private class LocalFragmentPagerAdapter extends FragmentPagerAdapter {
        public LocalFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GridFragment fragment = new GridFragment();
            fragment.setArgPosition(position);
            fragment.setArgMaxColumn(DEFAULT_MAX_COLUMN);
            fragment.setArgMaxRow(DEFAULT_MAX_ROW);
            fragment.setArgParentId(mObjectId);
            return fragment;
        }

        @Override
        public int getCount() {
            return (getAdapter().getCount() - 1) / DEFAULT_MAX_COLUMN / DEFAULT_MAX_ROW  + 1;
        }
    }

    public static class GridFragment extends Fragment {
        private static final String ARG_POSITION = "arg-position";
        private static final String ARG_MAX_COLUMN = "arg-max-column";
        private static final String ARG_MAX_ROW = "arg-max-row";
        private static final String ARG_PARENT_ID = "arg-parent-id";

        private GridView mGridView = null;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int position = getArgPosition();
            int maxColumn = getArgMaxColumn();
            int maxRow = getArgMaxRow();
            int maxItemCount = maxColumn * maxRow;

            mGridView = new GridView(getContext());
            mGridView.setNumColumns(maxColumn);
            mGridView.setAdapter(new SubListAdapter(
                    PagedGridFragment.getAdapter(getArgParentId()),
                    position * maxItemCount,
                    maxItemCount));

            return mGridView;
        }

        public void setArg(String key, int val) {
            Bundle args = getArguments();
            if (args == null) {
                args = new Bundle();
            }
            args.putInt(key, val);
            setArguments(args);
        }

        public int getArg(String key) {
            Bundle args = getArguments();
            if (args != null) {
                return args.getInt(key, 0);
            }
            return 0;
        }

        public void setArgPosition(int pos) {
            setArg(ARG_POSITION, pos);
        }

        public int getArgPosition() {
            return getArg(ARG_POSITION);
        }

        public void setArgMaxColumn(int maxColumn) {
            setArg(ARG_MAX_COLUMN, maxColumn);
        }

        public int getArgMaxColumn() {
            return getArg(ARG_MAX_COLUMN);
        }

        public void setArgMaxRow(int maxRow) {
            setArg(ARG_MAX_ROW, maxRow);
        }

        public int getArgMaxRow() {
            return getArg(ARG_MAX_ROW);
        }

        public void setArgParentId(int maxRow) {
            setArg(ARG_PARENT_ID, maxRow);
        }

        public int getArgParentId() {
            return getArg(ARG_PARENT_ID);
        }
    }

    private static class SubListAdapter extends BaseAdapter {
        private ListAdapter mParentAdapter;
        private int mOffset;
        private int mItemCount;

        public SubListAdapter(ListAdapter parentAdapter, int parentOffset, int maxItemCount) {
            if (parentAdapter == null || parentOffset < 0 || parentOffset >= parentAdapter.getCount()) {
                throw new InvalidParameterException("parentAdapter is null or parentOffset is out of the range of parent adapter");
            }

            // Save parent adapter
            mParentAdapter = parentAdapter;

            // Offset in parent adapter
            mOffset = parentOffset;

            // Real item count
            int parentCountFromOffset = mParentAdapter.getCount() - parentOffset;
            mItemCount = Math.min(parentCountFromOffset, maxItemCount);
        }

        @Override
        public int getCount() {
            return mItemCount;
        }

        @Override
        public Object getItem(int position) {
            return mParentAdapter.getItem(positionInParent(position));
        }

        @Override
        public long getItemId(int position) {
            return mParentAdapter.getItemId(positionInParent(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mParentAdapter.getView(positionInParent(position), convertView, parent);
        }

        private int positionInParent(int position) {
            return mOffset + position;
        }
    }
}
