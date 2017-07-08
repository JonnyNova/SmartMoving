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

package net.smart.moving.render.playerapi;

import api.player.render.RenderPlayerAPI;
import api.player.render.RenderPlayerBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.smart.moving.render.IModelPlayer;
import net.smart.moving.render.IRenderPlayer;
import net.smart.moving.render.SmartMovingRender;
import net.smart.render.SmartRenderInstall;
import net.smart.utilities.Reflect;

import java.lang.reflect.Field;

public class SmartMovingRenderPlayerBase extends RenderPlayerBase implements IRenderPlayer
{
	public SmartMovingRenderPlayerBase(RenderPlayerAPI renderPlayerAPI)
	{
		super(renderPlayerAPI);
	}

	public SmartMovingRender getRenderModel()
	{
		if(render == null)
			render = new SmartMovingRender(this);
		return render;
	}

	@Override
	public void doRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		getRenderModel().doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void superRenderDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		super.doRender(entityplayer, d, d1, d2, f, renderPartialTicks);
	}

	@Override
	public void rotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		getRenderModel().rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void superRenderRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		super.rotateCorpse(entityplayer, totalTime, actualRotation, f2);
	}

	@Override
	public void renderLivingAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		getRenderModel().renderLivingAt(entityplayer, d, d1, d2);
	}

	@Override
	public void superRenderRenderLivingAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		super.renderLivingAt(entityplayer, d, d1, d2);
	}

	@Override
	public void renderName(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		getRenderModel().renderName(entityplayer, d, d1, d2);
	}

	@Override
	public void superRenderRenderName(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		super.renderName(entityplayer, d, d1, d2);
	}

	@Override
	public RenderManager getMovingRenderManager()
	{
		return renderPlayerAPI.getRenderManagerField();
	}

	public boolean isRenderedWithBodyTopAlwaysInAccelerateDirection()
	{
		SmartMovingRender render = getRenderModel();
		return render.modelBipedMain.isFlying || render.modelBipedMain.isSwim || render.modelBipedMain.isDive || render.modelBipedMain.isHeadJump;
	}

	@Override
	public IModelPlayer getMovingModelArmor()
	{
		return SmartMoving.getPlayerBase(renderPlayer.getMainModel());
	}

	@Override
	public IModelPlayer getMovingModelArmorChestplate()
	{
		for (Object layer : renderPlayerAPI.getLayerRenderersField())
			if (layer instanceof LayerArmorBase)
				return SmartMoving.getPlayerBase((net.minecraft.client.model.ModelBiped)Reflect.GetField(_modelArmorChestplate, layer));
		return null;
	}

	@Override
	public IModelPlayer getMovingModelBipedMain()
	{
		for (Object layer : renderPlayerAPI.getLayerRenderersField())
			if (layer instanceof LayerArmorBase)
				return SmartMoving.getPlayerBase((net.minecraft.client.model.ModelBiped)Reflect.GetField(_modelArmor, layer));
		return null;
	}

	@Override
	public IModelPlayer[] getMovingModels()
	{
		net.minecraft.client.model.ModelBiped[] modelPlayers = api.player.model.ModelPlayerAPI.getAllInstances();
		if(allModelPlayers != null && (allModelPlayers == modelPlayers || modelPlayers.length == 0 && allModelPlayers.length == 0))
			return allIModelPlayers;

		allModelPlayers = modelPlayers;
		allIModelPlayers = new IModelPlayer[modelPlayers.length];
		for(int i=0; i<allIModelPlayers.length; i++)
			allIModelPlayers[i] = SmartMoving.getPlayerBase(allModelPlayers[i]);
		return allIModelPlayers;
	}

	private net.minecraft.client.model.ModelBiped[] allModelPlayers;
	private IModelPlayer[] allIModelPlayers;

	private SmartMovingRender render;

	private final static Field _modelArmorChestplate = Reflect.GetField(LayerArmorBase.class, SmartRenderInstall.LayerArmorBase_modelArmorChestplate);
	private final static Field _modelArmor = Reflect.GetField(LayerArmorBase.class, SmartRenderInstall.LayerArmorBase_modelArmor);
}