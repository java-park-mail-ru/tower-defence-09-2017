package com.td.game.services;

import com.td.daos.ScoresDao;
import com.td.daos.UserDao;
import com.td.domain.User;
import com.td.game.GameSession;
import com.td.game.domain.GameMap;
import com.td.game.domain.Player;
import com.td.game.domain.PlayerClass;
import com.td.game.domain.Wave;
import com.td.game.gameobjects.Path;
import com.td.game.resource.ResourceFactory;
import com.td.game.snapshots.GameFinishMessage;
import com.td.websocket.TransportService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameSessionService {
    @NotNull
    private final Map<Long, GameSession> usersSessions = new HashMap<>();

    @NotNull
    private final Set<GameSession> sessions = new HashSet<>();

    @NotNull
    private final TransportService transport;

    @NotNull
    private final ResourceFactory resourceFactory;

    @NotNull
    private final PathGenerateService pathGenerator;

    @NotNull
    private final MonsterWaveGenerator waveService;

    @NotNull
    private final UserDao userDao;

    @NotNull
    private final ScoresDao scoresDao;

    @NotNull
    private final GameInitService gameInitService;

    public GameSessionService(@NotNull TransportService transport,
                              @NotNull ResourceFactory resourceFactory,
                              @NotNull PathGenerateService pathGenerator,
                              @NotNull MonsterWaveGenerator waveService,
                              @NotNull UserDao userDao,
                              @NotNull ScoresDao scoresDao,
                              @NotNull GameInitService gameInitService) {
        this.transport = transport;
        this.resourceFactory = resourceFactory;
        this.pathGenerator = pathGenerator;
        this.waveService = waveService;
        this.userDao = userDao;
        this.scoresDao = scoresDao;
        this.gameInitService = gameInitService;
    }

    public void startGame(List<User> users) {
        GameMap map = new GameMap(resourceFactory.loadResource("GameMap.json", GameMap.GameMapResource.class));

        Path path = pathGenerator.generatePath();
        List<Path> paths = new ArrayList<>();
        paths.add(path);

        map.setPathTitles(path.getPathPoints());
        Wave wave = waveService.generateWave(0, paths);
        List<Player> players = users.stream()
                .map(user -> new Player(user.getId(), user.getProfile().getGameClass(), user.getNickname()))
                .collect(Collectors.toList());

        Map<String, PlayerClass> availableClasses = users.stream()
                .map(user -> resourceFactory.loadResource(user.getProfile().getGameClass() + ".json", PlayerClass.class))
                .collect(Collectors
                        .toMap(PlayerClass::toString,
                                value -> value,
                                (left, right) -> left));

        Map<Long, PlayerClass> playersClasses = users.stream()
                .collect(Collectors
                        .toMap(User::getId,
                                user -> availableClasses.get(user.getProfile().getGameClass())
                        ));
        GameSession session = new GameSession(players, playersClasses, map, wave, paths);
        gameInitService.initGameInSession(session);
        sessions.add(session);
        users.forEach(user -> usersSessions.put(user.getId(), session));
    }

    public boolean isPlaying(Long id) {
        return usersSessions.containsKey(id);
    }

    public boolean isValidSession(@NotNull GameSession session) {
        return session
                .getPlayers()
                .stream()
                .allMatch(player -> transport.isConnected(player.getId()));
    }

    public void terminateSession(@NotNull GameSession session, CloseStatus status) {
        List<Player> players = session.getPlayers();
        for (Player player : players) {
            usersSessions.remove(player.getId());
            transport.closeSession(player.getId(), status);
        }
        sessions.remove(session);
    }


    public void finishGame(@NotNull GameSession session) {
        for (Player player : session.getPlayers()) {
            User user = userDao.getUserById(player.getId());
            scoresDao.addScores(user, player.getScores());
            usersSessions.remove(user.getId());
            try {
                transport.sendMessageToUser(player.getId(), new GameFinishMessage(player.getScores()));
                transport.closeSession(player.getId(), CloseStatus.NORMAL);
            } catch (IOException e) {
                transport.closeSession(player.getId(), CloseStatus.SERVER_ERROR);
            }
        }
        sessions.remove(session);
    }


    public Set<GameSession> getSessions() {
        return sessions;
    }

    public GameSession getSessionForUser(Long playerId) {
        return usersSessions.get(playerId);
    }

}