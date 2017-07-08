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

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smart.moving.config.SmartMovingOptions;
import net.smart.utilities.BlockWallUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static net.smart.render.SmartRenderUtilities.RadiantToAngle;

public abstract class SmartMovingBase extends SmartMovingContext
{
	public final EntityPlayer sp;
	public final EntityPlayerSP esp;
	public final IEntityPlayerSP isp;

	public SmartMovingBase(EntityPlayer sp, IEntityPlayerSP isp)
	{
		this.sp = sp;
		this.isp = isp;

		if (sp instanceof EntityPlayerSP)
		{
			esp = (EntityPlayerSP) sp;
			if(Minecraft.getMinecraft().thePlayer == null)
			{
				Options.resetForNewGame();
				Config = Options;
			}
		}
		else
			esp = null;
	}

	public Block getBlock(int x, int y, int z)
	{
		return getBlock(sp.worldObj, x, y, z);
	}

	public IBlockState getState(BlockPos blockPos) {
		return getState(sp.worldObj, blockPos);
	}

	public IBlockState getState(int x, int y, int z)
	{
		return getState(sp.worldObj, x, y, z);
	}

	public Material getMaterial(int x, int y, int z)
	{
		return getMaterial(sp.worldObj, x, y, z);
	}

	public boolean isAirBlock(int x, int y, int z)
	{
		return sp.worldObj.isAirBlock(new BlockPos(x, y, z));
	}

	public AxisAlignedBB getBoundingBox()
	{
		return sp.getEntityBoundingBox();
	}

	public void setBoundingBox(AxisAlignedBB boundingBox)
	{
		sp.setEntityBoundingBox(boundingBox);
	}

	protected void moveFlying(float moveUpward, float moveStrafing, float moveForward, float speedFactor, boolean treeDimensional)
	{
		float diffMotionXStrafing = 0, diffMotionXForward = 0, diffMotionZStrafing = 0, diffMotionZForward = 0;
		{
			float total = MathHelper.sqrt_float(moveStrafing * moveStrafing + moveForward * moveForward);
			if(total >= 0.01F)
			{
				if(total < 1.0F)
					total = 1.0F;

				float moveStrafingFactor = moveStrafing / total;
				float moveForwardFactor = moveForward / total;
				float sin = MathHelper.sin((sp.rotationYaw * 3.141593F) / 180F);
				float cos = MathHelper.cos((sp.rotationYaw * 3.141593F) / 180F);
				diffMotionXStrafing = moveStrafingFactor * cos;
				diffMotionXForward = -moveForwardFactor * sin;
				diffMotionZStrafing = moveStrafingFactor * sin;
				diffMotionZForward = moveForwardFactor * cos;
			}
		}

		float rotation = treeDimensional ? sp.rotationPitch / RadiantToAngle : 0;
		float divingHorizontalFactor = MathHelper.cos(rotation);
		float divingVerticalFactor = -MathHelper.sin(rotation) * Math.signum(moveForward);

		float diffMotionX = diffMotionXForward * divingHorizontalFactor + diffMotionXStrafing;
		float diffMotionY = MathHelper.sqrt_float(diffMotionXForward * diffMotionXForward + diffMotionZForward * diffMotionZForward) * divingVerticalFactor + moveUpward;
		float diffMotionZ = diffMotionZForward * divingHorizontalFactor + diffMotionZStrafing;

		float total = MathHelper.sqrt_float(MathHelper.sqrt_float(diffMotionX * diffMotionX + diffMotionZ * diffMotionZ) + diffMotionY * diffMotionY);
		if(total > 0.01F)
		{
			float factor = speedFactor / total;
			sp.motionX += diffMotionX * factor;
			sp.motionY += diffMotionY * factor;
			sp.motionZ += diffMotionZ * factor;
		}
	}

	protected Block supportsCeilingClimbing(int i, int j, int k)
	{
		IBlockState state = getState(i, j, k);
		if(state == null)
			return null;

		String name = state.getBlock().getUnlocalizedName();
		if(name == null)
			return null;

		if(name.equals("tile.fenceIron"))
			return state.getBlock();

		if((name.equals("tile.trapdoor") || name.equals("tile.trapdoor_iron")) && !getValue(state, BlockTrapDoor.OPEN))
			return state.getBlock();

		return null;
	}

	@SuppressWarnings("static-method")
	protected boolean isLava(IBlockState state)
	{
		return state.getMaterial() == Material.LAVA;
	}

	protected float getLiquidBorder(int i, int j, int k)
	{
		float finiteLiquidBorder;
		Block block = getBlock(i, j, k);
		if(block == Block.getBlockFromName("water") || block == Block.getBlockFromName("flowing_water"))
			return getNormalWaterBorder(i, j, k);
		if(SmartMovingOptions.hasFiniteLiquid && (finiteLiquidBorder = getFiniteLiquidWaterBorder(i, j, k, block)) > 0)
			return finiteLiquidBorder;
		if(block == Block.getBlockFromName("lava") || block == Block.getBlockFromName("flowing_lava"))
			return Config._lavaLikeWater.value ? getNormalWaterBorder(i, j, k) : 0F;

		Material material = getMaterial(i, j, k);
		if(material == null || material == Material.LAVA)
			return Config._lavaLikeWater.value ? 1F : 0F;
		if(material == Material.WATER)
			return getNormalWaterBorder(i, j, k);
		if(material.isLiquid())
			return 1F;

		return 0F;
	}

	protected float getNormalWaterBorder(int i, int j, int k)
	{
		int level = getValue(getState(i, j, k), BlockLiquid.LEVEL);
		if(level >= 8)
			return 1F;
		if(level == 0)
			if(getBlock(i, j + 1, k).getMaterial(null) == Material.AIR)
				return 0.8875F;
			else
				return 1F;
		return (8 - level) / 8F;
	}

	protected float getFiniteLiquidWaterBorder(int i, int j, int k, Block block)
	{
		int type;
		if((type = Orientation.getFiniteLiquidWater(block)) > 0)
		{
			if(type == 2)
				return 1F;
			if(type == 1)
			{
				Block aboveBlock = getBlock(sp.worldObj, i, j + 1, k);
				if(Orientation.getFiniteLiquidWater(aboveBlock) > 0)
					return 1F;
				return getValue(getState(i, j, k), BlockLiquid.LEVEL) / 16F;
			}
		}
		return 0F;
	}

	public boolean isFacedToLadder(boolean isSmall)
	{
		return getOnLadder(1, true, isSmall) > 0;
	}

	public boolean isFacedToSolidVine(boolean isSmall)
	{
		return getOnVine(1, true, isSmall) > 0;
	}

	public boolean isOnLadderOrVine(boolean isSmall)
	{
		return getOnLadderOrVine(1, false, isSmall) > 0;
	}

	public boolean isOnVine(boolean isSmall)
	{
		return getOnLadderOrVine(1, false, false, true, isSmall) > 0;
	}

	public boolean isOnLadder(boolean isSmall)
	{
		return getOnLadderOrVine(1, false, true, false, isSmall) > 0;
	}

	protected int getOnLadder(int maxResult, boolean faceOnly, boolean isSmall)
	{
		return getOnLadderOrVine(maxResult, faceOnly, true, false, isSmall);
	}

	protected int getOnVine(int maxResult, boolean faceOnly, boolean isSmall)
	{
		return getOnLadderOrVine(maxResult, faceOnly, false, true, isSmall);
	}

	protected int getOnLadderOrVine(int maxResult, boolean faceOnly, boolean isSmall)
	{
		return getOnLadderOrVine(maxResult, faceOnly, true, true, isSmall);
	}

	protected int getOnLadderOrVine(int maxResult, boolean faceOnly, boolean ladder, boolean vine, boolean isSmall)
	{
		int i = MathHelper.floor_double(sp.posX);
		int minj = MathHelper.floor_double(getBoundingBox().minY);
		int k = MathHelper.floor_double(sp.posZ);

		if(Config.isStandardBaseClimb())
		{
			Block block = getBlock(sp.worldObj, i, minj, k);
			if(ladder)
				if(vine)
					return Orientation.isClimbable(sp.worldObj, i, minj, k) ? 1 : 0;
				else
					return block != Block.getBlockFromName("vine") && Orientation.isClimbable(sp.worldObj, i, minj, k) ? 1 : 0;
			else
				if(vine)
					return block == Block.getBlockFromName("vine") && Orientation.isClimbable(sp.worldObj, i, minj, k) ? 1 : 0;
				else
					return 0;
		}
		else
		{
			if(isSmall)
				minj--;

			HashSet<Orientation> facedOnlyTo = null;
			if(faceOnly)
				facedOnlyTo = Orientation.getClimbingOrientations(sp, true, false);

			int result = 0;
			int maxj = MathHelper.floor_double(sp.getEntityBoundingBox().minY + Math.ceil(getBoundingBox().maxY - getBoundingBox().minY)) - 1;
			for(int j = minj; j <= maxj; j++)
			{
				IBlockState state = getState(i, j, k);
				if(ladder)
				{
					boolean localLadder = Orientation.isKnownLadder(state);
					Orientation localLadderOrientation = null;
					if(localLadder)
					{
						localLadderOrientation = Orientation.getKnownLadderOrientation(sp.worldObj, i, j, k);
						if(facedOnlyTo == null || facedOnlyTo.contains(localLadderOrientation))
							result++;
					}

					for(Orientation direction : facedOnlyTo != null ? facedOnlyTo : Orientation.Orthogonals)
					{
						if(result >= maxResult)
							return result;

						if(direction != localLadderOrientation)
						{
							IBlockState remoteState = getState(i + direction._i, j, k + direction._k);
							if(Orientation.isKnownLadder(remoteState))
							{
								Orientation remoteLadderOrientation = Orientation.getKnownLadderOrientation(sp.worldObj, i + direction._i, j, k + direction._k);
								if(remoteLadderOrientation.rotate(180) == direction)
									result++;
							}
						}
					}
				}

				if(result >= maxResult)
					return result;

				if(vine && Orientation.isVine(state))
					if(facedOnlyTo == null)
						result++;
					else
					{
						Iterator<Orientation> iterator = facedOnlyTo.iterator();
						while(iterator.hasNext())
						{
							Orientation climbOrientation = iterator.next();
							if(climbOrientation.hasVineOrientation(sp.worldObj, i, j, k) && climbOrientation.isRemoteSolid(sp.worldObj, i, j, k))
							{
								result++;
								break;
							}
						}
					}

				if(result >= maxResult)
					return result;
			}
			return result;
		}
	}

	public boolean climbingUpIsBlockedByLadder()
	{
		if(sp.isCollidedHorizontally && sp.isCollidedVertically && !sp.onGround && esp.movementInput.moveForward > 0F)
		{
			Orientation orientation = Orientation.getOrientation(sp, 20F, true, false);
			if(orientation != null)
			{
				int i = MathHelper.floor_double(sp.posX);
				int j = MathHelper.floor_double(getBoundingBox().maxY);
				int k = MathHelper.floor_double(sp.posZ);
				if(Orientation.isLadder(getState(i, j, k)))
					return Orientation.getKnownLadderOrientation(sp.worldObj, i, j, k) == orientation;
			}
		}
		return false;
	}

	public boolean climbingUpIsBlockedByTrapDoor()
	{
		if(sp.isCollidedHorizontally && sp.isCollidedVertically && !sp.onGround && esp.movementInput.moveForward > 0F)
		{
			Orientation orientation = Orientation.getOrientation(sp, 20F, true, false);
			if(orientation != null)
			{
				int i = MathHelper.floor_double(sp.posX);
				int j = MathHelper.floor_double(getBoundingBox().maxY);
				int k = MathHelper.floor_double(sp.posZ);
				if(Orientation.isTrapDoor(getState(i, j, k)))
					return Orientation.getOpenTrapDoorOrientation(sp.worldObj, i, j, k) == orientation;
			}
		}
		return false;
	}

	public boolean climbingUpIsBlockedByCobbleStoneWall()
	{
		if(sp.isCollidedHorizontally && sp.isCollidedVertically && !sp.onGround && esp.movementInput.moveForward > 0F)
		{
			Orientation orientation = Orientation.getOrientation(sp, 20F, true, false);
			if(orientation != null)
			{
				int i = MathHelper.floor_double(sp.posX);
				int j = MathHelper.floor_double(getBoundingBox().maxY);
				int k = MathHelper.floor_double(sp.posZ);
				if(getBlock(i, j, k) == Block.getBlockFromName("cobblestone_wall"))
					return BlockWallUtil.canConnectTo(
							Block.getBlockFromName("cobblestone_wall"),
							sp.worldObj,
							new BlockPos(i - orientation._i, j, k - orientation._k));
			}
		}
		return false;
	}

	private List<?> getPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
	{
		AxisAlignedBB bb = getBoundingBox();
		bb = new AxisAlignedBB(bb.minX, yMin, bb.minZ, bb.maxX, yMax, bb.maxZ);
		return sp.worldObj.getCollisionBoxes(sp, horizontalTolerance == 0 ? bb : bb.expand(horizontalTolerance, 0, horizontalTolerance));
	}

	protected boolean isPlayerInSolidBetween(double yMin, double yMax)
	{
		return getPlayerSolidBetween(yMin, yMax, 0).size() > 0;
	}

	protected double getMaxPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
	{
		List<?> solids = getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
		double result = yMin;
		for(int i = 0; i < solids.size(); i++)
		{
			AxisAlignedBB box = (AxisAlignedBB)solids.get(i);
			if(isCollided(box, yMin, yMax, horizontalTolerance))
				result = Math.max(result, box.maxY);
		}
		return Math.min(result, yMax);
	}

	protected double getMinPlayerSolidBetween(double yMin, double yMax, double horizontalTolerance)
	{
		List<?> solids = getPlayerSolidBetween(yMin, yMax, horizontalTolerance);
		double result = yMax;
		for(int i = 0; i < solids.size(); i++)
		{
			AxisAlignedBB box = (AxisAlignedBB)solids.get(i);
			if(isCollided(box, yMin, yMax, horizontalTolerance))
				result = Math.min(result, box.minY);
		}
		return Math.max(result, yMin);
	}

	protected boolean isInLiquid()
	{
		return
			getMaxPlayerLiquidBetween(getBoundingBox().minY, getBoundingBox().maxY) != getBoundingBox().minY ||
			getMinPlayerLiquidBetween(getBoundingBox().minY, getBoundingBox().maxY) != getBoundingBox().maxY;
	}

	protected double getMaxPlayerLiquidBetween(double yMin, double yMax)
	{
		int i = MathHelper.floor_double(sp.posX);
		int jMin = MathHelper.floor_double(yMin);
		int jMax = MathHelper.floor_double(yMax);
		int k = MathHelper.floor_double(sp.posZ);

		for(int j = jMax; j >= jMin; j--)
		{
			float swimWaterBorder = getLiquidBorder(i, j, k);
			if(swimWaterBorder > 0)
				return j + swimWaterBorder;
		}
		return yMin;
	}

	protected double getMinPlayerLiquidBetween(double yMin, double yMax)
	{
		int i = MathHelper.floor_double(sp.posX);
		int jMin = MathHelper.floor_double(yMin);
		int jMax = MathHelper.floor_double(yMax);
		int k = MathHelper.floor_double(sp.posZ);

		for(int j = jMin; j <= jMax; j++)
		{
			float swimWaterBorder = getLiquidBorder(i, j, k);
			if(swimWaterBorder > 0)
				if(j > yMin)
					return j;
				else if(j + swimWaterBorder > yMin)
					return yMin;
		}
		return yMax;
	}

	public boolean isCollided(AxisAlignedBB box, double yMin, double yMax, double horizontalTolerance)
	{
		return
			box.maxX >= getBoundingBox().minX - horizontalTolerance &&
			box.minX <= getBoundingBox().maxX + horizontalTolerance &&
			box.maxY >= yMin &&
			box.minY <= yMax &&
			box.maxZ >= getBoundingBox().minZ - horizontalTolerance &&
			box.minZ <= getBoundingBox().maxZ + horizontalTolerance;
	}

	private boolean isHeadspaceFree(BlockPos pos, int height, boolean top)
	{
		for (int y = 0; y < height; y++)
			if (isOpenBlockSpace(pos.add(0, y, 0), top))
				return false;
		return true;
	}

	protected boolean pushOutOfBlocks(double x, double y, double z, boolean top)
	{
		BlockPos blockpos = new BlockPos(x, y, z);
		double d3 = x - blockpos.getX();
		double d4 = z - blockpos.getZ();

		int entHeight = Math.max(Math.round(sp.height), 1);

		boolean inTranslucentBlock = isHeadspaceFree(blockpos, entHeight, top);
		if (inTranslucentBlock)
		{
			byte b0 = -1;
			double d5 = 9999.0D;
			if ((!isHeadspaceFree(blockpos.west(), entHeight, top)) && (d3 < d5))
			{
				d5 = d3;
				b0 = 0;
			}
			if ((!isHeadspaceFree(blockpos.east(), entHeight, top)) && (1.0D - d3 < d5))
			{
				d5 = 1.0D - d3;
				b0 = 1;
			}
			if ((!isHeadspaceFree(blockpos.north(), entHeight, top)) && (d4 < d5))
			{
				d5 = d4;
				b0 = 4;
			}
			if ((!isHeadspaceFree(blockpos.south(), entHeight, top)) && (1.0D - d4 < d5))
			{
				d5 = 1.0D - d4;
				b0 = 5;
			}

			float f = 0.1F;
			if (b0 == 0)
				sp.motionX = -f;
			if (b0 == 1)
				sp.motionX = f;
			if (b0 == 4)
				sp.motionZ = -f;
			if (b0 == 5)
				sp.motionZ = f;
		}
		return false;
	}

	private boolean isOpenBlockSpace(BlockPos pos, boolean top)
	{
		IBlockState blockState = getState(pos);
		IBlockState upBlockState = getState(pos.up());
		return !blockState.getBlock().isNormalCube(blockState, sp.worldObj, pos) &&
				(!top || !upBlockState.getBlock().isNormalCube(blockState, sp.worldObj, pos.up()));
	}

	public boolean isInsideOfMaterial(Material material)
	{
		if(SmartMovingOptions.hasFiniteLiquid && material == Material.WATER)
		{
			double d = sp.posY + sp.getEyeHeight();
			int i = MathHelper.floor_double(sp.posX);
			int j = MathHelper.floor_float(MathHelper.floor_double(d));
			int k = MathHelper.floor_double(sp.posZ);
			Block l = getBlock(i, j, k);
			float border;
			if(l != null && (border = getFiniteLiquidWaterBorder(i, j, k, l)) > 0)
			{
				float f = (1 - border) - 0.1111111F;
				float f1 = (j + 1) - f;
				return d < f1;
			} else
			{
				return false;
			}
		}
		return isp.localIsInsideOfMaterial(material);
	}

	public int calculateSeparateCollisions(double x, double y, double z)
	{
		boolean isInWeb = isp.getIsInWebField();
		AxisAlignedBB boundingBox = sp.getEntityBoundingBox();
		boolean onGround = sp.onGround;
		World worldObj = sp.worldObj;
		Entity _this = sp;
		float stepHeight = sp.stepHeight;

		if (isInWeb)
		{
			isInWeb = false;
			x *= 0.25D;
			y *= 0.0500000007450581D;
			z *= 0.25D;
		}
		double d6 = x;
		double d7 = y;
		double d8 = z;
		boolean flag = onGround && isSneaking();
		if (flag)
		{
			double d9 = 0.05D;
			for (; (x != 0.0D) && (worldObj.getCollisionBoxes(_this, boundingBox.offset(x, -1.0D, 0.0D)).isEmpty()); d6 = x) {
				if ((x < d9) && (x >= -d9)) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= d9;
				} else {
					x += d9;
				}
			}
			for (; (z != 0.0D) && (worldObj.getCollisionBoxes(_this, boundingBox.offset(0.0D, -1.0D, z)).isEmpty()); d8 = z) {
				if ((z < d9) && (z >= -d9)) {
					z = 0.0D;
				} else if (z > 0.0D) {
					z -= d9;
				} else {
					z += d9;
				}
			}
			for (; (x != 0.0D) && (z != 0.0D) && (worldObj.getCollisionBoxes(_this, boundingBox.offset(x, -1.0D, z)).isEmpty()); d8 = z)
			{
				if ((x < d9) && (x >= -d9)) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= d9;
				} else {
					x += d9;
				}
				d6 = x;
				if ((z < d9) && (z >= -d9)) {
					z = 0.0D;
				} else if (z > 0.0D) {
					z -= d9;
				} else {
					z += d9;
				}
			}
		}
		List list1 = worldObj.getCollisionBoxes(_this, boundingBox.addCoord(x, y, z));
		AxisAlignedBB axisalignedbb = boundingBox;
		AxisAlignedBB axisalignedbb1;
		for (Iterator iterator = list1.iterator(); iterator.hasNext(); y = axisalignedbb1.calculateYOffset(boundingBox, y)) {
			axisalignedbb1 = (AxisAlignedBB)iterator.next();
		}
		boundingBox = boundingBox.offset(0.0D, y, 0.0D);
		boolean flag1 = onGround || ((d7 != y) && (d7 < 0.0D));
		AxisAlignedBB axisalignedbb2;
		for (Iterator iterator8 = list1.iterator(); iterator8.hasNext(); x = axisalignedbb2.calculateXOffset(boundingBox, x)) {
			axisalignedbb2 = (AxisAlignedBB)iterator8.next();
		}
		boundingBox = boundingBox.offset(x, 0.0D, 0.0D);
		for (Iterator iterator8 = list1.iterator(); iterator8.hasNext(); z = axisalignedbb2.calculateZOffset(boundingBox, z)) {
			axisalignedbb2 = (AxisAlignedBB)iterator8.next();
		}
		boundingBox = boundingBox.offset(0.0D, 0.0D, z);
		if ((stepHeight > 0.0F) && (flag1) && ((d6 != x) || (d8 != z)))
		{
			double d14 = x;
			double d10 = y;
			double d11 = z;
			boundingBox = axisalignedbb;
			y = stepHeight;
			List list = worldObj.getCollisionBoxes(_this, boundingBox.addCoord(d6, y, d8));
			AxisAlignedBB axisalignedbb4 = boundingBox;
			AxisAlignedBB axisalignedbb5 = axisalignedbb4.addCoord(d6, 0.0D, d8);
			double d12 = y;
			AxisAlignedBB axisalignedbb6;
			for (Iterator iterator1 = list.iterator(); iterator1.hasNext(); d12 = axisalignedbb6.calculateYOffset(axisalignedbb5, d12)) {
				axisalignedbb6 = (AxisAlignedBB)iterator1.next();
			}
			axisalignedbb4 = axisalignedbb4.offset(0.0D, d12, 0.0D);
			double d18 = d6;
			AxisAlignedBB axisalignedbb7;
			for (Iterator iterator2 = list.iterator(); iterator2.hasNext(); d18 = axisalignedbb7.calculateXOffset(axisalignedbb4, d18)) {
				axisalignedbb7 = (AxisAlignedBB)iterator2.next();
			}
			axisalignedbb4 = axisalignedbb4.offset(d18, 0.0D, 0.0D);
			double d19 = d8;
			AxisAlignedBB axisalignedbb8;
			for (Iterator iterator3 = list.iterator(); iterator3.hasNext(); d19 = axisalignedbb8.calculateZOffset(axisalignedbb4, d19)) {
				axisalignedbb8 = (AxisAlignedBB)iterator3.next();
			}
			axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d19);
			AxisAlignedBB axisalignedbb13 = boundingBox;
			double d20 = y;
			AxisAlignedBB axisalignedbb9;
			for (Iterator iterator4 = list.iterator(); iterator4.hasNext(); d20 = axisalignedbb9.calculateYOffset(axisalignedbb13, d20)) {
				axisalignedbb9 = (AxisAlignedBB)iterator4.next();
			}
			axisalignedbb13 = axisalignedbb13.offset(0.0D, d20, 0.0D);
			double d21 = d6;
			AxisAlignedBB axisalignedbb10;
			for (Iterator iterator5 = list.iterator(); iterator5.hasNext(); d21 = axisalignedbb10.calculateXOffset(axisalignedbb13, d21)) {
				axisalignedbb10 = (AxisAlignedBB)iterator5.next();
			}
			axisalignedbb13 = axisalignedbb13.offset(d21, 0.0D, 0.0D);
			double d22 = d8;
			AxisAlignedBB axisalignedbb11;
			for (Iterator iterator6 = list.iterator(); iterator6.hasNext(); d22 = axisalignedbb11.calculateZOffset(axisalignedbb13, d22)) {
				axisalignedbb11 = (AxisAlignedBB)iterator6.next();
			}
			axisalignedbb13 = axisalignedbb13.offset(0.0D, 0.0D, d22);
			double d23 = d18 * d18 + d19 * d19;
			double d13 = d21 * d21 + d22 * d22;
			if (d23 > d13)
			{
				x = d18;
				z = d19;
				boundingBox = axisalignedbb4;
			}
			else
			{
				x = d21;
				z = d22;
				boundingBox = axisalignedbb13;
			}
			y = -stepHeight;
			AxisAlignedBB axisalignedbb12;
			for (Iterator iterator7 = list.iterator(); iterator7.hasNext(); y = axisalignedbb12.calculateYOffset(boundingBox, y)) {
				axisalignedbb12 = (AxisAlignedBB)iterator7.next();
			}
			if (d14 * d14 + d11 * d11 >= x * x + z * z)
			{
				x = d14;
				y = d10;
				z = d11;
			}
		}

		boolean isCollidedPositiveX = d6 > x;
		boolean isCollidedNegativeX = d6 < x;
		boolean isCollidedPositiveY = d7 > y;
		boolean isCollidedNegativeY = d7 < y;
		boolean isCollidedPositiveZ = d8 > z;
		boolean isCollidedNegativeZ = d8 < z;

		int result = 0;
		if(isCollidedPositiveX)
			result += CollidedPositiveX;
		if(isCollidedNegativeX)
			result += CollidedNegativeX;
		if(isCollidedPositiveY)
			result += CollidedPositiveY;
		if(isCollidedNegativeY)
			result += CollidedNegativeY;
		if(isCollidedPositiveZ)
			result += CollidedPositiveZ;
		if(isCollidedNegativeZ)
			result += CollidedNegativeZ;
		return result;
	}

	public final static int CollidedPositiveX = 1;
	public final static int CollidedNegativeX = 2;
	public final static int CollidedPositiveY = 4;
	public final static int CollidedNegativeY = 8;
	public final static int CollidedPositiveZ = 16;
	public final static int CollidedNegativeZ = 32;

	public boolean isSneaking()
	{
		return sp.isSneaking();
	}

	public void correctOnUpdate(boolean isSmall, boolean reverseMaterialAcceleration)
	{
		double d = sp.posX - sp.prevPosX;
		double d1 = sp.posZ - sp.prevPosZ;
		float f = MathHelper.sqrt_double(d * d + d1 * d1);
		if(f < 0.05F && f > 0.02 && isSmall)
		{
			float f1 = ((float)Math.atan2(d1, d) * 180F) / 3.141593F - 90F;

			if(sp.swingProgress > 0.0F)
			{
				f1 = sp.rotationYaw;
			}
			float f4;
			for(f4 = f1 - sp.renderYawOffset; f4 < -180F; f4 += 360F) { }
			for(; f4 >= 180F; f4 -= 360F) { }
			float x = sp.renderYawOffset + f4 * 0.3F;
			float f5;
			for(f5 = sp.rotationYaw - x; f5 < -180F; f5 += 360F) { }
			for(; f5 >= 180F; f5 -= 360F) { }
			if(f5 < -75F)
			{
				f5 = -75F;
			}
			if(f5 >= 75F)
			{
				f5 = 75F;
			}
			sp.renderYawOffset = sp.rotationYaw - f5;
			if(f5 * f5 > 2500F)
			{
				sp.renderYawOffset += f5 * 0.2F;
			}
			for(; sp.renderYawOffset - sp.prevRenderYawOffset < -180F; sp.prevRenderYawOffset -= 360F) { }
			for(; sp.renderYawOffset - sp.prevRenderYawOffset >= 180F; sp.prevRenderYawOffset += 360F) { }
		}

		if(reverseMaterialAcceleration)
			reverseHandleMaterialAcceleration();
	}

	protected double getGapUnderneight()
	{
		return getBoundingBox().minY - getMaxPlayerSolidBetween(getBoundingBox().minY - 1.1D, getBoundingBox().minY, 0);
	}

	protected double getGapOverneight()
	{
		return getMinPlayerSolidBetween(getBoundingBox().maxY, getBoundingBox().maxY + 1.1D, 0) - getBoundingBox().maxY;
	}

	public double getOverGroundHeight(double maximum)
	{
		if(esp != null)
			return (getBoundingBox().minY - getMaxPlayerSolidBetween(getBoundingBox().minY - maximum, getBoundingBox().minY, 0));
		return (getBoundingBox().minY + 1D - getMaxPlayerSolidBetween(getBoundingBox().minY - maximum + 1D, getBoundingBox().minY + 1D, 0.1));
	}

	public Block getOverGroundBlockId(double distance)
	{
		int x = MathHelper.floor_double(sp.posX);
		int y = MathHelper.floor_double(getBoundingBox().minY);
		int z = MathHelper.floor_double(sp.posZ);
		int minY = y - (int)Math.ceil(distance);

		if(esp == null)
		{
			y++;
			minY++;
		}

		for(; y >= minY; y--)
		{
			Block block = getBlock(x, y, z);
			if(block != null)
				return block;
		}
		return null;
	}

	public void reverseHandleMaterialAcceleration()
	{
		World _this = sp.worldObj;
		AxisAlignedBB axisalignedbb = getBoundingBox().expand(0.0D, -0.40000000596046448D, 0.0D).contract(0.001D);
		Material material = Material.WATER;
		Entity entity = sp;

		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
		if (!_this.isAreaLoaded(new BlockPos(i, k, i1), new BlockPos(j, l, j1), true))
			return;

		Vec3d vec3 = new Vec3d(0.0D, 0.0D, 0.0D);
		for (int k1 = i; k1 < j; k1++)
			for (int l1 = k; l1 < l; l1++)
				for (int i2 = i1; i2 < j1; i2++)
				{
					BlockPos blockpos = new BlockPos(k1, l1, i2);
					IBlockState iblockstate = _this.getBlockState(blockpos);
					Block block = iblockstate.getBlock();
					if (iblockstate.getMaterial() == material)
					{
						double d0 = l1 + 1 - BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL).intValue());
						if (l >= d0)
						{
							vec3 = block.modifyAcceleration(_this, blockpos, entity, vec3);
						}
					}
				}

		if ((vec3.lengthVector() > 0.0D) && (entity.isPushedByWater()))
		{
			vec3 = vec3.normalize();
			double d1 = -0.014D; // instead +0.014D for reversal
			entity.motionX += vec3.xCoord * d1;
			entity.motionY += vec3.yCoord * d1;
			entity.motionZ += vec3.zCoord * d1;
		}
	}
}