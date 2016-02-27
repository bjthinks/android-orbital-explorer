package com.transvect.orbitalexplorer;

public class RenderState {

    private Orbital orbital;
    private boolean orbitalChanged;
    private boolean color;
    private boolean colorChanged;
    private boolean needToRender;

    public RenderState() {
        orbital = new Orbital(1, 1, 0, 0, false);
        orbitalChanged = true;
        color = true;
        colorChanged = true;
        needToRender = true;
    }

    // Main thread setters
    public synchronized void setOrbital(Orbital o) {
        orbital = o;
        orbitalChanged = true;
        needToRender = true;
    }

    public synchronized void setColor(boolean c) {
        color = c;
        colorChanged = true;
        needToRender = true;
    }

    // Render thread getter
    public synchronized FrozenState freeze() {
        FrozenState fs = new FrozenState();

        fs.orbital = orbital;
        fs.orbitalChanged = orbitalChanged;
        fs.color = color;
        fs.colorChanged = colorChanged;
        fs.needToRender = needToRender;

        orbitalChanged = false;
        colorChanged = false;
        needToRender = false;

        return fs;
    }

    static public class FrozenState {
        public Orbital orbital;
        public boolean orbitalChanged;
        public boolean color;
        public boolean colorChanged;
        public boolean needToRender;
    }
}
