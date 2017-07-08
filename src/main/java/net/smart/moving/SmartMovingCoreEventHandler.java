// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving;

import net.minecraft.client.entity.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import net.minecraft.server.management.*;

import net.smart.core.*;
import net.smart.moving.playerapi.*;

public class SmartMovingCoreEventHandler extends SmartCoreEventHandler
{
	@Override
	public void beforeProcessPlayer(NetHandlerPlayServer netServerHandler)
	{
		SmartMovingServerPlayerBase playerBase = SmartMovingServerPlayerBase.getPlayerBase(netServerHandler.playerEntity);
		playerBase.moving.beforeAddMovingHungerBatch();
	}

	@Override
	public void afterProcessPlayer(NetHandlerPlayServer netServerHandler)
	{
		SmartMovingServerPlayerBase playerBase = SmartMovingServerPlayerBase.getPlayerBase(netServerHandler.playerEntity);
		playerBase.moving.afterAddMovingHungerBatch();
	}

	@Override
	public void beforeOnPlayerRightClick(PlayerControllerMP playerControllerMP, EntityPlayerSP entityPlayerSP)
	{
		SmartMovingPlayerBase playerBase = SmartMovingPlayerBase.getPlayerBase(entityPlayerSP);
		playerBase.moving.beforeActivateBlockOrUseItem();
	}

	@Override
	public void afterOnPlayerRightClick(PlayerControllerMP playerControllerMP, EntityPlayerSP entityPlayerSP)
	{
		SmartMovingPlayerBase playerBase = SmartMovingPlayerBase.getPlayerBase(entityPlayerSP);
		playerBase.moving.afterActivateBlockOrUseItem();
	}

	@Override
	public void beforeActivateBlockOrUseItem(ItemInWorldManager itemInWorldManager, EntityPlayer entityPlayer)
	{
		SmartMovingServerPlayerBase playerBase = SmartMovingServerPlayerBase.getPlayerBase(entityPlayer);
		playerBase.moving.beforeActivateBlockOrUseItem();
	}

	@Override
	public void afterActivateBlockOrUseItem(ItemInWorldManager itemInWorldManager, EntityPlayer entityPlayer)
	{
		SmartMovingServerPlayerBase playerBase = SmartMovingServerPlayerBase.getPlayerBase(entityPlayer);
		playerBase.moving.afterActivateBlockOrUseItem();
	}
}