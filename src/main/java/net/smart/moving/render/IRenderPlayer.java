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

public interface IRenderPlayer
{
	void superRenderDoRender(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks);

	void superRenderRotateCorpse(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2);

	void superRenderRenderLivingAt(AbstractClientPlayer entityplayer, double d, double d1, double d2);

	void superRenderRenderName(AbstractClientPlayer par1EntityPlayer, double par2, double par4, double par6);

	RenderManager getMovingRenderManager();

	IModelPlayer getMovingModelBipedMain();

	IModelPlayer getMovingModelArmorChestplate();

	IModelPlayer getMovingModelArmor();

	IModelPlayer[] getMovingModels();
}