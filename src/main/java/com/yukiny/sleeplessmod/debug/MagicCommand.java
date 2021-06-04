package com.yukiny.sleeplessmod.debug;

import com.yukiny.sleeplessmod.entity.EntityWingCreeper;
import com.yukiny.sleeplessmod.others.Organizer;
import com.yukiny.sleeplessmod.others.player_extended_capability.PlayerExtendedCapabilities;
import com.yukiny.sleeplessmod.others.player_extended_capability.PlayerExtendedCapabilityProvider;
import com.yukiny.sleeplessmod.others.tick_manager.TickManagement;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MagicCommand extends CommandBase implements TickManagement {
    public MagicCommand() {

    }

    @Override
    public String getCommandName() {
        return "magic";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "magic <text>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return null;
    }

    @Override
    public int compareTo(ICommand p_compareTo_1_) {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) return;

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        try{
            if (args[0].equals("ores")) {
                PlayerExtendedCapabilities player_cap = player.getCapability(PlayerExtendedCapabilityProvider.PLAYER_CAP, null);
                player_cap.setFusaiValue(player_cap.getFusaiValue() + 200);
                player.addChatComponentMessage(new TextComponentString("Can see Ores for 1000 ticks"));
                Organizer.isOreSee = true;
                setValueToTickManagerWithRender(() -> {
                    Organizer.isOreSee = false;
                }, 1000);
            }
            if(args[0].equals("fire")){
                PlayerExtendedCapabilities player_cap = player.getCapability(PlayerExtendedCapabilityProvider.PLAYER_CAP, null);
                player_cap.setFusaiValue(player_cap.getFusaiValue() + 100);
                Organizer.fusaiValue = player_cap.getFusaiValue();

                double x = player.getPositionVector().xCoord;
                double y = player.getPositionVector().yCoord;
                double z = player.getPositionVector().zCoord;
                World world = player.getEntityWorld();
                List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x - 20, y - 20, z - 20, x + 20, y + 20, z + 20));
                System.out.println(entities.size());
                System.out.println("tset");
                for (Entity e : entities){
                    if(!world.isRemote){
                        EntityLightningBolt bolt = new EntityLightningBolt(world, e.posX, e.posY, e.posZ, false);
                        world.spawnEntityInWorld(bolt);
                    }
                }
            }
            if(args[0].equals("break")){
                World world = player.getEntityWorld();
                BlockPos pos = player.getPosition();
                IBlockState blockState = world.getBlockState(pos);
                PlayerExtendedCapabilities player_cap = player.getCapability(PlayerExtendedCapabilityProvider.PLAYER_CAP, null);
                player_cap.setFusaiValue(player_cap.getFusaiValue() + 100);
                Organizer.fusaiValue = player_cap.getFusaiValue();
//                Block block = blockState.getBlock();
                for(int y = 0; y < 7; y++){
                    for(int z = 0; z < 7; z++){
                        for(int x = 0; x < 7; x++){
                            final BlockPos tempPos = new BlockPos(pos.getX() + x, pos.getY() - y, pos.getZ() + z);
                            setValueToTickManager(() -> {
                                World tempWorld = Minecraft.getMinecraft().getIntegratedServer().getEntityWorld();
                                if(!tempWorld.isRemote){
                                    tempWorld.getBlockState(tempPos).getBlock().dropBlockAsItem(tempWorld, tempPos, tempWorld.getBlockState(tempPos), 0);
                                    tempWorld.setBlockToAir(tempPos);
                                }
                            }, 5 * (x + y + z));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
