package io.github.guipenedo.factionwarsaddon;

import com.gmail.nossr50.api.ChatAPI;
import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import io.github.guipenedo.factionwars.FactionWars;
import io.github.guipenedo.factionwars.api.TeamHandler;
import io.github.guipenedo.factionwars.handler.TeamHandlerListener;
import io.github.guipenedo.factionwars.models.FactionData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is meant to be used as an example.
 * You do not need this for McMMO party support, as this plugin is natively supported by FactionWars
 * Detailed information on each of the methods you should implement is available here:
 * https://guipenedo.github.io/FactionWars/apidocs/io/github/guipenedo/factionwars/api/TeamHandler.html
 */
public class ExampleTeamHandler extends TeamHandler implements Listener {
    public ExampleTeamHandler(String name) {
        //you may add any other additional setup you need here
        super(name);
    }

    //since party name == id, these methods return the argument
    @Override
    public String getTeamName(String id) {
        return id;
    }

    @Override
    public String getTeamIdByName(String name) {
        return name;
    }

    @Override
    public List<Player> getOnlinePlayers(String id) {
        return PartyAPI.getOnlineMembers(id);
    }

    //possible Relation values: ALLY, NEUTRAL and ENEMY
    @Override
    public Relation getRelationBetween(String id1, String id2) {
        if (id1.equals(PartyAPI.getAllyName(id2)))
            return Relation.ALLY;
        return Relation.NEUTRAL;
    }

    //if this team does not have a separate bank account, you should return the leader's bank account
    @Override
    public String getBankName(String id) {
        return PartyAPI.getPartyLeader(id);
    }

    //send a message to the whole team
    @Override
    public void sendMessage(String id, String message) {
        ChatAPI.sendPartyChat(FactionWars.get(), "Wars", id, message);
    }

    @Override
    public List<String> getAllTeams() {
        ArrayList<String> parties = new ArrayList<>();
        for (Party p : PartyAPI.getParties())
            parties.add(p.getName());
        return parties;
    }

    //possible Roles: ADMIN, NORMAL, MODERATOR, OTHER
    @Override
    public List<Player> getMembersWithRole(String id, Role role) {
        if (role == Role.ADMIN) {
            return Collections.singletonList(FactionWars.get().getServer().getPlayer(PartyAPI.getPartyLeader(id)));
        } else if (role == Role.NORMAL)
            return getOnlinePlayers(id);
        return Collections.emptyList();
    }

    //the parameter might be a Player, OfflinePlayer or player name (String)
    @Override
    public String getPlayerTeam(Object player) {
        if (player instanceof String)
            player = FactionWars.get().getServer().getPlayer((String) player);
        if (player instanceof Player) {
            try {
                return PartyAPI.getPartyName((Player) player);
            } catch (Exception e){
                return null;
            }
        }
        return null;
    }

    //Events
    @EventHandler(ignoreCancelled = true)
    public void onMcMMOPartyChangeEvent(final McMMOPartyChangeEvent e) {
        if (FactionData.getFaction(e.getNewParty()) == null)
            TeamHandlerListener.onTeamCreate(e.getNewParty());
        TeamHandlerListener.onTeamChange(e.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getOldParty() != null && PartyAPI.getPartyLeader(e.getOldParty()) == null)
                    TeamHandlerListener.onTeamDelete(e.getOldParty());
            }
        }.runTaskLater(FactionWars.get(), 1000);
    }
}
