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

package net.smart.moving.render;

import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.entity.*;

public class RenderPlayer extends net.smart.render.RenderPlayer implements IRenderPlayer
{
	public RenderPlayer(RenderManager renderManager)
	{
		this(renderManager, false);
	}

	public RenderPlayer(RenderManager renderManager, boolean b)
	{
		super(renderManager, b);

		render = new SmartMovingRender(this);
	}

	@Override
	public net.smart.render.IModelPlayer createModel(net.minecraft.client.model.ModelBiped existing, float f, boolean b)
	{
		if(existing instanceof net.minecraft.client.model.ModelPlayer)
			return new net.smart.moving.render.ModelPlayer(existing, f, b);
		return new net.smart.moving.render.ModelBiped(existing, f);
	}

	@Override
	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		render.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superRenderDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		super.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	protected void rotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		render.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRenderRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		super.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	protected void renderLivingAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		render.renderLivingAt(entityplayer, d, d1, d2);
	}

	@Override
	public void superRenderRenderLivingAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		super.renderLivingAt(entityplayer, d, d1, d2);
	}

	@Override
	public void renderName(AbstractClientPlayer entityplayer, double par2, double par4, double par6)
	{
		render.renderName(entityplayer, par2, par4, par6);
	}

	@Override
	public void superRenderRenderName(AbstractClientPlayer entityplayer, double par2, double par4, double par6)
	{
		super.renderName(entityplayer, par2, par4, par6);
	}

	@Override
	public RenderManager getMovingRenderManager()
	{
		return renderManager;
	}

	@Override
	public IModelPlayer getMovingModelBipedMain()
	{
		return (IModelPlayer)super.getModelBipedMain();
	}

	@Override
	public IModelPlayer getMovingModelArmorChestplate()
	{
		return (IModelPlayer)super.getModelArmorChestplate();
	}

	@Override
	public IModelPlayer getMovingModelArmor()
	{
		return (IModelPlayer)super.getModelArmor();
	}

	@Override
	public IModelPlayer[] getMovingModels()
	{
		if(allIModelPlayers == null)
			allIModelPlayers = new IModelPlayer[] { getMovingModelBipedMain(), getMovingModelArmorChestplate(), getMovingModelArmor() };
		return allIModelPlayers;
	}

	private IModelPlayer[] allIModelPlayers;

	private final SmartMovingRender render;
}