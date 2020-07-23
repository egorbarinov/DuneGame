package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dune.game.core.*;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.units.types.UnitType;
import com.dune.game.core.users_logic.BaseLogic;
import com.dune.game.screens.utils.Assets;

public class Harvester extends AbstractUnit {
    public Harvester(GameController gc) {
        super(gc);
        this.textures = Assets.getInstance().getAtlas().findRegion("tankcore").split(CORE_SIZE, CORE_SIZE)[0];
        this.weaponTexture = Assets.getInstance().getAtlas().findRegion("harvester");
        this.containerCapacity = 3;
        this.minDstToActiveTarget = 5.0f;
        this.speed = 60.0f;
        this.rotationSpeed = 60.0f;
        this.weapon = new Weapon(4.0f, 1);
        this.hpMax = 500;
        this.unitType = UnitType.HARVESTER;
    }

    @Override
    public void setup(BaseLogic baseLogic, float x, float y) {
        this.position.set(x, y);
        this.baseLogic = baseLogic;
        this.ownerType = baseLogic.getOwnerType(); // запрашиваем тип харвестера у baseLogic
        this.hp = this.hpMax;
        this.destination.set(position);
        this.commandMoveTo(this.position, true);
    }

    public void updateWeapon(float dt) {
        if (gc.getMap().getResourceCount(position) > 0 && container < containerCapacity) {
            int result = weapon.use(dt);
            if (result > -1) {
                container += gc.getMap().harvestResource(position, result);
                if (container > containerCapacity) {
                    container = containerCapacity;
                }
            }
        } else {
            weapon.reset();
        }
    }

    @Override
    public void commandAttack(Targetable target) {
        commandMoveTo(target.getPosition(), true);
    }

    @Override
    public void renderGui(SpriteBatch batch) {
        super.renderGui(batch);
        if (container > 0) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 22, 64, 8);
            batch.setColor(1.0f, 1.0f, 0.0f, 1.0f);
            float containerPercentage = (float)container / containerCapacity;
            batch.draw(progressbarTexture, position.x - 30, position.y + 24, 60 * containerPercentage, 4);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public void update(float dt) {
        super.update(dt);
        Building b = gc.getMap().getBuildingEntrance(getCellX(), getCellY());
        if (b != null && b.getBuildingType() == Building.Type.STOCK && b.getOwnerLogic() == this.baseLogic) {
            baseLogic.addMoney(container * 100);
            container = 0;
        }
    }

    public boolean isFullContainer() { // проверка на заполненность контейнера
        return container == containerCapacity;
    }
}