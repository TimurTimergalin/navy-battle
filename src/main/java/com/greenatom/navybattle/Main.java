package com.greenatom.navybattle;

import com.greenatom.navybattle.client.ShotStatus;
import com.greenatom.navybattle.client.player.PlayerClient;
import com.greenatom.navybattle.client.placement.ShipPlacementManager;
import com.greenatom.navybattle.ships.Coordinates;
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
                                1, 4
                        )
                ),
                c -> c.new Parser()
        );
        client.start();

        Coordinates cords = client.requestShot();
        client.announceAlliedTurn(cords.x(), cords.y(), ShotStatus.MISS);
        client.registerAlliedShot(cords.x(), cords.y(), ShotStatus.MISS);
    }
}