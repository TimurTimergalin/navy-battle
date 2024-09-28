package com.greenatom.navybattle.client.player;

import com.greenatom.navybattle.ships.Coordinates;
import com.greenatom.navybattle.ships.ShipPlacement;

public interface PlayerCommandParser {
    Coordinates parseCoordinates(String s);
    ShipPlacement parseCommand(String command);
}
