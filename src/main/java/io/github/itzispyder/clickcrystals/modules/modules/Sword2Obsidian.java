package io.github.itzispyder.clickcrystals.modules.modules;

import io.github.itzispyder.clickcrystals.events.EventHandler;
import io.github.itzispyder.clickcrystals.events.Listener;
import io.github.itzispyder.clickcrystals.events.events.PacketSendEvent;
import io.github.itzispyder.clickcrystals.modules.Categories;
import io.github.itzispyder.clickcrystals.modules.Module;
import io.github.itzispyder.clickcrystals.util.BlockUtils;
import io.github.itzispyder.clickcrystals.util.HotbarUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;

/**
 * Sword2Obsidian module
 */
public class Sword2Obsidian extends Module implements Listener {

    private static long cooldown;

    public Sword2Obsidian() {
        super("Sword2Obsidian", Categories.CRYSTALLING,"Punch the ground with your sword to switch to obsidian.");
    }

    @Override
    protected void onEnable() {
        system.addListener(this);
    }

    @Override
    protected void onDisable() {
        system.removeListener(this);
    }

    @EventHandler
    private void onPacketSend(PacketSendEvent e) {
        if (e.getPacket() instanceof PlayerActionC2SPacket packet) {
            final BlockPos pos = packet.getPos();

            if (packet.getAction() != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) return;
            if (BlockUtils.isCrystallabe(pos)) return;
            if (!HotbarUtils.has(Items.END_CRYSTAL)) return;

            if (cooldown > System.currentTimeMillis()) return;
            cooldown = System.currentTimeMillis() + (50 * 4);

            if (HotbarUtils.isForClickCrystal()) {
                e.setCancelled(true);
                HotbarUtils.search(Items.OBSIDIAN);
                BlockUtils.interact(pos,packet.getDirection());
            }
        }
    }
}
