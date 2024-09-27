package com.greenatom.navybattle;

import com.greenatom.navybattle.view.ShipPlacementView;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        new ShipPlacementView(10, List.of(1, 2, 3, 4)).draw();
    }
}