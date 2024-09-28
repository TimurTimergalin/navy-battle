package com.greenatom.navybattle.client.player;

import com.greenatom.navybattle.ships.Ship;
import com.greenatom.navybattle.ships.ShipPlacement;

public interface PlayerCommandParser {
    Ship.Coordinates parseCoordinates(String s);
    ShipPlacement parseCommand(String command);
}
