package com.shaubert.liftago.util;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.shaubert.ui.adapters.CheckableAdapter;
import com.shaubert.ui.adapters.RecyclerCheckableAdapter;

import java.util.ArrayList;

public class ListState {

    private static final String ROW_ID = "ROW_ID";
    private static final String SELECTED_IDS = "SELECTED_IDS";
    private static final String CHOICE_MODE = "CHOICE_MODE";

    private long savedRowId = -1;
    private long[] savedSelectedIds;
    private int savedChoiceMode = -1;

    public static ListState fromBundle(Bundle bundle) {
        ListState listState = new ListState();
        listState.restore(bundle);
        return listState;

    }

    public void restore(Bundle bundle) {
        reset();
        if (bundle == null) return;

        savedRowId = bundle.getLong(ROW_ID, -1);
        savedSelectedIds = bundle.getLongArray(SELECTED_IDS);
        savedChoiceMode = bundle.getInt(CHOICE_MODE, ListView.CHOICE_MODE_NONE);
    }

    public void reset() {
        savedRowId = -1;
        savedSelectedIds = null;
        savedChoiceMode = -1;
    }

    public long getSavedRowId() {
        return savedRowId;
    }

    public long[] getSavedSelectedIds() {
        return savedSelectedIds;
    }

    public int getSavedChoiceMode() {
        return savedChoiceMode;
    }

    public void applyStateToListView(ListView listView, ListAdapter adapter) {
        if (savedChoiceMode != -1) {
            listView.setChoiceMode(savedChoiceMode);
        }

        if (savedRowId >= 0) {
            int pos = findItemWithIdPosition(listView, savedRowId);
            if (pos >= 0) {
                listView.setSelectionFromTop(pos, 0);
            }
        }

        CheckableAdapter checkableAdapter = adapter instanceof CheckableAdapter ? (CheckableAdapter) adapter : null;
        if (savedSelectedIds != null) {
            if (checkableAdapter != null) {
                checkableAdapter.setCheckedItems(savedSelectedIds);
            } else {
                for (long id : savedSelectedIds) {
                    int pos = findItemWithIdPosition(listView, id);
                    if (pos >= 0) {
                        listView.setItemChecked(pos, true);
                    }
                }
            }
        }
    }

    private int findItemWithIdPosition(ListView listView, long id) {
        for (int i = 0; i < listView.getCount(); i++) {
            if (id == listView.getItemIdAtPosition(i)) return i;
        }
        return -1;
    }

    public void applyStateToRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        if (savedRowId >= 0) {
            int pos = 0;
            for (; pos < adapter.getItemCount(); pos++) {
                if (savedRowId == adapter.getItemId(pos)) {
                    break;
                }
            }
            if (pos < adapter.getItemCount() && recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().scrollToPosition(pos);
            }
        }

        RecyclerCheckableAdapter checkableAdapter =
                adapter instanceof  RecyclerCheckableAdapter ? (RecyclerCheckableAdapter) adapter : null;
        if (checkableAdapter != null && savedSelectedIds != null) {
            checkableAdapter.setCheckedItems(savedSelectedIds);
        }
    }

    public void populate(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        reset();

        if (adapter != null && adapter.getItemCount() > 0) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null && layoutManager.getChildCount() > 0) {
                View view = layoutManager.getChildAt(0);
                savedRowId = recyclerView.getChildItemId(view);
            }

            ArrayList<Long> selectedIds = new ArrayList<>();
            RecyclerCheckableAdapter checkableAdapter = adapter instanceof RecyclerCheckableAdapter ? (RecyclerCheckableAdapter) adapter : null;
            if (checkableAdapter != null) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    long itemId = adapter.getItemId(i);
                    if (checkableAdapter.isItemChecked(itemId)) {
                        selectedIds.add(itemId);
                    }
                }
            }
            if (!selectedIds.isEmpty()) {
                savedSelectedIds = new long[selectedIds.size()];
                for (int i = 0; i < savedSelectedIds.length; i++) {
                    savedSelectedIds[i] = selectedIds.get(i);
                }
            }
        }
    }

    public void populate(ListView listView, ListAdapter adapter) {
        reset();

        if (adapter != null && !adapter.isEmpty()) {
            int position = listView.getFirstVisiblePosition();
            savedRowId = listView.getItemIdAtPosition(position);

            ArrayList<Long> selectedIds = new ArrayList<>();
            CheckableAdapter checkableAdapter = adapter instanceof CheckableAdapter ? (CheckableAdapter) adapter : null;
            int headerViewsCount = adapter instanceof HeaderViewListAdapter ? 0 : listView.getHeaderViewsCount();
            for (int i = 0; i < adapter.getCount(); i++) {
                long itemId = adapter.getItemId(i);
                if (checkableAdapter != null) {
                    if (checkableAdapter.isItemChecked(itemId)) {
                        selectedIds.add(itemId);
                    }
                } else if (listView.isItemChecked(headerViewsCount + i)) {
                    selectedIds.add(itemId);
                }
            }
            if (!selectedIds.isEmpty()) {
                savedSelectedIds = new long[selectedIds.size()];
                for (int i = 0; i < savedSelectedIds.length; i++) {
                    savedSelectedIds[i] = selectedIds.get(i);
                }
            }
        }

        savedChoiceMode = listView.getChoiceMode();
    }

    public void toBundle(Bundle bundle) {
        if (savedRowId != -1) bundle.putLong(ROW_ID, savedRowId);
        if (savedSelectedIds != null) bundle.putLongArray(SELECTED_IDS, savedSelectedIds);
        if (savedChoiceMode != -1) bundle.putInt(CHOICE_MODE, savedChoiceMode);
    }

}
