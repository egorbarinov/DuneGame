package com.dune.game.core.users_logic;

import com.dune.game.core.GameController;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

import java.util.ArrayList;
import java.util.List;

public class AiLogic extends BaseLogic {
    private String name;
    private float timer;

    private List<BattleTank> tmpMyBattleTanks;
    private List<Harvester> tmpPlayerHarvesters;
    private List<BattleTank> tmpEnemiesBattleTanks;

    public AiLogic(GameController gc, String name) {
        this.name = name;
        this.gc = gc;
        this.money = 1000;
        this.unitsCount = 10;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.AI;
        this.tmpMyBattleTanks = new ArrayList<>();
        this.tmpPlayerHarvesters = new ArrayList<>();
        this.tmpEnemiesBattleTanks = new ArrayList<>();
        this.timer = 10000.0f;
    }

    @Override
    public void update(float dt) {
        timer += dt;
        if (timer > 2.0f) {
            timer = 0.0f;
            gc.getUnitsController().collectTanksByOwner(tmpMyBattleTanks, this, UnitType.BATTLE_TANK);
//            gc.getUnitsController().collectTanks(tmpPlayerHarvesters, gc.getUnitsController().getPlayerUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanksExcludeOwner(tmpEnemiesBattleTanks, this, UnitType.BATTLE_TANK);
            for (int i = 0; i < tmpMyBattleTanks.size(); i++) {
                BattleTank aiBattleTank = tmpMyBattleTanks.get(i);
                if (aiBattleTank.getTarget() != null) {
                    continue;
                }
                aiBattleTank.commandAttack(findNearestTarget(aiBattleTank, tmpEnemiesBattleTanks));
            }
        }
    }

    public <T extends AbstractUnit> T findNearestTarget(AbstractUnit currentTank, List<T> possibleTargetList) {
        T target = null;
        float minDist = 1000000.0f;
        for (int i = 0; i < possibleTargetList.size(); i++) {
            T possibleTarget = possibleTargetList.get(i);
            if (possibleTarget == currentTank) {
                continue;
            }
            float currentDst = currentTank.getPosition().dst(possibleTarget.getPosition());
            if (currentDst < minDist) {
                target = possibleTarget;
                minDist = currentDst;
            }
        }
        return target;
    }
}
