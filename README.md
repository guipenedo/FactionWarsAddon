# FactionWarsAddon

This is an example implementation of [FactionWars'](https://www.spigotmc.org/resources/factionwars-new-updated.10961/) API.

+ **ExampleTeamHandler** shows how to provide support for a custom teams plugin (McMMO Parties is used as an example).
+ **ExampleGamemodeManager** shows how to use FactionWars to create a custom gamemode (the native Blitz is used as an example).

Don't forget to include the following in your plugin.yml:
```yaml
loadbefore: [FactionWars]
```

For obvious reasons, *FactionWars.jar* is not included in this repository, but it must be added as a dependency of your addon.

## More info

+ [Guide](https://guipenedo.github.io/FactionWars/#/api)
+ [Javadocs](https://guipenedo.github.io/FactionWars/apidocs/)