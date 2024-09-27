package com.greenatom.navybattle.view.components;

import java.util.ArrayList;
import java.util.List;

public class Chat implements Component{
    private final List<TextLine> textLines;

    public Chat(int originX, int originY, int size) {
        textLines = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            textLines.add(new TextLine(originX, originY + i));
        }
    }

    public void addMessage(String text) {
        for (int i = 1; i < textLines.size(); ++i) {
            textLines.get(i - 1).setText(textLines.get(i).getText());
        }
        textLines.getLast().setText(text);
    }

    @Override
    public int getTop() {
        return textLines.getFirst().getTop();
    }

    @Override
    public int getLeft() {
        return textLines.getFirst().getLeft();
    }

    @Override
    public int getWidth() {
        return textLines.stream().map(TextLine::getWidth).mapToInt(Integer::intValue).max().orElse(0);
    }

    @Override
    public int getHeight() {
        return textLines.size();
    }
}
