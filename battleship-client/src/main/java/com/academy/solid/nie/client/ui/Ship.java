package com.academy.solid.nie.client.ui;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
class Ship {
    private int health;

    public List<Point2D> getPositions() {
        return positions;
    }

    private List<Point2D> positions;

    Ship(List<Point2D> positions) {
        this.positions = positions;
        this.health = getLength();
    }

    void hit(Point2D point2D) {
        health--;
    }

    boolean isAlive() {
        return health > 0;
    }

    int getLength() {
        return positions.size();
    }
}
