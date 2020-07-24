package com.dune.game.core.map;

import com.badlogic.gdx.math.Vector2;

public interface GameMap {
    int getSizeX();
    int getSizeY();
    boolean isCellPassable(int cellX, int cellY, boolean isFlyable);
    boolean isCellPassable(Vector2 position, boolean isFlyable);
    int getCellCost(int cellX, int cellY);
}
