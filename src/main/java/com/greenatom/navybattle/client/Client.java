package com.greenatom.navybattle.client;

import com.greenatom.navybattle.ships.Coordinates;
import com.greenatom.navybattle.ships.ShipPlacement;

public interface Client {

    ShipPlacement start();

    void announceAlliedTurn(int x, int y, ShotStatus shotStatus);
    void announceEnemyTurn(int x, int y, ShotStatus shotStatus);

    void registerAlliedShot(int x, int y, ShotStatus shotStatus);
    void registerEnemyShot(int x, int y, ShotStatus shotStatus);

    void declareVictory();
    void declareDefeat();

    Coordinates requestShot();

    enum UserError {
        COORDINATES_OUT_OF_BOUNDS, REPEATED_MOVE
    }

    void reportUserError(UserError error);
}
