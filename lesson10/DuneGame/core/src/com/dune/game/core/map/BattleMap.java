package com.dune.game.core.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.Building;
import com.dune.game.screens.utils.Assets;

public class BattleMap implements GameMap {
    public static final int COLUMNS_COUNT = 24;
    public static final int ROWS_COUNT = 14;
    public static final int CELL_SIZE = 60;
    public static final int MAP_WIDTH_PX = COLUMNS_COUNT * CELL_SIZE;
    public static final int MAP_HEIGHT_PX = ROWS_COUNT * CELL_SIZE;

    @Override
    public int getSizeX() {
        return COLUMNS_COUNT;
    }

    @Override
    public int getSizeY() {
        return ROWS_COUNT;
    }

    public Building getBuildingFromCell(int cellX, int cellY) {
        if (cellX < 0 || cellY < 0 || cellX >= COLUMNS_COUNT || cellY >= ROWS_COUNT) {
            return null;
        }
        return cells[cellX][cellY].buildingCore;
    }

    @Override
    public boolean isCellPassable(int cellX, int cellY, boolean isFlyable) {
        if (cellX < 0 || cellY < 0 || cellX >= COLUMNS_COUNT || cellY >= ROWS_COUNT) {
            return false;
        }
        if (cells[cellX][cellY].groundPassable && !cells[cellX][cellY].blockByTank) {
            return true;
        }
        if (cells[cellX][cellY].airPassable && isFlyable) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCellPassable(Vector2 position, boolean isFlyable) {
        int cellX = (int)(position.x / BattleMap.CELL_SIZE);
        int cellY = (int)(position.y / BattleMap.CELL_SIZE);
        return isCellPassable(cellX, cellY, isFlyable);
    }

    @Override
    public int getCellCost(int cellX, int cellY) {
        return 1;
    }

    private TextureRegion grassTexture;
    private TextureRegion resourceTexture;
    private Cell[][] cells;

    public void blockCell(int cellX, int cellY, Cell.BlockType blockType) {
        cells[cellX][cellY].block(blockType);
    }

    public void unblockCell(int cellX, int cellY, Cell.BlockType blockType) {
        cells[cellX][cellY].unblock(blockType);
    }

    public void setupBuilding(int startX, int startY, int endX, int endY, int entranceX, int entranceY, Building building) {
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                cells[i][j].buildingCore = building;
                if (building != null) {
                    blockCell(i, j, Cell.BlockType.BUILDING);
                } else {
                    unblockCell(i, j, Cell.BlockType.BUILDING);
                }
            }
        }
        cells[entranceX][entranceY].buildingEntrance = building;
    }

    public BattleMap() {
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.resourceTexture = Assets.getInstance().getAtlas().findRegion("resr");
        this.cells = new Cell[COLUMNS_COUNT][ROWS_COUNT];
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public int getResourceCount(Vector2 point) {
        int cx = (int) (point.x / CELL_SIZE);
        int cy = (int) (point.y / CELL_SIZE);
        return cells[cx][cy].resource;
    }

    public int harvestResource(Vector2 point, int power) {
        int value = 0;
        int cx = (int) (point.x / CELL_SIZE);
        int cy = (int) (point.y / CELL_SIZE);
        if (cells[cx][cy].resource >= power) {
            value = power;
            cells[cx][cy].resource -= power;
        } else {
            value = cells[cx][cy].resource;
            cells[cx][cy].resource = 0;
        }
        return value;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                if (cells[i][j].groundPassable) {
                    if(cells[i][j].blockByTank) {
                        batch.setColor(1,0,0,1);
                    }
                    batch.draw(grassTexture, i * CELL_SIZE, j * CELL_SIZE);
                    batch.setColor(1,1,1,1);
                }
                cells[i][j].render(batch, resourceTexture);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j].update(dt);
            }
        }
    }

    public Building getBuildingEntrance(int cellX, int cellY) {
        return cells[cellX][cellY].buildingEntrance;
    }
}