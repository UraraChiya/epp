package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.style.Blitter;

public class ActionEPPButton extends EPPButton {

    private final Blitter icon;

    public ActionEPPButton(OnPress onPress, Blitter icon) {
        super(onPress);
        this.icon = icon;
    }

    @Override
    Blitter getBlitterIcon() {
        return icon;
    }

}
