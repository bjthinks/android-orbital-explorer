package com.transvect.orbitalexplorer;

public class RenderState {

    private Orbital orbital;
    private boolean orbitalChanged;
    private boolean color;
    private boolean needToRender;

    public RenderState() {
        orbital = new Orbital(1, 1, 0, 0, false);
        orbitalChanged = true;
        color = true;
        needToRender = true;
    }

    public void setOrbital(Orbital o) {
        orbital = o;
        orbitalChanged = true;
        needToRender = true;
    }

    public void setColor(boolean c) {
        color = c;
        needToRender = true;
    }

    public FrozenState freeze() {
        return new FrozenState(this);
    }

    static public class FrozenState {
        public Orbital orbital;
        public boolean orbitalChanged;
        public boolean color;
        public boolean needToRender;

        public FrozenState(RenderState rs) {
            orbital = rs.orbital;
            orbitalChanged = rs.orbitalChanged;
            color = rs.color;
            needToRender = rs.needToRender;

            rs.orbitalChanged = false;
            rs.needToRender = false;
        }
    }
}
