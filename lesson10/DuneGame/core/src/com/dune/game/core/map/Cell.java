package com.dune.game.core.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.dune.game.core.Building;

public class Cell {
    public enum BlockType {
        GROUND, AIR, UNIT, BUILDING
    }

    Building buildingCore;
    Building buildingEntrance;
    int cellX, cellY;
    int resource;
    float resourceRegenerationRate;
    float resourceRegenerationTime;
    boolean groundPassable;
    boolean airPassable;
    boolean blockByTank;

    public Cell(int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
        this.groundPassable = true;
        this.airPassable = true;
        if (MathUtils.random() < 0.1f) {
            resource = MathUtils.random(1, 3);
        }
        resourceRegenerationRate = MathUtils.random(5.0f) - 4.5f;
        if (resourceRegenerationRate < 0.0f) {
            resourceRegenerationRate = 0.0f;
        } else {
            resourceRegenerationRate *= 20.0f;
            resourceRegenerationRate += 10.0f;
        }
    }

    public void update(float dt) {
        if (resourceRegenerationRate > 0.01f) {
            resourceRegenerationTime += dt;
            if (resourceRegenerationTime > resourceRegenerationRate) {
                resourceRegenerationTime = 0.0f;
                resource++;
                if (resource > 5) {
                    resource = 5;
                }
            }
        }
    }

    public void render(SpriteBatch batch, TextureRegion resourceTexture) {
        if (resource > 0) {
            float scale = 0.5f + resource * 0.1f;
            batch.draw(resourceTexture, cellX * BattleMap.CELL_SIZE, cellY * BattleMap.CELL_SIZE, BattleMap.CELL_SIZE / 2, BattleMap.CELL_SIZE / 2, BattleMap.CELL_SIZE, BattleMap.CELL_SIZE, scale, scale, 0.0f);
        } else {
            if (resourceRegenerationRate > 0.01f) {
                batch.draw(resourceTexture, cellX * BattleMap.CELL_SIZE, cellY * BattleMap.CELL_SIZE, BattleMap.CELL_SIZE / 2, BattleMap.CELL_SIZE / 2, BattleMap.CELL_SIZE, BattleMap.CELL_SIZE, 0.1f, 0.1f, 0.0f);
            }
        }
    }

    public void block(BlockType blockType) {
        switch (blockType) {
            case GROUND:
                groundPassable = false;
                resourceRegenerationRate = 0.0f;
                resource = 0;
                break;
            case AIR:
                airPassable = false;
                break;
            case BUILDING:
                groundPassable = false;
                resourceRegenerationRate = 0.0f;
                resource = 0;
                airPassable = false;
                break;
            case UNIT:
                blockByTank = true;
                break;
        }
    }

    public void unblock(BlockType blockType) {
        switch (blockType) {
            case GROUND:
                groundPassable = true;
                break;
            case AIR:
                airPassable = true;
                break;
            case BUILDING:
                groundPassable = true;
                airPassable = true;
                break;
            case UNIT:
                blockByTank = false;
                break;
        }
    }
}