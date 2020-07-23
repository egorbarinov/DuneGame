package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.*;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.TargetType;
import com.dune.game.core.units.types.UnitType;
import com.dune.game.core.users_logic.BaseLogic;
import com.dune.game.screens.utils.Assets;

public abstract class AbstractUnit extends GameObject implements Poolable, Targetable {
    protected BaseLogic baseLogic;
    protected UnitType unitType;
    protected Owner ownerType;
    protected Weapon weapon;

    protected Vector2 destination;
    protected int realCellXDestination, realCellYDestination;
    protected TextureRegion[] textures;
    protected TextureRegion weaponTexture;

    protected TextureRegion progressbarTexture;
    protected int hp;
    protected int hpMax;
    protected float angle;
    protected float speed;
    protected float rotationSpeed;

    protected float moveTimer;
    protected float lifeTime;
    protected float timePerFrame;
    protected int container;
    protected int containerCapacity;

    protected Targetable target;
    protected float minDstToActiveTarget;

    public static int CORE_SIZE = 64;
    public static int CORE_SIZE_D2 = CORE_SIZE / 2;

    @Override
    public TargetType getTargetType() {
        return TargetType.UNIT;
    }

    public BaseLogic getBaseLogic() {
        return baseLogic;
    }

    public boolean takeDamage(int damage) {
        if (!isActive()) {
            return false;
        }
        hp -= damage;
        if (hp <= 0) {
            return true;
        }
        return false;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void moveBy(Vector2 value) {
        boolean stayStill = false;
        if (position.dst(destination) < 3.0f) {
            stayStill = true;
        }
        tmp.set(position).add(value);

        if (!gc.getMap().isCellGroundPassable(tmp)) { // запрещаем толкать танки за пределы экрана
            return;
        }
        position.add(value);
        if (stayStill) {
            destination.set(position);
        }
    }

    public Owner getOwnerType() {
        return ownerType;
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public AbstractUnit(GameController gc) {
        super(gc);
        this.progressbarTexture = Assets.getInstance().getAtlas().findRegion("progressbar");
        this.timePerFrame = 0.08f;
        this.rotationSpeed = 90.0f;
        this.destination = new Vector2();
    }

    public abstract void setup(BaseLogic baseLogic, float x, float y);

    private int getCurrentFrameIndex() {
        return (int) (moveTimer / timePerFrame) % textures.length;
    }

    public Vector2 getDestination() { ///////////////////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!
        return destination;
    }

    public void update(float dt) {
        lifeTime += dt;
        if (target != null) {
            commandMoveTo(target.getPosition(), false);
            if (position.dst(target.getPosition()) < minDstToActiveTarget) {
                commandMoveTo(position, false);
            }
        }
        if (target == null) {
            weapon.setAngle(rotateTo(weapon.getAngle(), angle, 180.0f, dt));
        }
        updateWeapon(dt);
        if (position.dst(destination) > 3.0f) {
            float angleTo = tmp.set(destination).sub(position).angle();
            angle = rotateTo(angle, angleTo, rotationSpeed, dt);
            moveTimer += dt;

            if (gc.getMap().getResourceCount(position) > 0) {
                for (int i = 0; i < gc.getMap().getResourceCount(position); i++) {
                    gc.getParticleController().setup(MathUtils.random(getCellX() * BattleMap.CELL_SIZE, getCellX() * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE), MathUtils.random(getCellY() * BattleMap.CELL_SIZE, getCellY() * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE), MathUtils.random(-20, 20), MathUtils.random(-20, 20), 0.3f, 0.5f, 0.4f,
                            0, 0, 1, 0.1f, 1, 1, 1, 0.4f);
                }
            }

            tmp.set(speed, 0).rotate(angle);
            position.mulAdd(tmp, dt);
            if ((position.dst(destination) < 120.0f && Math.abs(angleTo - angle) > 10)) {
                position.mulAdd(tmp, -dt);
            }

            if (!gc.getMap().isCellGroundPassable(position)) { // запрет на смещение за пределы карты
                tmp.set(position).sub(getCellX() * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, getCellY() * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2).nor().scl(2);
                position.add(tmp);
            }
        } else {
            if (getCellX() == realCellXDestination && getCellY() == realCellYDestination) {
                return;
            }
            gc.getPathFinder().buildRoute(getCellX(), getCellY(), realCellXDestination, realCellYDestination, destination);
        }

        checkBounds();
    }

    public void commandMoveTo(Vector2 point, boolean resetTarget) {
        int cellPointX = (int) (point.x / BattleMap.CELL_SIZE);
        int cellPointY = (int) (point.y / BattleMap.CELL_SIZE);
        realCellXDestination = cellPointX;
        realCellYDestination = cellPointY;
        gc.getPathFinder().buildRoute(getCellX(), getCellY(), realCellXDestination, realCellYDestination, destination);
        if (resetTarget) {
            target = null;
        }
    }

    public abstract void commandAttack(Targetable target);

    public abstract void updateWeapon(float dt);

    public void checkBounds() {
        if (position.x < CORE_SIZE_D2) {
            position.x = CORE_SIZE_D2;
        }
        if (position.y < CORE_SIZE_D2) {
            position.y = CORE_SIZE_D2;
        }
        if (position.x > BattleMap.MAP_WIDTH_PX - CORE_SIZE_D2) {
            position.x = BattleMap.MAP_WIDTH_PX - CORE_SIZE_D2;
        }
        if (position.y > BattleMap.MAP_HEIGHT_PX - CORE_SIZE_D2) {
            position.y = BattleMap.MAP_HEIGHT_PX - CORE_SIZE_D2;
        }
    }

    public void render(SpriteBatch batch) {
        float c = 1.0f;
        float r = 0.0f;
        if (gc.isUnitSelected(this)) {
            c = 0.7f + (float) Math.sin(lifeTime * 8.0f) * 0.3f;
        }
        if (ownerType == Owner.AI) {
            r = 0.4f;
        }
        batch.setColor(c, c - r, c - r, 1.0f);
        batch.draw(textures[getCurrentFrameIndex()], position.x - CORE_SIZE_D2, position.y - CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE, CORE_SIZE, 1, 1, angle);

        batch.draw(weaponTexture, position.x - CORE_SIZE_D2, position.y - CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE_D2, CORE_SIZE, CORE_SIZE, 1, 1, weapon.getAngle());

        batch.setColor(1, 1, 1, 1);
        renderGui(batch);
    }

    public void renderGui(SpriteBatch batch) {
        if (hp < hpMax) {
            batch.setColor(0.2f, 0.2f, 0.0f, 1.0f);
            batch.draw(progressbarTexture, position.x - 32, position.y + 30, 64, 12);
            batch.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            float percentage = (float) hp / hpMax;
            batch.draw(progressbarTexture, position.x - 30, position.y + 32, 60 * percentage, 8);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public float rotateTo(float srcAngle, float angleTo, float rSpeed, float dt) {
        if (Math.abs(srcAngle - angleTo) > 3.0f) {
            if ((srcAngle > angleTo && Math.abs(srcAngle - angleTo) <= 180.0f) || (srcAngle < angleTo && Math.abs(srcAngle - angleTo) > 180.0f)) {
                srcAngle -= rSpeed * dt;
            } else {
                srcAngle += rSpeed * dt;
            }
        }
        if (srcAngle < 0.0f) {
            srcAngle += 360.0f;
        }
        if (srcAngle > 360.0f) {
            srcAngle -= 360.0f;
        }
        return srcAngle;
    }
}