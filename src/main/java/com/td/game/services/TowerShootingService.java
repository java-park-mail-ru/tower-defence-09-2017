package com.td.game.services;

import com.td.game.GameSession;
import com.td.game.domain.Area;
import com.td.game.domain.ShotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TowerShootingService {

    public void processTowerShooting(@NotNull GameSession session) {
        List<Area> areas = session.getAreas();
        session.getCurrentWave()
                .getRunning()
                .forEach(monster ->
                        areas.forEach(area -> area.addObjectIfCollision(monster)));
        List<ShotEvent> shots = areas
                .stream()
                .filter(area -> area.size() > 0)
                .flatMap(area -> area.getTower().fire(area).stream())
                .collect(Collectors.toList());
        session.setShotEvents(shots);
        areas.forEach(Area::clear);
    }

    public void reloadTowers(@NotNull GameSession session, long delta) {
        session.getTowers().forEach(tower -> tower.reload(delta));
    }

}
