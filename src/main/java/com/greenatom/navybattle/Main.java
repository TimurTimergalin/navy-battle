package com.greenatom.navybattle;

import com.greenatom.navybattle.client.bot.AutoShipPlacer;
import com.greenatom.navybattle.client.bot.BotClient;
import com.greenatom.navybattle.client.bot.BotIntellect;
import com.greenatom.navybattle.client.player.PlayerClient;
import com.greenatom.navybattle.client.placement.ShipPlacementManager;
import com.greenatom.navybattle.controller.GameController;
import com.greenatom.navybattle.controller.ShotTracker;
import com.greenatom.navybattle.view.BattleView;
import com.greenatom.navybattle.view.ShipPlacementView;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int size = 10;
        var availableShipSizes = Map.of(
                1, 4,
                2, 3,
                3, 2,
                4, 1
        );

        new GameController(
                new PlayerClient(
                        new ShipPlacementView(size, availableShipSizes.keySet().stream().sorted().toList()),
                        new BattleView(size),
                        new ShipPlacementManager(size, availableShipSizes),
                        c -> c.new Parser()
                ),
                new BotClient(
                        new AutoShipPlacer(
                                new ShipPlacementManager(size, availableShipSizes),
                                availableShipSizes.keySet().stream().sorted().toList(),
                                size
                        ),
                        new BotIntellect(size)
                ),
                placement -> new ShotTracker(placement, size)
        ).run();
    }
}