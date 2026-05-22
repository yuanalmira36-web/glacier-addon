package com.example.addon.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SusChunk extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgFilters = settings.createGroup("Target Blocks");
    private final SettingGroup sgRender = settings.createGroup("Render Settings");

    private final Setting<Integer> sensitivity = sgGeneral.add(new IntSetting.Builder().name("sensitivity").defaultValue(3).min(1).max(50).build());
    private final Setting<Boolean> chatAlert = sgGeneral.add(new BoolSetting.Builder().name("chat-alerts").defaultValue(true).build());
    private final Setting<Boolean> detectKelpVines = sgFilters.add(new BoolSetting.Builder().name("kelp-and-vines").defaultValue(true).build());
    private final Setting<Boolean> detectAmethyst = sgFilters.add(new BoolSetting.Builder().name("amethyst").defaultValue(true).build());
    private final Setting<Boolean> detectBamboo = sgFilters.add(new BoolSetting.Builder().name("bamboo").defaultValue(true).build());
    private final Setting<Boolean> detectBeeNest = sgFilters.add(new BoolSetting.Builder().name("bee-nests").defaultValue(true).build());

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").defaultValue(new SettingColor(255, 140, 0, 45)).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").defaultValue(new SettingColor(255, 140, 0, 220)).build());

    private final Map<ChunkPos, Integer> updateCounter = new HashMap<>();
    private final Set<ChunkPos> susChunks = new HashSet<>();

    public SusChunk() {
        super(Categories.World, "sus-chunk", "Highlights organic growth stashes.");
    }

    @Override
    public void onActivate() {
        susChunks.clear();
        updateCounter.clear();
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (mc.world == null) return;
        if (event.packet instanceof BlockUpdateS2CPacket packet) {
            Block block = packet.getState().getBlock();
            if (isTargetBlock(block)) {
                ChunkPos pos = new ChunkPos(packet.getPos());
                if (susChunks.contains(pos)) return;

                int currentCount = updateCounter.getOrDefault(pos, 0) + 1;
                updateCounter.put(pos, currentCount);

                if (currentCount >= sensitivity.get()) {
                    susChunks.add(pos);
                    if (chatAlert.get() && mc.player != null) {
                        ChatUtils.info("Sus Chunk triggered at: X: " + pos.getCenterX() + ", Z: " + pos.getCenterZ());
                    }
                }
            }
        }
    }

    private boolean isTargetBlock(Block block) {
        if (detectKelpVines.get() && (block == Blocks.KELP || block == Blocks.KELP_PLANT || block == Blocks.CAVE_VINES || block == Blocks.CAVE_VINES_PLANT)) return true;
        if (detectAmethyst.get() && (block == Blocks.AMETHYST_CLUSTER || block == Blocks.BUDDING_AMETHYST)) return true;
        if (detectBamboo.get() && block == Blocks.BAMBOO) return true;
        if (detectBeeNest.get() && (block == Blocks.BEE_NEST || block == Blocks.BEEHIVE)) return true;
        return false;
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (susChunks.isEmpty() || mc.world == null) return;
        for (ChunkPos chunk : susChunks) {
            event.renderer.box(chunk.getStartX(), mc.world.getBottomY(), chunk.getStartZ(), chunk.getEndX() + 1, mc.world.getTopY(), chunk.getEndZ() + 1, sideColor.get(), lineColor.get(), ShapeMode.Both, 0);
        }
    }
}
