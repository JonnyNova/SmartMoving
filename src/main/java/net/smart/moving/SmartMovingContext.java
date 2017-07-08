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

import net.minecraftforge.fml.client.registry.*;
import net.minecraftforge.fml.common.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.client.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.WorldSettings.*;
import net.smart.moving.config.*;
import net.smart.render.*;

public abstract class SmartMovingContext extends SmartRenderContext
{
	public static final float ClimbPullMotion = 0.3F;

	public static final double FastUpMotion = 0.2D;
	public static final double MediumUpMotion = 0.14D;
	public static final double SlowUpMotion = 0.1D;
	public static final double HoldMotion = 0.08D;
	public static final double SinkDownMotion = 0.05D;
	public static final double ClimbDownMotion = 0.01D;
	public static final double CatchCrawlGapMotion = 0.17D;

	public static final float SwimCrawlWaterMaxBorder = 1F;
	public static final float SwimCrawlWaterTopBorder = 0.65F;
	public static final float SwimCrawlWaterMediumBorder = 0.6F;
	public static final float SwimCrawlWaterBottomBorder = 0.55F;

	public static final float HorizontalGroundDamping = 0.546F;
	public static final float HorizontalAirDamping = 0.91F;
	public static final float HorizontalAirodynamicDamping = 0.999F;

	public static final float SwimSoundDistance = 1F / 0.7F;
	public static final float SlideToHeadJumpingFallDistance = 0.05F;


	public static final SmartMovingClient Client = new SmartMovingClient();
	public static final SmartMovingOptions Options = new SmartMovingOptions();
	public static final SmartMovingServerConfig ServerConfig = new SmartMovingServerConfig();
	public static SmartMovingClientConfig Config = Options;


	private static boolean wasInitialized;

	public static void onTickInGame()
	{
		Minecraft minecraft = Minecraft.getMinecraft();

		if(minecraft.theWorld != null && minecraft.theWorld.isRemote)
			SmartMovingFactory.handleMultiPlayerTick(minecraft);

		Options.initializeForGameIfNeccessary();

		initializeServerIfNecessary();
	}

	public static void initialize()
	{
		if(!wasInitialized)
			net.smart.render.statistics.SmartStatisticsContext.setCalculateHorizontalStats(true);

		ClientRegistry.registerKeyBinding(Options.keyBindGrab);
		ClientRegistry.registerKeyBinding(Options.keyBindConfigToggle);
		ClientRegistry.registerKeyBinding(Options.keyBindSpeedIncrease);
		ClientRegistry.registerKeyBinding(Options.keyBindSpeedDecrease);

		if(wasInitialized)
			return;

		wasInitialized = true;

		System.out.println(SmartMovingInfo.ModComMessage);
		FMLLog.getLogger().info(SmartMovingInfo.ModComMessage);
	}

	public static void initializeServerIfNecessary()
	{
		MinecraftServer currentMinecraftServer = MinecraftServer.getServer();
		if(currentMinecraftServer != null && currentMinecraftServer != lastMinecraftServer)
		{
			GameType gameType;
			try
			{
				gameType = currentMinecraftServer.getGameType();
			}
			catch(Throwable t)
			{
				return;
			}
			SmartMovingServer.initialize(SmartMovingOptions.optionsPath, gameType.getID(), Options);
		}
		lastMinecraftServer = currentMinecraftServer;
	}

	public static Block getBlock(World world, int x, int y, int z)
	{
		return getState(world, x, y, z).getBlock();
	}

	public static IBlockState getState(World world, int x, int y, int z)
	{
		BlockPos position = new BlockPos(x, y, z);
		IBlockState state = world.getBlockState(position);
		Block block = state.getBlock();
		return block.getActualState(state, world, position);
	}

	public static Material getMaterial(World world, int x, int y, int z)
	{
		return getBlock(world, x, y, z).getMaterial();
	}

	public static boolean getValue(IBlockState state, PropertyBool property)
	{
		return state.getValue(property);
	}

	public static int getValue(IBlockState state, PropertyInteger property)
	{
		return state.getValue(property);
	}

	public static EnumFacing getValue(IBlockState state, PropertyDirection property)
	{
		return state.getValue(property);
	}

	public static Enum getValue(IBlockState state, PropertyEnum property)
	{
		return (Enum)state.getValue(property);
	}

	private static MinecraftServer lastMinecraftServer = null;
}