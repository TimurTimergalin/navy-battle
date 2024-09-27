package com.greenatom.navybattle.view.components;

public interface Component {
    int getTop();

    int getLeft();

    int getWidth();

    int getHeight();

    default int getRight() {
        return getLeft() + getWidth() - 1;
    }

    default int getBottom() {
        return getTop() + getHeight() - 1;
    }
}
