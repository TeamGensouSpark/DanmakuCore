/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore

import java.util

import scala.collection.JavaConverters._

import net.katsstuff.danmakucore.capability.callableentity.CapabilityCallableEntityS
import net.katsstuff.danmakucore.capability.dancoredata.{CapabilityDanCoreDataS, DanmakuCoreDataHandler}
import net.katsstuff.danmakucore.capability.danmakuhit.{CapabilityDanmakuHitBehaviorS, DanmakuHitBehaviorHandler}
import net.katsstuff.danmakucore.capability.owner.CapabilityHasOwnerS
import net.katsstuff.danmakucore.client.ClientProxy
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.ShotData
import net.katsstuff.danmakucore.entity.EntityFallingData
import net.katsstuff.danmakucore.handler.PlayerChangeHandler
import net.katsstuff.danmakucore.helper.LogHelper
import net.katsstuff.danmakucore.item.ItemDanmaku
import net.katsstuff.danmakucore.lib.LibMod
import net.katsstuff.danmakucore.lib.data.LibItems
import net.katsstuff.danmakucore.network.{DanCorePacketHandler, ShotDataSerializer}
import net.katsstuff.danmakucore.server.commands.DanmakuCoreCmd
import net.katsstuff.danmakucore.shape.ShapeHandler
import net.katsstuff.mirror.data.Vector3
import net.minecraft.block.BlockDispenser
import net.minecraft.dispenser.IBlockSource
import net.minecraft.item.ItemStack
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.{FMLCommonHandler, Mod, SidedProxy}
import net.minecraftforge.fml.relauncher.Side

@Mod(
  modid = LibMod.Id,
  name = LibMod.Name,
  version = LibMod.Version,
  modLanguage = "scala",
  dependencies = "required-after:mirror@[0.2.0,);"
)
object DanmakuCore {
  MinecraftForge.EVENT_BUS.register(CommonProxy)

  if (FMLCommonHandler.instance().getSide == Side.CLIENT) {
    MinecraftForge.EVENT_BUS.register(ClientProxy)
  }

  def instance: DanmakuCore.type = this

  /**
    * Construct a ResourceLocation with DanmakuCore as the mod.
    */
  def resource(path: String) = new ResourceLocation(LibMod.Id, path)

  def spawnDanmaku(states: Seq[DanmakuState]): Unit = proxy.spawnDanmaku(states)

  def spawnDanmaku(states: util.List[DanmakuState]): Unit = proxy.spawnDanmaku(states.asScala)

  @SidedProxy(clientSide = LibMod.ClientProxy, serverSide = LibMod.CommonProxy, modId = LibMod.Id)
  var proxy: CommonProxy = _

  @Mod.EventHandler
  def preInit(event: FMLPreInitializationEvent): Unit = {
    LogHelper.setLog(event.getModLog)
    DataSerializers.registerSerializer(ShotDataSerializer)
    DataSerializers.registerSerializer(EntityFallingData.DataTypeSerializer)
    proxy.registerColors()
    proxy.registerRenderers()
    CapabilityDanCoreDataS.register()
    CapabilityDanmakuHitBehaviorS.register()
    CapabilityCallableEntityS.register()
    CapabilityHasOwnerS.register()
  }

  @Mod.EventHandler
  def init(event: FMLInitializationEvent): Unit = {
    proxy.bakeRenderModels()
    DanCorePacketHandler.load()
    MinecraftForge.EVENT_BUS.register(ShapeHandler)
    MinecraftForge.EVENT_BUS.register(DanmakuCoreDataHandler)
    MinecraftForge.EVENT_BUS.register(DanmakuHitBehaviorHandler)
    MinecraftForge.EVENT_BUS.register(PlayerChangeHandler)
    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
      LibItems.DANMAKU,
      (source: IBlockSource, stack: ItemStack) => {
        val iPos         = BlockDispenser.getDispensePosition(source)
        val directionVec = source.getBlockState.getValue(BlockDispenser.FACING).getDirectionVec
        val pos          = Vector3(iPos.getX, iPos.getY, iPos.getZ)
        val direction    = Vector3(directionVec.getX, directionVec.getY, directionVec.getZ)
        if (ItemDanmaku.shootDanmaku(stack, source.getWorld, None, None, alternateMode = false, pos, direction, 0)) {
          stack.shrink(1)
        }
        val shot = ShotData.fromNBTItemStack(stack)
        shot.form.playShotSound(source.getWorld, pos, shot)
        stack
      }
    )
  }

  @Mod.EventHandler
  def postInit(event: FMLPostInitializationEvent): Unit =
    proxy.registerItemColors()

  @Mod.EventHandler
  def serverStarting(event: FMLServerStartingEvent): Unit = {
    proxy.serverStarting(event)
    event.registerServerCommand(new DanmakuCoreCmd)
  }

  @Mod.EventHandler
  def serverStopped(event: FMLServerStoppedEvent): Unit =
    proxy.serverStopped(event)
}
