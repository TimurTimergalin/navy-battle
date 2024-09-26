package com.greenatom.navybattle.view.components;

import com.greenatom.navybattle.view.utils.Printer;

public class TextLine implements Component {
    private final int startX;
    private final int startY;

    private int size = 0;

    public TextLine(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public TextLine(int startX, int startY, String text) {
        this(startX, startY);
        setText(text);
    }

    private void clear(Printer printer) {
        printer.goTo(startY, startX);

        for (int i = 0; i < size; i++) {
            printer.text(" ");
        }
        size = 0;
    }

    public void setText(String text) {
        var printer = new Printer();
        clear(printer);
        printer.goTo(startY, startX).text(text);
        size = text.length();
    }

    @Override
    public int getTop() {
        return startY;
    }

    @Override
    public int getLeft() {
        return startX;
    }

    @Override
    public int getWidth() {
        return size;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}
