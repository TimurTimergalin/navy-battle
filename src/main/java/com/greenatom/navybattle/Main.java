package com.greenatom.navybattle;

import com.greenatom.navybattle.view.components.TextLine;
import com.greenatom.navybattle.view.components.field.Field;
import com.greenatom.navybattle.view.components.field.TileStatus;
import com.greenatom.navybattle.view.utils.Printer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        new Printer().clearAll().flush();
        new Field(4, 4)
                .draw()
                .changeStatus(1, 1, TileStatus.VERTICAL)
                .changeStatus(2, 1, TileStatus.VERTICAL)
                .changeStatus(3, 1, TileStatus.HIT)
                .changeStatus(5, 8, TileStatus.MISS)
                .changeStatus(7, 3, TileStatus.HORIZONTAL)
                .changeStatus(7, 4, TileStatus.HORIZONTAL)
                .changeStatus(7, 5, TileStatus.HORIZONTAL)
                .changeStatus(7, 6, TileStatus.HORIZONTAL);

        var textLine = new TextLine(6, 35, "Some long text");
        Thread.sleep(2000);
        textLine.setText("Short text");

        new Printer().goTo(20, 1).flush();
    }
}