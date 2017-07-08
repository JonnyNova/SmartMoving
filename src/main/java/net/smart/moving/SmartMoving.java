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
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import static net.smart.render.SmartRenderUtilities.Half;
import static net.smart.render.SmartRenderUtilities.Quarter;
import static net.smart.render.SmartRenderUtilities.RadiantToAngle;

public abstract class SmartMoving extends SmartMovingBase
{
	public boolean isSlow;
	public boolean isFast;

	public boolean isClimbing;
	public boolean isHandsVineClimbing;
	public boolean isFeetVineClimbing;

	public boolean isClimbJumping;
	public boolean isClimbBackJumping;
	public boolean isWallJumping;
	public boolean isClimbCrawling;
	public boolean isCrawlClimbing;
	public boolean isCeilingClimbing;
	public boolean isRopeSliding;

	public boolean isDipping;
	public boolean isSwimming;
	public boolean isDiving;
	public boolean isLevitating;
	public boolean isHeadJumping;
	public boolean isCrawling;
	public boolean isSliding;
	public boolean isFlying;

	public int actualHandsClimbType;
	public int actualFeetClimbType;

	public int angleJumpType;

	public float heightOffset;

	private float spawnSlindingParticle;
	private float spawnSwimmingParticle;

	public SmartMoving(EntityPlayer sp, IEntityPlayerSP isp)
	{
		super(sp, isp);
	}

	public boolean isAngleJumping()
	{
		return angleJumpType > 1 && angleJumpType < 7;
	}

	public abstract boolean isJumping();

	public abstract boolean doFlyingAnimation();

	public abstract boolean doFallingAnimation();

	protected void spawnParticles(Minecraft minecraft, double playerMotionX, double playerMotionZ)
	{
		float horizontalSpeedSquare = 0;
		if(isSliding || isSwimming)
			horizontalSpeedSquare = (float)(playerMotionX * playerMotionX + playerMotionZ * playerMotionZ);

		if(isSliding)
		{
			int i = MathHelper.floor_double(sp.posX);
			int j = MathHelper.floor_double(sp.getEntityBoundingBox().minY - 0.1F);
			int k = MathHelper.floor_double(sp.posZ);
			Block block = getBlock(sp.worldObj, i, j, k);
			if(block != null)
			{
				double posY = sp.getEntityBoundingBox().minY + 0.1D;
				double motionX = -playerMotionX * 4D;
				double motionY = 1.5D;
				double motionZ = -playerMotionZ * 4D;

				spawnSlindingParticle += horizontalSpeedSquare;

				float maxSpawnSlindingParticle = Config._slideParticlePeriodFactor.value * 0.1F;
				while(spawnSlindingParticle > maxSpawnSlindingParticle)
				{
					double posX = sp.posX + getSpawnOffset();
					double posZ = sp.posZ + getSpawnOffset();
					sp.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX, posY, posZ, motionX, motionY, motionZ, new int[] { Block.getStateId(getState(i, j, k)) });
					spawnSlindingParticle -= maxSpawnSlindingParticle;
				}
			}
		}

		if(isSwimming)
		{
			float posY = MathHelper.floor_double(sp.getEntityBoundingBox().minY) + 1.0F;
			int x = (int)Math.floor(sp.posX);
			int y = (int)Math.floor(posY - 0.5);
			int z = (int)Math.floor(sp.posZ);

			boolean isLava = isLava(sp.worldObj.getBlockState(new BlockPos(x, y, z)));
			spawnSwimmingParticle += horizontalSpeedSquare;

			float maxSpawnSwimmingParticle = (isLava ? Config._lavaSwimParticlePeriodFactor.value : Config._swimParticlePeriodFactor.value) * 0.01F;
			while(spawnSwimmingParticle > maxSpawnSwimmingParticle)
			{
				double posX = sp.posX + getSpawnOffset();
				double posZ = sp.posZ + getSpawnOffset();
				Particle splash = isLava ?
						new ParticleSplash.Factory().getEntityFX(EnumParticleTypes.LAVA.getParticleID(), sp.worldObj, posX, posY, posZ, 0, 0.2, 0) :
						new ParticleSplash.Factory().getEntityFX(EnumParticleTypes.WATER_SPLASH.getParticleID(), sp.worldObj, posX, posY, posZ, 0, 0.2, 0);
				minecraft.effectRenderer.addEffect(splash);

				spawnSwimmingParticle -= maxSpawnSwimmingParticle;
			}
		}
	}

	private float getSpawnOffset()
	{
		return (sp.getRNG().nextFloat() - 0.5F) * 2F * sp.width;
	}

	protected void onStartClimbBackJump()
	{
		net.smart.render.SmartRenderRender.getPreviousRendererData(sp).rotateAngleY += isHeadJumping ? Half : Quarter;
		isClimbBackJumping = true;
	}

	protected void onStartWallJump(Float angle)
	{
		if (angle != null)
			net.smart.render.SmartRenderRender.getPreviousRendererData(sp).rotateAngleY = angle / RadiantToAngle;
		isWallJumping = true;
		sp.fallDistance = 0F;
	}
}