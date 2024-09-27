package com.greenatom.navybattle.ships;

public class MisplacedShipException extends Exception{
    public MisplacedShipException() {
    }

    public MisplacedShipException(String message) {
        super(message);
    }

    public MisplacedShipException(String message, Throwable cause) {
        super(message, cause);
    }

    public MisplacedShipException(Throwable cause) {
        super(cause);
    }
}
