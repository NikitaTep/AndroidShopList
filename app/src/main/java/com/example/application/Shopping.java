package com.example.application;

import android.content.ClipData;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Shopping implements Parcelable {
    private int id;
    private String name;
    private ArrayList<Item> list;

    public Shopping(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.list = new ArrayList<Item>();
    }

    public Shopping(Integer id, String name, ArrayList<Item> list) {
        this.id = id;
        this.name = name;
        this.list = list;
    }

    protected Shopping(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ArrayList<String> mObjList = new ArrayList<String>();
        in.readList(mObjList, getClass().getClassLoader());
        this.list = new ArrayList<Item>();
        for (int i = 0; i < mObjList.size(); i++)
            list.add(new Item(mObjList.get(i)));

    }

    public static final Creator<Shopping> CREATOR = new Creator<Shopping>() {
        @Override
        public Shopping createFromParcel(Parcel in) {
            return new Shopping(in);
        }

        @Override
        public Shopping[] newArray(int size) {
            return new Shopping[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public boolean isNew() {
        return id == 0;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addNewItem(Item item){
        list.add(item);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        ArrayList<String> tmparr = new ArrayList<String>();
        for (int i = 0; i!= list.size(); i++)
            tmparr.add(list.get(i).toString());
        dest.writeList(tmparr);
    }

    @Override
    public String toString() {
        return "Shopping{" +
                "id=" + id +
                ", list='" + list + '\'' +
                '}';
    }

}
