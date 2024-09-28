package com.greenatom.navybattle.client.bot;

import com.greenatom.navybattle.client.Client;
import com.greenatom.navybattle.client.ShotStatus;
import com.greenatom.navybattle.ships.Coordinates;
import com.greenatom.navybattle.ships.ShipPlacement;

public class BotClient implements Client {
    private final AutoShipPlacer autoShipPlacer;
    private final BotIntellect intellect;
    private final int turnDelayMils;

    public BotClient(AutoShipPlacer autoShipPlacer, BotIntellect intellect, int turnDelayMils) {
        this.autoShipPlacer = autoShipPlacer;
        this.intellect = intellect;
        this.turnDelayMils = turnDelayMils;
    }

    @Override
    public ShipPlacement start() {
        return autoShipPlacer.generatePlacement();
    }

    // Бот никак не реагирует на сообщения - у него нет вывода
    @Override
    public void announceAlliedTurn(int x, int y, ShotStatus shotStatus) {}

    @Override
    public void announceEnemyTurn(int x, int y, ShotStatus shotStatus) {}

    @Override
    public void declareVictory() {}

    @Override
    public void declareDefeat() {}

    // Боту все равно на ходы игрока - на его стратегию это никак не влияет
    @Override
    public void registerEnemyShot(int x, int y, ShotStatus shotStatus) {}

    @Override
    public void registerAlliedShot(int x, int y, ShotStatus shotStatus) {
        intellect.react(x, y, shotStatus);
    }

    @Override
    public Coordinates requestShot() throws InterruptedException {
        Thread.sleep(turnDelayMils);
        return intellect.makeMove();
    }

    @Override
    public void reportUserError(UserError error) {
        throw new RuntimeException("BotClient have made an invalid move, produced error: " + error.name());
    }
}
