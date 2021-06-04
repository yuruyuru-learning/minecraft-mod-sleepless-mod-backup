package com.yukiny.sleeplessmod.events;

import com.yukiny.sleeplessmod.others.tick_manager.TickManagement;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class DirtBreakEventHandler implements TickManagement {
    private static final int MAX_DEPTH = 5;
    private static final Item[] EVENT_ITEM = {Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE};
    private static final Block[] EVENT_BLOCK = {Blocks.DIRT};
    private static final Block[] EVENT_BREAKABLE_BLOCK = {Blocks.DIRT};
    private static final int BREAK_DELAY = 10;

    @SubscribeEvent
    public void OnBlockBreak(BlockEvent.BreakEvent event){
        EntityPlayer player = event.getPlayer();
        if(player == null){
            return;
        }
        if(player.getHeldItemMainhand() == null){
            return;
        }
        Item item = player.getHeldItemMainhand().getItem();
        if(!Arrays.asList(EVENT_ITEM).contains(item)){
            return;
        }
        Block block = event.getState().getBlock();
        if(!Arrays.asList(EVENT_BLOCK).contains(block)){
            return;
        }
        System.out.println("sdfjkj5");
        BlockPos pos = event.getPos();
        event.setCanceled(true);
        breakBlock(event.getWorld(), pos, 1);
        System.out.println("sdfjkj6");
    }

    private void breakBlock(World world, BlockPos pos, int depth){
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(!Arrays.asList(EVENT_BREAKABLE_BLOCK).contains(block)){
            return;
        }
        for(int y = 0; y < MAX_DEPTH; y++){
            for(int z = 0; z < MAX_DEPTH; z++){
                for(int x = 0; x < MAX_DEPTH; x++){
                    final BlockPos tempPos = new BlockPos(pos.getX() + x, pos.getY() - y, pos.getZ() + z);
                    setValueToTickManager(() -> {
                        World tempWorld = Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
                        if(!tempWorld.isRemote){
                            tempWorld.getBlockState(tempPos).getBlock().dropBlockAsItem(tempWorld, tempPos, tempWorld.getBlockState(tempPos), 0);
                            tempWorld.setBlockToAir(tempPos);
                        }
                    }, BREAK_DELAY * (x + y + z));
                }
            }
        }
    }
}