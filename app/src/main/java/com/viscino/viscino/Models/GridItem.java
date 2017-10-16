package com.viscino.viscino.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

/**
 * Created by j on 10-10-2017.
 */

public class GridItem implements AsymmetricItem {

    private int columnSpan;
    private int rowSpan;
    private int position;
    private String id;
    private String text;
    private String url;

    public GridItem() {
        this(1, 1, 0,"","","");
    }

    public GridItem(int columnSpan, int rowSpan, int position,String id, String text, String url) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
        this.id = id;
        this.text = text;
        this.url = url;
    }

    private GridItem(Parcel in) {
        readFromParcel(in);
    }

    @Override public int getColumnSpan() {
        return columnSpan;
    }

    @Override public int getRowSpan() {
        return rowSpan;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    @Override public String toString() {
        return String.format("%s: %sx%s", position, rowSpan, columnSpan,id,text,url);
    }

    @Override public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
        text = in.readString();
    }

    @Override public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
        dest.writeString(text);
    }

    /* Parcelable interface implementation */
    public static final Parcelable.Creator<GridItem> CREATOR = new Parcelable.Creator<GridItem>() {

        @Override public GridItem createFromParcel(@NonNull Parcel in) {
            return new GridItem(in);
        }

        @Override @NonNull public GridItem[] newArray(int size) {
            return new GridItem[size];
        }
    };
}
