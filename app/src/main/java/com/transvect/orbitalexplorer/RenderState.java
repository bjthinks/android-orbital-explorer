package com.transvect.orbitalexplorer;

import android.os.Parcel;
import android.os.Parcelable;

public class RenderState implements Parcelable {

    private Orbital orbital;
    private boolean orbitalChanged;
    private boolean color;
    private boolean colorChanged;

    public RenderState() {
        orbital = new Orbital(1, 1, 0, 0, false);
        orbitalChanged = true;
        color = true;
        colorChanged = true;
    }

    // Parcelable stuff
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        //out.writeParcelable(orbital, flags);
        out.writeInt(color ? 1 : 0);
    }

    public static final Parcelable.Creator<RenderState> CREATOR
            = new Parcelable.Creator<RenderState>() {
        @Override
        public RenderState createFromParcel(Parcel in) {
            RenderState result = new RenderState();
            //result.orbital = in.readParcelable(Orbital.class.getClassLoader());
            result.color = (in.readInt() != 0);
            return result;
        }
        @Override
        public RenderState[] newArray(int size) {
            return new RenderState[size];
        }
    };

    // Deep copy an old RenderState into a new one. We can't just overwrite the new
    // one in the caller, because a reference to it has already been given to the
    // rendering thread.
    public synchronized void copyStateFrom(RenderState old) {
        orbital = old.orbital;
        orbitalChanged = true;
        color = old.color;
        colorChanged = true;
    }

    // Main thread setters
    public synchronized void setOrbital(Orbital o) {
        orbital = o;
        orbitalChanged = true;
    }

    public synchronized void toggleColor() {
        color = !color;
        colorChanged = true;
    }

    // Render thread getter
    public synchronized FrozenState freeze() {
        FrozenState fs = new FrozenState();

        fs.orbital = orbital;
        fs.orbitalChanged = orbitalChanged;
        fs.color = color;
        fs.colorChanged = colorChanged;

        orbitalChanged = false;
        colorChanged = false;

        return fs;
    }

    static public class FrozenState {
        public Orbital orbital;
        public boolean orbitalChanged;
        public boolean color;
        public boolean colorChanged;
    }
}
