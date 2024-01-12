package net.invictusmanagement.invictuslifestyle.customviews;

import android.view.DragEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.AccessPointsAdapter;
import net.invictusmanagement.invictuslifestyle.interfaces.EmptyListener;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;

import java.util.List;

public class DragListener implements View.OnDragListener {

    private boolean isDropped = false;
    private EmptyListener emptyListener;

    public DragListener(EmptyListener emptyListener) {
        this.emptyListener = emptyListener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DROP:
                isDropped = true;
                int positionTarget = -1;

                View viewSource = (View) event.getLocalState();
                int viewId = v.getId();
                final int flItem = R.id.glMain;
//                final int tvEmptyListTop = R.id.tvEmptyListTop;
//                final int tvEmptyListBottom = R.id.tvEmptyListBottom;
//                final int rvTop = R.id.favList;
                final int rvBottom = R.id.list;

                switch (viewId) {
                    case flItem:
//                    case tvEmptyListTop:
//                    case tvEmptyListBottom:
//                    case rvTop:
                    case rvBottom:

                        RecyclerView target;
                        switch (viewId) {
//                            case tvEmptyListTop:
//                            case rvTop:
//                                target = (RecyclerView) v.getRootView().findViewById(rvTop);
//                                break;
//                            case tvEmptyListBottom:
                            case rvBottom:
                                target = (RecyclerView) v.getRootView().findViewById(rvBottom);
                                break;
                            default:
                                target = (RecyclerView) v.getParent();
                                positionTarget = (int) v.getTag();
                        }

                        if (viewSource != null) {
                            RecyclerView source = (RecyclerView) viewSource.getParent();

                            AccessPointsAdapter adapterSource = (AccessPointsAdapter) source.getAdapter();
                            int positionSource = (int) viewSource.getTag();
                            int sourceId = source.getId();

                            AccessPoint list = adapterSource.getList().get(positionSource);
                            List<AccessPoint> listSource = adapterSource.getList();

                            listSource.remove(positionSource);
                            adapterSource.updateList(listSource);
                            adapterSource.notifyDataSetChanged();

                            AccessPointsAdapter adapterTarget = (AccessPointsAdapter) target.getAdapter();
                            List<AccessPoint> customListTarget = adapterTarget.getList();
                            if (positionTarget >= 0) {
                                customListTarget.add(positionTarget, list);
                            } else {
                                customListTarget.add(list);
                            }
                            adapterTarget.updateList(customListTarget);
                            adapterTarget.notifyDataSetChanged();

                            if (sourceId == rvBottom && adapterSource.getItemCount() < 1) {
                                emptyListener.setEmptyListBottom(true);
                            }
//                            if (viewId == tvEmptyListBottom) {
//                                emptyListener.setEmptyListBottom(false);
//                            }
//                            if (sourceId == rvTop && adapterSource.getItemCount() < 1) {
//                                emptyListener.setEmptyListTop(true);
//                            }
//                            if (viewId == tvEmptyListTop) {
//                                emptyListener.setEmptyListTop(false);
//                            }

                            emptyListener.stopDropEvent();

                        }
                        break;
                }
                break;
        }

        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }
}