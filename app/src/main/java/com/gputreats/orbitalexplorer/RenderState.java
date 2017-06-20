package com.gputreats.orbitalexplorer;

class RenderState {

    private OrbitalView orbitalView;

    Orbital orbital;

    RenderState() {
        orbital = new Orbital(1, 4, 2, 1, false, true);
    }

    // This happens BEFORE the render thread starts up
    synchronized void setOrbitalView(OrbitalView ov) {
        orbitalView = ov;
    }

    synchronized Orbital getOrbital() {
        return orbital;
    }

    synchronized void setOrbital(Orbital newOrbital) {
        if (newOrbital.notEquals(orbital)) {
            orbital = newOrbital;
            if (orbitalView != null) {
                orbitalView.setOrbital(newOrbital);
            }
        }
    }
}
