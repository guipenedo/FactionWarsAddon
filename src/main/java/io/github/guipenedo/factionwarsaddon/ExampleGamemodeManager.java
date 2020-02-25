package io.github.guipenedo.factionwarsaddon;

import io.github.guipenedo.factionwars.FactionWars;
import io.github.guipenedo.factionwars.api.FactionWarsAPI;
import io.github.guipenedo.factionwars.gamemodes.GamemodeManager;
import io.github.guipenedo.factionwars.helpers.Util;
import io.github.guipenedo.factionwars.models.WarMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is meant to be used as an example.
 * This is the Blitz implementation included natively in FactionWars.
 * More info on each of the methods you need to implement here:
 * https://guipenedo.github.io/FactionWars/apidocs/io/github/guipenedo/factionwars/gamemodes/GamemodeManager.html
 */
public class ExampleGamemodeManager implements GamemodeManager {
    private final String map;
    private int lives = 3;
    private boolean most_lives_wins = false;
    private Map<UUID, Integer> remaining = new HashMap<>();

    public ExampleGamemodeManager(WarMap map, ConfigurationSection gameSettings) {
        this.map = map.getId();
        this.lives = gameSettings.getInt("gamemode-settings.lives", lives);
        this.most_lives_wins = gameSettings.getBoolean("gamemode-settings.most-lives-wins", most_lives_wins);
    }

    @Override
    public void updateLocations(WarMap map) {
        if (map.isSetup(this)){
            //update internal variables with values from map.getLocations()
        }
    }

    @Override
    public void startMatch() {
        remaining.clear();
        for (UUID uuid : WarMap.getMap(map).getPlayers())
            remaining.put(uuid, lives);
    }

    private int getFactionLives(String f) {
        int lives = 0;
        for (Player player : WarMap.getMap(map).getPlayerList())
            if (f.equals(FactionWars.getHandler().getPlayerTeam(player)))
                lives += getLives(player.getUniqueId());
        return lives;
    }

    @Override
    public void timeOut() {
        if (most_lives_wins) {
            WarMap m = WarMap.getMap(map);
            int f1 = getFactionLives(m.getF1()), f2 = getFactionLives(m.getF2());
            if (f1 > f2)
                FactionWarsAPI.matchWon(m, m.getF1());
            else if (f2 > f1)
                FactionWarsAPI.matchWon(m, m.getF2());
            else
                FactionWarsAPI.matchTied(WarMap.getMap(map));
        } else
            FactionWarsAPI.matchTied(WarMap.getMap(map));
    }

    public void updateScoreboard() {
        WarMap m = WarMap.getMap(map);
        int f1 = playersLeft(m.getF1()), f2 = playersLeft(m.getF2());
        for (Player p : m.getPlayerList())
            FactionWarsAPI.showScoreboard(p, m, Util.getPlainMessage("gamemodes.blitz.scoreboard.players-left"), "§9" + FactionWars.getHandler().getTeamName(m.getF1()), String.valueOf(f1), "§c" + FactionWars.getHandler().getTeamName(m.getF2()), String.valueOf(f2), "", Util.getPlainMessage("gamemodes.blitz.scoreboard.your-lives"), getLives(p.getUniqueId()) + "/" + lives + " §d♥");
    }

    @Override
    public void kill(UUID uuid) {
    }

    private int playersLeft(String f) {
        int count = 0;
        WarMap m = WarMap.getMap(map);
        for (Player player : m.getPlayerList())
            if (getLives(player.getUniqueId()) > 0)
                if (f.equals(FactionWars.getHandler().getPlayerTeam(player)))
                    count++;
        return count;
    }

    private void checkEnd() {
        WarMap m = WarMap.getMap(map);
        int f1 = playersLeft(m.getF1()), f2 = playersLeft(m.getF2());
        if (f1 == 0 && f2 > 0)
            FactionWarsAPI.matchWon(m, m.getF2());
        else if (f2 == 0 && f1 > 0)
            FactionWarsAPI.matchWon(m, m.getF1());
    }

    @Override
    public void death(UUID uuid) {
        if (getLives(uuid) == 0) return;
        remaining.put(uuid, getLives(uuid) - 1);

        Player pl = FactionWars.get().getServer().getPlayer(uuid);
        final HashMap<String, Object> var = Util.getVars("left", getLives(uuid), "lives", lives, "player", pl.getName());
        pl.sendMessage(Util.getMessage("gamemodes.blitz.lost-life", var));
        WarMap.getMap(map).message(getLives(pl.getUniqueId()) > 0 ? Util.getMessage("gamemodes.blitz.broadcast", var) : Util.getMessage("gamemodes.blitz.no-lives", var));

        checkEnd();
    }

    @Override
    public void reset() {
        remaining.clear();
    }

    @Override
    public boolean shouldRespawn(UUID uuid) {
        return getLives(uuid) > 0;
    }

    @Override
    public void move(PlayerMoveEvent e) {
        //useful to check if player is in range of flag in CTF, for example
    }

    @Override
    public Map<String, String> getLocations(WarMap map) {
        return Collections.emptyMap();
        //example for CTF:
        /*
        return ImmutableMap.of("flag1", "Add flags for §9team 1§6: type §c/fw setlocation " + map.getId() + " flag1\nAdd as many as you want!",
                "flag2", "Add flags for §cteam 2§6: type §c/fw setlocation " + map.getId() + " flag2\nAdd as many as you want!",
                "s1", "Add §9team 1§6 flag return point (only add once!) §c/fw setlocation " + map.getId() + " s1",
                "s2", "Add §cteam 2§6 flag return point (only add once!) §c/fw setlocation " + map.getId() + " s2");

         */
    }

    private int getLives(UUID uuid) {
        return remaining.getOrDefault(uuid, 0);
    }
}
