package com.dune.game.core.users_logic;

import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.BattleMap;
import com.dune.game.core.Building;
import com.dune.game.core.GameController;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;
import com.dune.game.core.units.Harvester;
import com.dune.game.core.units.types.Owner;
import com.dune.game.core.units.types.UnitType;

import java.util.ArrayList;
import java.util.List;

public class AiLogic extends BaseLogic {
    private float timer;

    private List<Harvester> tmpAiHarvesters;
    private List<BattleTank> tmpAiBattleTanks;
    private List<Harvester> tmpPlayerHarvesters;
    private List<BattleTank> tmpPlayerBattleTanks;

    List<Vector2> busyCell; // лист, где будут храниться занятые ячейки

    public AiLogic(GameController gc) {
        this.gc = gc;
        this.money = 1000;
        this.unitsCount = 10;
        this.unitsMaxCount = 100;
        this.ownerType = Owner.AI;
        this.tmpAiBattleTanks = new ArrayList<>();
        this.tmpAiHarvesters = new ArrayList<>();
        this.tmpPlayerHarvesters = new ArrayList<>();
        this.tmpPlayerBattleTanks = new ArrayList<>();
        this.timer = 10000.0f;
        this.busyCell = new ArrayList<>();
    }

    public void update(float dt) {
        timer += dt;
        if (timer > 2.0f) {
            timer = 0.0f;
            gc.getUnitsController().collectTanks(tmpAiHarvesters, gc.getUnitsController().getAiUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpAiBattleTanks, gc.getUnitsController().getAiUnits(), UnitType.BATTLE_TANK);
            gc.getUnitsController().collectTanks(tmpPlayerHarvesters, gc.getUnitsController().getPlayerUnits(), UnitType.HARVESTER);
            gc.getUnitsController().collectTanks(tmpPlayerBattleTanks, gc.getUnitsController().getPlayerUnits(), UnitType.BATTLE_TANK);
            for (int i = 0; i < tmpAiBattleTanks.size(); i++) {
                BattleTank aiBattleTank = tmpAiBattleTanks.get(i);
                aiBattleTank.commandAttack(findNearestTarget(aiBattleTank, tmpPlayerBattleTanks));
            }
            busyCell.clear();
            for (int i = 0; i < tmpAiHarvesters.size(); i++) {
                Harvester h = tmpAiHarvesters.get(i);
                busyCell.add(h.getDestination());

                // если контейнер полный, харвестер едет на базу
                if (h.isFullContainer()) {
                    for (int j = 0; j < gc.getBuildingsController().activeSize(); j++) {
                        Building stock = gc.getBuildingsController().getActiveList().get(j);
                        if (stock != null) {
                            h.commandMoveTo(stock.getEntrancePosition(), false);
                        }
                    }
                } else { // ищем ресурсы
                    float ResX;
                    float ResY;
                    float resource = 0.0f;
                    float amount;
                    Vector2 maxResourceLocation = new Vector2();
                    Vector2 temp = new Vector2(0, 0);
                    for (int k = 0; k < BattleMap.ROWS_COUNT; k++) {
                        for (int l = 0; l < BattleMap.COLUMNS_COUNT; l++) {
                            ResX = ((float) l) * BattleMap.CELL_SIZE;
                            ResY = ((float) k) * BattleMap.CELL_SIZE;
                            temp.set(ResX, ResY);
                            amount = ((float) gc.getMap().getResourceCount(temp) / h.getPosition().dst(temp));
                            if (amount > resource && !h.getDestination().equals(temp) && !busyCell.contains(temp)) //
                            {
                                resource = amount;
                                maxResourceLocation.set(temp);
                            }
                        }
                    }
                    if (resource > 0.0f) {
                        h.commandMoveTo(maxResourceLocation, false);
                        busyCell.add(maxResourceLocation); // добавляем клетку с ресурсом в список занятых клеток
                    }

                }
            }
        }
    }

    public <T extends AbstractUnit> T findNearestTarget(AbstractUnit currentTank, List<T> possibleTargetList) {
        T target = null;
        float minDist = 1000000.0f;
        for (int i = 0; i < possibleTargetList.size(); i++) {
            T possibleTarget = possibleTargetList.get(i);
            float currentDst = currentTank.getPosition().dst(possibleTarget.getPosition());
            if (currentDst < minDist) {
                target = possibleTarget;
                minDist = currentDst;
            }
        }
        return target;
    }

}
