/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.danmakucore.client.shader.DanCoreShaderProgram
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] abstract class AbstractFormCrystal(name: String) extends FormGeneric(name) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {
    @SideOnly(Side.CLIENT)
    override def renderLegacy(
        danmaku: DanmakuState,
        x: Double,
        y: Double,
        z: Double,
        orientation: Quat,
        partialTicks: Float,
        manager: RenderManager
    ): Unit = {
      val shotData = danmaku.shot
      val sizeX    = shotData.sizeX
      val sizeY    = shotData.sizeY
      val sizeZ    = shotData.sizeZ
      val color    = shotData.color
      val dist     = x * x + y * y + z * z

      GlStateManager.rotate(orientation.toQuaternion)
      GL11.glScalef((sizeX / 3) * 2, (sizeY / 3) * 2, sizeZ)

      createCrystal(0xFFFFFF, 1F, dist)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)

      createCrystal(color, 0.3F, dist)

      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }

    override def renderShaders(
        danmaku: DanmakuState,
        x: Double,
        y: Double,
        z: Double,
        orientation: Quat,
        partialTicks: Float,
        manager: RenderManager,
        shaderProgram: DanCoreShaderProgram
    ): Unit = {
      val shotData = danmaku.shot
      val sizeX    = shotData.sizeX
      val sizeY    = shotData.sizeY
      val sizeZ    = shotData.sizeZ
      val color    = shotData.color

      DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, color)
      GlStateManager.rotate(orientation.toQuaternion)
      GL11.glScalef((sizeX / 3) * 2, (sizeY / 3) * 2, sizeZ)

      val dist = x * x + y * y + z * z
      createCrystal(0xFFFFFF, 1F, dist)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)

      createCrystal(DanCoreRenderHelper.OverwriteColor, 0.3F, dist)

      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }
  }

  @SideOnly(Side.CLIENT)
  protected def createCrystal(color: Int, alpha: Float, dist: Double): Unit
}
