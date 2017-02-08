package com.gputreats.orbitalexplorer;

import android.os.Parcel;
import android.os.Parcelable;

class Orbital extends BaseOrbital implements Parcelable {
    Orbital(int inZ, int inN, int inL, int inM, boolean inReal, boolean inColor) {
        super(inZ, inN, inL, inM, inReal, inColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(qZ);
        dest.writeInt(qN);
        dest.writeInt(qL);
        dest.writeInt(qM);
        dest.writeInt(real ? 1 : 0);
        dest.writeInt(color ? 1 : 0);
    }

    public static final Parcelable.Creator<Orbital> CREATOR
            = new Parcelable.Creator<Orbital>() {
        @Override
        public Orbital createFromParcel(Parcel source) {
            return new Orbital(
                    source.readInt(), source.readInt(),
                    source.readInt(), source.readInt(),
                    source.readInt() != 0, source.readInt() != 0);
        }
        @Override
        public Orbital[] newArray(int size) {
            return new Orbital[size];
        }
    };
}
