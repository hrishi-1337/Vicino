package com.viscino.viscino.Utils;

import android.widget.ListAdapter;

import com.viscino.viscino.Models.GridItem;

import java.util.List;

/**
 * Created by j on 12-10-2017.
 */

public interface GridAdapter  extends ListAdapter {

    void appendItems(List<GridItem> newItems);

    void setItems(List<GridItem> moreItems);
}
