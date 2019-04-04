package com.example.application;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    public int id;
    public String name;
    public int amount;
    public String unit;
    public int availability;

    public Item (Integer id,String name, String unit, Integer amount, int availability){
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.availability = availability;
    }

    public Item (String str){
        String[] separated = str.split(" ");
        if (separated.length == 4){
            this.name = separated[0];
            this.amount = Integer.parseInt(separated[1]);
            this.unit = separated[2];
            this.availability = Integer.parseInt(separated[3]);
        }
    }

    protected Item(Parcel in) {
        id = in.readInt();
        name = in.readString();
        amount = in.readInt();
        unit = in.readString();
        availability = in.readInt();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getAvailability(){
        return availability;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(amount);
        dest.writeString(unit);
        dest.writeInt(availability);
    }

    @Override
    public String toString(){
        return name + " "
                + amount + " "
                + unit + " "
                + availability;
    }
}
