/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form;

import org.lwjgl.opengl.GL11;

import net.katsstuff.danmakucore.client.helper.RenderHelper;
import net.katsstuff.danmakucore.lib.LibFormName;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A two sided normal crystal. Pointy on both sides.
 */
public class FormCrystal1 extends AbstractFormCrystal {

	public FormCrystal1() {
		super(LibFormName.CRYSTAL1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void createCrystal(int color, float alpha) {
		GL11.glPushMatrix();

		GL11.glTranslatef(0F, 0F, 1F);
		RenderHelper.drawCone(color, alpha);
		GL11.glTranslatef(0F, 0F, -1F);
		RenderHelper.drawCylinder(color, alpha);
		GL11.glRotatef(180, 0F, 1F, 0F);
		GL11.glTranslatef(0F, 0F, 1F);
		RenderHelper.drawCone(color, alpha);

		GL11.glPopMatrix();
	}
}
