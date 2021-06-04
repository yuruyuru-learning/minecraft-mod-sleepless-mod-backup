package com.yukiny.sleeplessmod.events;

import com.yukiny.sleeplessmod.SleeplessMod;
import com.yukiny.sleeplessmod.others.Organizer;
import com.yukiny.sleeplessmod.others.player_extended_capability.PlayerExtendedCapabilities;
import com.yukiny.sleeplessmod.others.player_extended_capability.PlayerExtendedCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerExtendedCapabilityHandler {
    public static final ResourceLocation PLAYER_CAP = new ResourceLocation(SleeplessMod.MODID, "player_cap");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event){
        if(!(event.getObject() instanceof EntityPlayer)) return;

        event.addCapability(PLAYER_CAP, new PlayerExtendedCapabilityProvider());
    }

    @SubscribeEvent
    public void onPlayerAwake(PlayerWakeUpEvent event){
        EntityPlayer player = event.getEntityPlayer();
        PlayerExtendedCapabilities player_cap = player.getCapability(PlayerExtendedCapabilityProvider.PLAYER_CAP, null);
        player_cap.setFusaiValue(player_cap.getFusaiValue() - 300);
        Organizer.fusaiValue = player_cap.getFusaiValue();
    }
}
