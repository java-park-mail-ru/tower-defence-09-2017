package com.td.game.services;

import com.td.game.domain.Wave;
import com.td.game.gameobjects.Monster;
import com.td.game.gameobjects.Path;
import com.td.game.resource.ResourceFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class MonsterWaveGenerator {

    public static final long WAVE_START_DELAY = 10000;
    public static final long NEW_MONSTER_DELAY = 500;

    @NotNull
    private final ResourceFactory resourceFactory;

    private List<Monster.MonsterResource> availableMonsters;

    private Random randomGenerator = new Random(System.currentTimeMillis());

    public MonsterWaveGenerator(@NotNull ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    @PostConstruct
    public void init() {
        availableMonsters = resourceFactory.loadResourceList("monsters/MonstersList.json", Monster.MonsterResource.class);
    }

    public Wave generateWave(int waveNumber, List<Path> paths) {
        List<Monster> monsters = new ArrayList<>();
        Map<Path, List<Monster>> pathBindings = new HashMap<>();
        for (int i = 0; i <= waveNumber; ++i) {
            Monster monster = createRandomMonster();
            Path path = paths.get(randomGenerator.nextInt(paths.size()));
            monster.setCoord(path.getInitalPoint());
            monsters.add(monster);
            pathBindings.compute(path, (key, val) -> {
                if (val == null) {
                    List<Monster> binded = new ArrayList<>();
                    binded.add(monster);
                    return binded;
                } else {
                    val.add(monster);
                    return val;
                }
            });

        }
        return new Wave(monsters, pathBindings, WAVE_START_DELAY, NEW_MONSTER_DELAY);
    }

    @NotNull
    private Monster createRandomMonster() {
        Integer randomIdx = randomGenerator.nextInt(availableMonsters.size());
        return new Monster(availableMonsters.get(randomIdx));
    }

}
