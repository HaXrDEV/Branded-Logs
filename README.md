# **Branded Logs**

This is a simple mod that allows modpack creators to print additional information about their modpack into the game's logs. This includes the name of the modpack and its version, as well as information about the user's machine, operating system, Java version, and RAM allocation.

*This mod was greatly inspired by [[Log More Info]](https://github.com/FederAndInk/log_more_info).*

![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)
![neoforge](https://raw.githubusercontent.com/thomasglasser/thomasglasser/refs/heads/main/files/badges/cozy/supported/neoforge_vector.svg)
![forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg)

## **Why would anyone need this?**

As a modpack author, you may often receive logs from players asking for help with an issue or crash. With this mod, there will no longer be any confusion about which version of your modpack a log was generated from. You will also no longer have to ask them about the hardware or Java configuration of their system, as that can easily be deduced from the logs!

## **How to use it**

The mod will automatically parse modpack information from supported launchers right out of the box. However, if a configuration file from *[Better Compatibility Checker](https://www.curseforge.com/minecraft/mc-mods/better-compatibility-checker)* (*bcc.json*) is present, it will always take priority.

#### Supported Launchers:

* ✅ **CurseForge App** (*minecraftinstance.json*)
* ✅ **Prism Launcher** (*instance.cfg*)
* ✅ **ATLauncher** (*instance.json*)
* ✅ **GDLauncher** (*instance.json*)
* ❌ **Modrinth App** (*No longer uses profile.json file*)

To enable the mod to retrieve the version number and name of your modpack in an unsupported launcher, you need to include a configuration file from *Better Compatibility Checker* in your pack. You can either use the *[Better Compatibility Checker](https://www.curseforge.com/minecraft/mc-mods/better-compatibility-checker)* mod to generate the config file, or create one manually using the template below:

#### `./config/bcc.json`
```json
{"modpackName": "INSERT_MODPACK_NAME_HERE", "modpackVersion": "INSERT_MODPACK_VERSION_HERE"}
```

## **Dependencies**

[![cloth-config-api](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/cloth-config-api_vector.svg)](https://modrinth.com/mod/cloth-config)

Optional: *[Better Compatibility Checker](https://www.curseforge.com/minecraft/mc-mods/better-compatibility-checker)*

## **Links**

[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/branded-logs)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/branded-logs)
[![discord-singular](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-singular_vector.svg)](https://discord.gg/Kss5gBgeDA)
