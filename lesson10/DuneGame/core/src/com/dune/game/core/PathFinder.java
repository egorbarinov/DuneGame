package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PathFinder {
    private static class CellNode implements Comparable<CellNode> {
        private int x, y;
        private CellNode from;
        private int cost, priority;
        private boolean passable;
        private boolean processed;
        private List<CellNode> neighbors;

        public CellNode(int x, int y) {
            this.x = x;
            this.y = y;
            this.neighbors = new ArrayList<CellNode>();
            this.passable = true;
        }

        @Override
        public int compareTo(CellNode o) {
            return this.priority - o.priority;
        }
    }

    private GameMap gameMap;
    private CellNode[][] nodes;

    public PathFinder(GameMap gameMap) {
        this.gameMap = gameMap;
        this.nodes = new CellNode[gameMap.getSizeX()][gameMap.getSizeY()];
        for (int i = 0; i < gameMap.getSizeX(); i++) {
            for (int j = 0; j < gameMap.getSizeY(); j++) {
                this.nodes[i][j] = new CellNode(i, j);
            }
        }
    }

    public void buildRoute(int srcX, int srcY, int dstX, int dstY, Vector2 newDst) {
        for (int i = 0; i < gameMap.getSizeX(); i++) {
            for (int j = 0; j < gameMap.getSizeY(); j++) {
                this.nodes[i][j].neighbors.clear();
                this.nodes[i][j].processed = false;
                this.nodes[i][j].from = null;
                this.nodes[i][j].cost = 0;
                this.nodes[i][j].priority = 0;

                for (int k = i - 1; k <= i + 1; k++) {
                    for (int l = j - 1; l <= j + 1; l++) {
                        if (k < 0 || l < 0 || k >= gameMap.getSizeX() || l >= gameMap.getSizeY() || (k == i && l == j)) {
                            continue;
                        }
                        if (gameMap.isCellPassable(k, l, false)) {
                            this.nodes[i][j].neighbors.add(this.nodes[k][l]);
                        }
                    }
                }
//                if (i > 0 && gameMap.isCellPassable(i - 1, j, false)) {
//                    this.nodes[i][j].neighbors.add(this.nodes[i - 1][j]);
//                }
//                if (i < gameMap.getSizeX() - 1 && gameMap.isCellPassable(i + 1, j, false)) {
//                    this.nodes[i][j].neighbors.add(this.nodes[i + 1][j]);
//                }
//                if (j > 0 && gameMap.isCellPassable(i, j - 1, false)) {
//                    this.nodes[i][j].neighbors.add(this.nodes[i][j - 1]);
//                }
//                if (j < gameMap.getSizeY() - 1 && gameMap.isCellPassable(i, j + 1, false)) {
//                    this.nodes[i][j].neighbors.add(this.nodes[i][j + 1]);
//                }
            }
        }

        nodes[srcX][srcY].from = null;
        nodes[srcX][srcY].cost = 0;

        PriorityQueue<CellNode> frontier = new PriorityQueue<CellNode>(1000);
        frontier.add(nodes[srcX][srcY]);

        while (!frontier.isEmpty()) {
            CellNode current = frontier.poll();
            current.processed = true;

            if (current.x == dstX && current.y == dstY) {
                break;
            }

            for (int i = 0; i < current.neighbors.size(); i++) {
                CellNode next = current.neighbors.get(i);
                int newCost = current.cost + gameMap.getCellCost(next.x, next.y);
                if (current.x != next.x && current.y != next.y) {
                    newCost++; // todo проверить бОльшую стоимость диагоналей
                }

                if (!next.processed && (next.from == null || newCost < next.cost)) {
                    next.cost = newCost;
                    next.priority = newCost + (Math.abs(next.x - dstX) + Math.abs(next.y - dstY)) * 1;
                    next.from = current;
                    if (!frontier.contains(next)) {
                        frontier.add(next);
                    }
                }
            }
        }

        CellNode out = nodes[dstX][dstY];
        if (out == null) {
            return;
        }
        if (out.from == null) {
            newDst.set(out.x * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, out.y * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2);
            return;
        }

        while (out.from != nodes[srcX][srcY]) {
            out = out.from;
        }
        newDst.set(out.x * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, out.y * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2);
    }
}