package com.transvect.orbitalexplorer;

public class RenderState {

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

    // Main thread setters
    public synchronized void setOrbital(Orbital o) {
        orbital = o;
        orbitalChanged = true;
    }

    public synchronized void setColor(boolean c) {
        color = c;
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
