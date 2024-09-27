package com.greenatom.navybattle;

import com.greenatom.navybattle.client.player.PlayerClient;
import com.greenatom.navybattle.client.player.placement.ShipPlacementManager;
import com.greenatom.navybattle.view.BattleView;
import com.greenatom.navybattle.view.ShipPlacementView;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        var client = new PlayerClient(
                new ShipPlacementView(10, List.of(1, 2, 3, 4)),
                new BattleView(10),
                new ShipPlacementManager(
                        10,
                        Map.of(
                                1, 4,
                                2, 3,
                                3, 2,
                                4, 1
                        )
                )
        );
        client.start();
    }
}