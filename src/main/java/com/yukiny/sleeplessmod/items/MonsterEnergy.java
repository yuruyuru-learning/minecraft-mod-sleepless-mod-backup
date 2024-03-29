package com.yukiny.sleeplessmod.items;

import com.yukiny.sleeplessmod.SleeplessMod;
import com.yukiny.sleeplessmod.others.Organizer;
import com.yukiny.sleeplessmod.others.tick_manager.TickManagement;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MonsterEnergy extends ItemFood implements TickManagement {

    public static final int HYPER_TICK_LENGTH_MONSTER = 2000;

    public MonsterEnergy(){
        super(1, 0.5f, false);
        setCreativeTab(SleeplessMod.sleeplessTab);
        setRegistryName("energy_drink_monster");
        setUnlocalizedName(SleeplessMod.MODID + "energy_drink_monster");
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        super.onFoodEaten(stack, worldIn, player);
        System.out.println("ttttt");
        Organizer.isHyper = true;
        setValueToTickManager(() -> {
            Organizer.isHyper = false;
        }, HYPER_TICK_LENGTH_MONSTER);
        if(worldIn.isRemote){
            return;
        }
        for(int i = 0; i < 10; i+=4) {
            for (int j = 0; j < 10; j+= 4) {
                EntityCreeper creeper = new EntityCreeper(worldIn);
                creeper.setPosition(player.getPositionVector().xCoord + i,player.getPositionVector().yCoord,player.getPositionVector().zCoord + j);
                worldIn.spawnEntityInWorld(creeper);
                System.out.println("test");
            }
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
