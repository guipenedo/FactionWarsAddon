package io.github.guipenedo.factionwarsaddon;

import io.github.guipenedo.factionwars.api.FactionWarsAddonPlugin;
import io.github.guipenedo.factionwars.api.TeamHandler;
import io.github.guipenedo.factionwars.gamemodes.GamemodeManager;
import io.github.guipenedo.factionwars.models.WarMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionWarsAddon extends JavaPlugin implements FactionWarsAddonPlugin {
    @Override
    public TeamHandler getTeamHandler() {
        //if you only want gamemodes, make sure to delete "ExampleTeamHandler" and make this method return null
        return new ExampleTeamHandler("MyCustomTeamHandler");
    }

    @Override
    public GamemodeManager getGamemodeManager(String customType, WarMap map, ConfigurationSection config) {
        //if you want want TeamHandling, make this method return null
        //always check the customType
        if (customType.equals("CUSTOMBLITZ"))
            return new ExampleGamemodeManager(map, config);
        return null;
    }
}
