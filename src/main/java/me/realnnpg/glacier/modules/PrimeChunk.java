package com.example.addon.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.ChunkPos;
import meteordevelopment.orbit.EventHandler;

import java.util.HashSet;
import java.util.Set;

public class PrimeChunk extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgFilters = settings.createGroup("Valuable Blocks");
    private final SettingGroup sgRender = settings.createGroup("Render Settings");

    private final Setting<Boolean> chatAlert = sgGeneral.add(new BoolSetting.Builder().name("chat-alerts").defaultValue(true).build());
    private final Setting<Boolean> detectSpawners = sgFilters.add(new BoolSetting.Builder().name("spawners").defaultValue(true).build());
    private final Setting<Boolean> detectValuableBlocks = sgFilters.add(new BoolSetting.Builder().name("hcf-blocks").defaultValue(true).build());
    private final Setting<Boolean> detectShulkers = sgFilters.add(new BoolSetting.Builder().name("shulkers").defaultValue(true).build());
    private final Setting<Boolean> detectEnchanting = sgFilters.add(new BoolSetting.Builder().name("enchanting-tables").defaultValue(true).build());

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").defaultValue(new SettingColor(0, 255, 255, 45)).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").defaultValue(new SettingColor(0, 255, 255, 220)).build());

    private final Set<ChunkPos> primeChunks = new HashSet<>();

    public PrimeChunk() {
        super(Categories.World, "prime-chunk", "Highlights chunks containing high-tier rich bases.");
    }

    @Override
    public void onActivate() {
        primeChunks.clear();
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (mc.world == null) return;
        if (event.packet instanceof BlockUpdateS2CPacket packet) {
            Block block = packet.getState().getBlock();
            if (isPrimeBlock(block)) {
                ChunkPos pos = new ChunkPos(packet.getPos());
                if (!primeChunks.contains(pos)) {
                    primeChunks.add(pos);
                    if (chatAlert.get() && mc.player != null) {
                        ChatUtils.info("⭐ Prime Chunk detected at: X: " + pos.getCenterX() + ", Z: " + pos.getCenterZ());
                    }
                }
            }
        }
    }

    private boolean isPrimeBlock(Block block) {
        if (detectSpawners.get() && block == Blocks.SPAWNER) return true;
        if (detectShulkers.get() && block instanceof ShulkerBoxBlock) return true;
        if (detectEnchanting.get() && block == Blocks.ENCHANTING_TABLE) return true;
        if (detectValuableBlocks.get() && (block == Blocks.DIAMOND_BLOCK || block == Blocks.NETHERITE_BLOCK || block == Blocks.GOLD_BLOCK)) return true;
        return false;
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (primeChunks.isEmpty() || mc.world == null) return;
        for (ChunkPos chunk : primeChunks) {
            event.renderer.box(chunk.getStartX(), mc.world.getBottomY(), chunk.getStartZ(), chunk.getEndX() + 1, mc.world.getTopY(), chunk.getEndZ() + 1, sideColor.get(), lineColor.get(), ShapeMode.Both, 0);
        }
    }
}
