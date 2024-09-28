package com.greenatom.navybattle;

import com.greenatom.navybattle.client.Client;
import com.greenatom.navybattle.client.PlayerClient;
import com.greenatom.navybattle.client.placement.ShipPlacementManager;
import com.greenatom.navybattle.ships.Ship;
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
                )
        );
        client.start();

        Ship.Coordinates cords = client.requestShot();
        client.announceAlliedTurn(cords.x(), cords.y(), Client.ShotStatus.MISS);
        client.registerAlliedShot(cords.x(), cords.y(), Client.ShotStatus.MISS);
    }
}