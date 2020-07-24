package com.dune.game.core.users_logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.dune.game.core.BattleMap;
import com.dune.game.core.Building;
import com.dune.game.core.GameController;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

import javax.imageio.stream.ImageOutputStream;

public class PlayerLogic extends BaseLogic {
    public PlayerLogic(GameController gc) {
        this.gc = gc;
        this.money = 1000;
        this.unitsCount = 10;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.PLAYER;
    }

    public void update(float dt) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            for (int i = 0; i < gc.getSelectedUnits().size(); i++) {
                AbstractUnit u = gc.getSelectedUnits().get(i);
                if (u.getOwnerType() == Owner.PLAYER) {
                    unitProcessing(u);
                }
            }
        }
    }

    public void unitProcessing(AbstractUnit unit) {
        if (unit.getUnitType() == UnitType.HARVESTER) {
            unit.commandMoveTo(gc.getMouse(), true);
            return;
        }
        if (unit.getUnitType() == UnitType.BATTLE_TANK) {
            AbstractUnit aiUnit = gc.getUnitsController().getNearestAiUnit(gc.getMouse());
            if (aiUnit != null) {
                unit.commandAttack(aiUnit);
                return;
            }

            int mouseCellX = (int) (gc.getMouse().x / BattleMap.CELL_SIZE);
            int mouseCellY = (int) (gc.getMouse().y / BattleMap.CELL_SIZE);
            Building b = gc.getMap().getBuildingFromCell(mouseCellX, mouseCellY);
            if (b != null) {
                unit.commandAttack(b);
                return;
            }
            unit.commandMoveTo(gc.getMouse(), true);

        }
    }
}
