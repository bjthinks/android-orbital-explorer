package com.transvect.orbitalexplorer;

public class RenderState {

    private boolean color;
    private boolean needToRender;

    public RenderState() {
        color = true;
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
        public boolean color;
        public boolean needToRender;

        public FrozenState(RenderState rs) {
            color = rs.color;
            needToRender = rs.needToRender;
            rs.needToRender = false;
        }
    }
}
