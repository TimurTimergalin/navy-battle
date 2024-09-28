package com.greenatom.navybattle.controller;

import com.greenatom.navybattle.client.Client;
import com.greenatom.navybattle.client.ShotStatus;
import com.greenatom.navybattle.ships.Coordinates;
import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;

import java.util.function.Function;

public class GameController {
    private final Client player1;
    private final Client player2;
    private final Function<ShipPlacement, ShotTracker> shotTrackerFactory;

    private Actor active;
    private Actor passive;

    public GameController(Client player1, Client player2, Function<ShipPlacement, ShotTracker> shotTrackerFactory) {
        this.player1 = player1;
        this.player2 = player2;
        this.shotTrackerFactory = shotTrackerFactory;
    }

    private void start() {
        active = new Actor(player1, shotTrackerFactory.apply(player1.start()));
        passive = new Actor(player2, shotTrackerFactory.apply(player2.start()));
    }

    private void swapActors() {
        Actor tmp = passive;
        passive = active;
        active = tmp;
    }

    // Возвращает true, если надо продолжать ход
    private boolean makeTurn() {
        Coordinates turn = active.client().requestShot();

        Client.UserError error = passive.field().checkShot(turn);
        while (error != null) {
            active.client().reportUserError(error);
            turn = active.client().requestShot();
            error = passive.field().checkShot(turn);
        }

        ShotStatus status = passive.field().makeShot(turn);
        active.client().announceAlliedTurn(turn.x(), turn.y(), status);
        active.client().registerAlliedShot(turn.x(), turn.y(), status);
        passive.client().announceEnemyTurn(turn.x(), turn.y(), status);
        passive.client().registerEnemyShot(turn.x(), turn.y(), status);

        if (status == ShotStatus.KILL) {
            Ship killed = passive.field().getShipAt(turn);

            killed.getTiles().forEach(
                    tile -> tile
                            .getNeighbors()
                            .filter(neighbor -> passive.field().checkShot(neighbor) == null)
                            .forEach(
                                    neighbor -> {
                                        active.client().registerAlliedShot(neighbor.x(), neighbor.y(), ShotStatus.MISS);
                                        passive.client().registerEnemyShot(neighbor.x(), neighbor.y(), ShotStatus.MISS);
                                        passive.field().makeShot(neighbor);
                                    }
                            )
            );
        }

        if (passive.field().isDead()) {
            active.client().declareVictory();
            passive.client().declareDefeat();
            return false;
        }

        if (status == ShotStatus.MISS) {
            swapActors();
        }
        return true;
    }

    public void run() {
        start();
        //noinspection StatementWithEmptyBody
        while (makeTurn()) {}
    }
}
