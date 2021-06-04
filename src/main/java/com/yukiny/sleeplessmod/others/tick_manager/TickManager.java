package com.yukiny.sleeplessmod.others.tick_manager;

import com.yukiny.sleeplessmod.SleeplessMod;
import com.yukiny.sleeplessmod.others.Organizer;
import com.yukiny.sleeplessmod.others.player_extended_capability.PlayerExtendedCapabilities;
import com.yukiny.sleeplessmod.others.player_extended_capability.PlayerExtendedCapabilityProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TickManager implements TickManagement{
    public static List<Runnable> runner = new ArrayList<>();
    public static List<Integer> delay = new ArrayList<>();
    public static List<Integer> delayCount = new ArrayList<>();

    public static List<Runnable> renderRunner = new ArrayList<>();
    public static List<Integer> renderDelay = new ArrayList<>();
    public static List<Integer> renderDelayCount = new ArrayList<>();

    private BufferedImage image;

    @SubscribeEvent
    public void updateTick(TickEvent.PlayerTickEvent event){
        for(int i = 0; i < delay.size(); i++){
            delayCount.set(i, delayCount.get(i) + 1);
            if(delayCount.get(i) > delay.get(i)){
                runner.get(i).run();
                runner.remove(i);
                delay.remove(i);
                delayCount.remove(i);
            }
        }
        if(Organizer.isWaitingForCapabilityUpdate) {
            if (event.player instanceof EntityPlayerMP) {
                PlayerExtendedCapabilities player_cap = event.player.getCapability(PlayerExtendedCapabilityProvider.PLAYER_CAP, null);
                player_cap.setFusaiValue(player_cap.getFusaiValue() + 1);
                Organizer.fusaiValue = player_cap.getFusaiValue();
                Organizer.isWaitingForCapabilityUpdate = false;
            }
        }
        if(!(Organizer.isHyper || Organizer.isRedBull)){
            Random random = new Random();
            if(random.nextFloat() < 0.002){
                System.out.println("testaaa");
                if(Organizer.fusaiValue < Organizer.MAX_FUSAI_VALUE / 7){
                    event.player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200));
                } else if (Organizer.fusaiValue < Organizer.MAX_FUSAI_VALUE / 7 * 2){
                    event.player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200));
                } else if (Organizer.fusaiValue < Organizer.MAX_FUSAI_VALUE / 7 * 3){
                    event.player.inventory.setInventorySlotContents(random.nextInt(20), null);
                } else if (Organizer.fusaiValue < Organizer.MAX_FUSAI_VALUE / 7 * 4){
                    event.player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200));
                } else if (Organizer.fusaiValue < Organizer.MAX_FUSAI_VALUE / 7 * 5){
                    canSnap = true;
                    setValueToTickManager(() -> {
                        TickManager.canSnap = false;
                    }, 20);
                } else if (Organizer.fusaiValue < Organizer.MAX_FUSAI_VALUE / 7 * 6){
                    event.player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200));
                } else {
                    event.player.onKillCommand();
                }
            }
        }
    }

    private static boolean canSnap;


    @SubscribeEvent
    public void onSnap(RenderGameOverlayEvent.Post event) {
        if (event.getType() != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if (!canSnap) {
                return;
            }
//        PhotoBlock.canSnap = false;

            image = null;
            image = ScreenShotHelper.createScreenshot(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(), Minecraft.getMinecraft().getFramebuffer());
            for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {
                    int rgb = image.getRGB(j, i);
                    Color color = Color.decode(String.valueOf(rgb));
                    int r = color.getRed();
                    int g = color.getBlue();
                    int b = color.getGreen();
                    r = Math.round(r / 2);
                    g = Math.round(g / 3 * 2);
                    Color colornew = new Color(r, g, b);
                    String hex = Integer.toHexString(colornew.getRGB()).substring(2);
                    image.setRGB(j, i, Integer.parseInt(hex, 16));
                }
            }
        }
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event){
        if(!canSnap){
            return;
        }
        if(image == null){
            return;
        }
        if (event.getType() != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if(Minecraft.getMinecraft().thePlayer == null){
                return;
            }
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if(Minecraft.getMinecraft().theWorld == null){
                return;
            }
            World world = Minecraft.getMinecraft().theWorld;
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (player != null && world != null && gui == null && !player.isSpectator()) {

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableBlend();

                int sizeX = event.getResolution().getScaledWidth();
                int sizeY = event.getResolution().getScaledHeight();

                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ZERO);

                DynamicTexture texture = new DynamicTexture(image);
                Minecraft.getMinecraft().getTextureManager().bindTexture(Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("nameTest", texture));

                int left = sizeX / 2 - 91;
                int top = sizeY - 39;

                drawDispTexture(0, 0, sizeX, sizeY);

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public void drawDispTexture(int x, int y, int width, int height) {
        float f = 0.00390625F * 256;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x + 0, y + height, -90.0F).tex(0, f).endVertex();
        vertexbuffer.pos(x + width, y + height, -90.0F).tex(f, f).endVertex();
        vertexbuffer.pos(x + width, y + 0, -90.0F).tex(f, 0).endVertex();
        vertexbuffer.pos(x + 0, y + 0, -90.0F).tex(0, 0).endVertex();
        tessellator.draw();
    }



    @SubscribeEvent
    public void renderTick(RenderWorldLastEvent event){
        for(int i = 0; i < renderDelay.size(); i++){
            renderDelayCount.set(i, renderDelayCount.get(i) + 1);
            if(renderDelayCount.get(i) > renderDelay.get(i)){
                renderRunner.get(i).run();
                renderRunner.remove(i);
                renderDelay.remove(i);
                renderDelayCount.remove(i);
            }
        }

        if(Organizer.isOreSee){
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            World world = Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
            for(int i = 0; i < 10; i++){
                for(int j = 0; j < 10; j++){
                    for(int k = 0; k < 10; k++){
                        BlockPos pos = new BlockPos(player.getPosition().getX() + i - 5, player.getPosition().getY() + j - 5, player.getPosition().getZ() + k - 5);
                        if(world.getBlockState(pos).getBlock() == Blocks.COAL_ORE ||
                                world.getBlockState(pos).getBlock() == Blocks.DIAMOND_ORE ||
                                world.getBlockState(pos).getBlock() == Blocks.IRON_ORE) {
                            GlStateManager.disableTexture2D();
                            GlStateManager.disableBlend();
                            GlStateManager.depthMask(true);
                            GL11.glLineWidth(2.5f);
                            double partialTicks = event.getPartialTicks();
                            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

                            Block blockb = Blocks.BEDROCK;
                            IBlockState blockState = blockb.getDefaultState();
//            AxisAlignedBB largeBB = blockState.getSelectedBoundingBox(player.getEntityWorld(), event.getTarget().getBlockPos()).expand(-0.005, -0.005, -0.005);
                            AxisAlignedBB largeBB = blockState.getSelectedBoundingBox(world, pos).expand(0.005, 0.005, 0.005).offset(-d0, -d1, -d2);
                            RenderGlobal.drawSelectionBoundingBox(largeBB, 1f, 0.3f, 0.3f, 1f);
                        }
                    }
                }
            }



        }

    }
}
