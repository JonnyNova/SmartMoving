=======================
Smart Moving Server Mod
=======================

Version 16.3 for Minecraft Server 1.8.9

by Divisor



Description
===========

The Smart Moving Server mod provides smart moving specific communication between different SMP clients.

Additionally using a SMP server with this mod avoids bugs that can happen when using Smart Moving clients with standard SMP servers.

In summary using a SMP server with Smart Moving provides the following features:
* crawling into small holes without taking damage
* climbing down without suffering ground arrival damage
* smart moving anmiations for the other players



Required Mods
=============

This mod requires

    * MinecraftForge and
    * Player API core 1.0 or higher

to be installed too.



Installation
============

Move the Smart Moving file "${%mod.moving.file}" into the subfolder "mods" of your Minecraft server installation folder. In case this folder does not exist, create it or run your Minecraft Forge server at least once.

Don't forget to:
* ensure you have the latest version of Minecraft Forge server installed!
* ensure you have the latest version of PlayerAPI core installed!
* ensure all your clients have Minecraft Forge client installed!


In any case, NEVER forget: ALWAYS back up your stuff!



Development Installation
========================

The details of how to install Smart Moving at a Minecraft Forge development environent are described in the section "Development Installation" of the file "development.txt".



Configuration
=============

The file "smart_moving_options.txt" can be used to configure the behavior this mod.
It is located in your minecraft server's working directory.
If does not exist at Minecraft server startup time it is automatically generated.

You can use its content to manipulate this mod's various features on all connected clients.



SBC Support
===========
Smart Moving provides support for Simple Block code (http://dev.bukkit.org/server-mods/sbc/)
Have a look at the website to find the exact codes to block specific Smart Moving features for server players.