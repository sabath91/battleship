package com.epam.solid.nie.ships;

/**
 * Created by marek on 13.12.2017.
 */
public interface ShipFactory {
    BattleShip createShip(BattleShipType type);
}
