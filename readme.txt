================
Smart Moving Mod
================

Version 16.3 for Minecraft Client 1.8.9

by Divisor



Description
===========

The Smart moving mod provides various additionaly moving possibilities:

* Climbing only via gaps in the walls
* Climbing ladders with different speeds depending on ladder coverage and/or neighbour blocks
* Alternative animations for flying and falling
* Climbing along ceilings and up vines
* Jumping up & back while climbing
* Configurable sneaking
* Alternative swimming
* Alternative diving
* Alternative flying
* Faster sprinting
* Side & Back jumps
* Charged jumps
* Wall jumping
* Head jumps
* Crawling
* Sliding

Exact behavior depends on configuration (see chapter 'Configuration' below)



Required Mods
=============

This mod requires

    * Minecraft Forge and
    * Player API core 1.0 or higher

to be installed too. Additionally

    * Render Player API core 1.0 or higher

can be installed to improve the compatibiltity to other mods.



Installation
============

Move the Smart Moving files "${mod.render.file}" and "${%mod.moving.file}" into the subfolder "mods" of your Minecraft Forge installation folder. In case this folder does not exist, create it or run your Minecraft Forge client at least once.

Don't forget to:
* ensure you have the latest version of Player API core installed!
* ensure you have the latest version of Minecraft Forge installed!



Development Installation
========================

The details of how to install Smart Moving at a Minecraft Forge development environent are described in the section "Development Installation" of the file "development.txt".



Configuration
=============

The file "smart_moving_options.txt" can be used to configure the behavior this mod.
It is located in your ".minecraft" folder next to minecrafts "options.txt".
If does not exist at Minecraft startup time it is automatically generated.

You can use its content to manipulate this mod's various features.