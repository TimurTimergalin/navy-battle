package com.greenatom.navybattle;

import com.greenatom.navybattle.view.components.InputLine;
import com.greenatom.navybattle.view.components.TextLine;
import com.greenatom.navybattle.view.components.field.Field;
import com.greenatom.navybattle.view.components.field.TileStatus;
import com.greenatom.navybattle.view.utils.Printer;

public class Main {
    public static void main(String[] args) {
        new Printer().clearAll().flush();
        var field = new Field(4, 4)
                .draw()
                .changeStatus(1, 1, TileStatus.HORIZONTAL)
                .changeStatus(2, 1, TileStatus.HORIZONTAL)
                .changeStatus(3, 1, TileStatus.HIT)
                .changeStatus(5, 8, TileStatus.MISS)
                .changeStatus(7, 3, TileStatus.VERTICAL)
                .changeStatus(7, 4, TileStatus.VERTICAL)
                .changeStatus(7, 5, TileStatus.VERTICAL)
                .changeStatus(7, 6, TileStatus.VERTICAL);

        var inputLabel = new TextLine(field.getRight() + 2, field.getTop(), "Type something: ");
        var output = new TextLine(inputLabel.getLeft(), inputLabel.getBottom() + 1);
        var input = new InputLine(inputLabel.getRight() + 1, inputLabel.getTop());
        output.setText(input.getInput());

        new Printer().goTo(20, 1).flush();
    }
}