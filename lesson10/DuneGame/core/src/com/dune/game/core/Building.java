package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.units.types.TargetType;
import com.dune.game.core.users_logic.BaseLogic;
import com.dune.game.screens.utils.Assets;

public class Building extends GameObject implements Poolable, Targetable {
    public enum Type {
        STOCK
    }

    // * * *
    // * P *
    //   E
    private BaseLogic ownerLogic;
    private Type buildingType;
    private TextureRegion texture;
    private TextureRegion progressbarTexture;
    private Vector2 textureWorldPosition;
    private Vector2 entrancePosition; // координаты входа в строение
    private int hpMax;
    private int hp;

    @Override
    public int getCellX() {
        return cellX;
    }

    @Override
    public int getCellY() {
        return cellY;
    }

    private int cellX, cellY;

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public Type getBuildingType() {
        return buildingType;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BUILDING;
    }

    public BaseLogic getOwnerLogic() {
        return ownerLogic;
    }

    public Building(GameController gc) {
        super(gc);
        this.texture = Assets.getInstance().getAtlas().findRegion("grass");
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.textureWorldPosition = new Vector2(); // координаты нижнего угла здания
        this.entrancePosition = new Vector2();
    }

    public Vector2 getEntrancePosition() { // возвращает координаты входа в здание
        return entrancePosition;
    }

    public void setup(BaseLogic ownerLogic, int cellX, int cellY) {
        this.ownerLogic = ownerLogic;  // определяем, что здание может принадлежать либо player либо ai
        this.position.set(cellX * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, cellY * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2); // координаты нижнего угла здания
        this.cellX = cellX; // координаты входа по X
        this.cellY = cellY; // координаты входа по Y
        this.hpMax = 500;
        this.hp = this.hpMax;
        this.textureWorldPosition.set((cellX - 1) * BattleMap.CELL_SIZE, cellY * BattleMap.CELL_SIZE);
        this.buildingType = Type.STOCK;
        gc.getMap().setupBuilding(cellX - 1, cellY, cellX + 1, cellY + 1, cellX, cellY - 1, this); // блокировка проходимости по воздуху и по земле
        this.entrancePosition.set(((float)cellX + 0.5f) * BattleMap.CELL_SIZE, ((float) cellY - 0.5f) * BattleMap.CELL_SIZE);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(0.5f, 0.5f, 0.5f, 0.6f);
        batch.draw(texture, textureWorldPosition.x, textureWorldPosition.y, BattleMap.CELL_SIZE * 3, BattleMap.CELL_SIZE * 2); // отрисовываем здание
        batch.setColor(0.5f, 0.2f, 0.2f, 0.8f);
        batch.draw(texture, textureWorldPosition.x + BattleMap.CELL_SIZE, textureWorldPosition.y - BattleMap.CELL_SIZE, BattleMap.CELL_SIZE, BattleMap.CELL_SIZE); // отрисовка входа
        batch.setColor(1, 1, 1, 1);

        if (hp < hpMax) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 30, 64, 12);
            batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            float percentage = (float) hp / hpMax;
            batch.draw(progressbarTexture, position.x - 30, position.y + 32, 60 * percentage, 8);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public void update(float dt) {
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) {
            destroy();
        }
    }

    public void destroy() {
        gc.getMap().setupBuilding(cellX - 1, cellY, cellX + 1, cellY + 1, cellX, cellY - 1, null);
    }
}