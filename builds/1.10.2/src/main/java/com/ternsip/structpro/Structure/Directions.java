package com.ternsip.structpro.Structure;

import net.minecraft.block.*;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.Map;


/**
 * This class transforms block metadata (rotate n times and flip)
 * @author Ternsip (ternsip@gmail.com)
 */
class Directions {

    private static final int UNKNOWN = 0x00;
    static final int SOUTH = 0x01;
    static final int WEST = 0x02;
    static final int EAST = 0x04;
    static final int NORTH = 0x08;
    private static final int UP = 0x10;
    private static final int DOWN = 0x20;

    private static final Map<BlockType, HashMap<Integer, Integer>> metaToDirection = new HashMap<BlockType, HashMap<Integer, Integer>>();
    private static final Map<BlockType, HashMap<Integer, Integer>> directionToMeta = new HashMap<BlockType, HashMap<Integer, Integer>>();
    private static final Map<BlockType, Integer> masks = new HashMap<BlockType, Integer>();

    public enum BlockType {
        LOG, BED, RAIL_NORMAL, RAIL_CURVE, RAIL_ASC, RAIL_POWERED, RAIL_POWERED_ASC, TORCH, STAIR, CHEST, SIGNPOST,
        DOOR, LEVER, BUTTON, REDSTONE_REPEATER, TRAPDOOR, VINE, ANVIL,
        MUSHROOM, MUSHROOM_CAP_CORNER, MUSHROOM_CAP_SIDE, IDLE
    }

    static BlockType getBlockType(Block block, int meta) {
        if (block instanceof BlockBed || block instanceof BlockPumpkin || block instanceof BlockFenceGate || block instanceof BlockEndPortalFrame || block instanceof BlockTripWireHook || block instanceof BlockCocoa) {
            return BlockType.BED;
        }
        if (block instanceof BlockRail) {
            return (meta < 0x2) ? BlockType.RAIL_NORMAL : (meta < 0x6) ? BlockType.RAIL_ASC : BlockType.RAIL_CURVE;
        }
        if (block instanceof BlockRailPowered || block instanceof BlockRailDetector) {
            return (meta < 0x2) ? BlockType.RAIL_POWERED : BlockType.RAIL_POWERED_ASC;
        }
        if (block instanceof BlockStairs) {
            return BlockType.STAIR;
        }
        if (    block instanceof BlockChest ||
                block instanceof BlockEnderChest ||
                block instanceof BlockFurnace ||
                block instanceof BlockLadder ||
                Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.WALL_SIGN) ||
                block instanceof BlockSkull ||
                block instanceof BlockDispenser ||
                block instanceof BlockPistonBase ||
                block instanceof BlockPistonExtension ||
                block instanceof BlockHopper) {
            return BlockType.CHEST;
        }
        if (Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.STANDING_SIGN)) {
            return BlockType.SIGNPOST;
        }
        if (block instanceof BlockDoor) {
            return BlockType.DOOR;
        }
        if (block instanceof BlockButton) {
            return BlockType.BUTTON;
        }
        if (block instanceof BlockRedstoneRepeater || block instanceof BlockRedstoneComparator) {
            return BlockType.REDSTONE_REPEATER;
        }
        if (block instanceof BlockTrapDoor) {
            return BlockType.TRAPDOOR;
        }
        if (block instanceof BlockVine) {
            return BlockType.VINE;
        }
        if (block instanceof BlockAnvil) {
            return BlockType.ANVIL;
        }
        if (block instanceof BlockLog) {
            return BlockType.LOG;
        }
        if (block instanceof BlockTorch) {
            return BlockType.TORCH;
        }
        if (block instanceof BlockLever) {
            return BlockType.LEVER;
        }
        if (block instanceof BlockHugeMushroom) {
            return (meta == 0x0 || meta > 0x9 || meta == 0x5) ? BlockType.MUSHROOM : (meta % 0x2 == 0 ? BlockType.MUSHROOM_CAP_SIDE : BlockType.MUSHROOM_CAP_CORNER);
        }
        return BlockType.IDLE;
    }

    static boolean isDoubleDirected(BlockType blockType) {
        return blockType == BlockType.MUSHROOM_CAP_CORNER || blockType == BlockType.RAIL_CURVE;
    }

    static Integer getDirection(int meta, BlockType blockType) {
        if (metaToDirection.containsKey(blockType)) {
            HashMap<Integer, Integer> metaToDir = metaToDirection.get(blockType);
            if (metaToDir.containsKey(meta)) {
                return metaToDir.get(meta);
            }
        }
        return UNKNOWN;
    }

    static Integer getMeta(int defaultMeta, int direction, BlockType blockType) {
        if (directionToMeta.containsKey(blockType)) {
            HashMap<Integer, Integer> biMap = directionToMeta.get(blockType);
            if (biMap.containsKey(direction)) {
                return biMap.get(direction);
            }
        }
        return defaultMeta;
    }

    static int getMask(BlockType blockType) {
        return masks.containsKey(blockType) ? masks.get(blockType) : 0;
    }

    private static void addMappings(HashMap<Integer, Integer> metaToDir, BlockType blockType, int mask) {
        HashMap<Integer, Integer> dirToMeta = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : metaToDir.entrySet()) {
            dirToMeta.put(entry.getValue(), entry.getKey());
        }
        int[] from = {WEST, EAST, NORTH, SOUTH, UP, DOWN};
        int[] to = {EAST, WEST, SOUTH, NORTH, DOWN, UP};
        for (int k = 0; k < from.length; ++k) {
            if (!dirToMeta.containsKey(from[k]) && dirToMeta.containsKey(to[k])) {
                dirToMeta.put(from[k], dirToMeta.get(to[k]));
            }
        }
        metaToDirection.put(blockType, metaToDir);
        directionToMeta.put(blockType, dirToMeta);
        masks.put(blockType, mask);
    }

    static {

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, UP);
            put(0x4, EAST);
            put(0x8, SOUTH);
        }}, BlockType.LOG, 0xC);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, DOWN);
            put(0x1, UP);
            put(0x2, NORTH);
            put(0x3, SOUTH);
            put(0x4, WEST);
            put(0x5, EAST);
        }}, BlockType.CHEST, 0x7);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, SOUTH);
            put(0x1, NORTH);
            put(0x2, EAST);
            put(0x3, WEST);
        }}, BlockType.TRAPDOOR, 0x3);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, SOUTH);
            put(0x1, WEST);
            put(0x2, NORTH);
            put(0x3, EAST);
        }}, BlockType.BED, 0x3);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x1, SOUTH);
            put(0x2, WEST);
            put(0x4, NORTH);
            put(0x8, EAST);
        }}, BlockType.VINE, 0xF);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, NORTH);
            put(0x1, EAST);
        }}, BlockType.RAIL_NORMAL, 0xF);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x2, EAST);
            put(0x3, WEST);
            put(0x4, NORTH);
            put(0x5, SOUTH);
        }}, BlockType.RAIL_ASC, 0xF);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x6, EAST);
            put(0x7, SOUTH);
            put(0x8, WEST);
            put(0x9, NORTH);
        }}, BlockType.RAIL_CURVE, 0xF);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, NORTH);
            put(0x1, EAST);
        }}, BlockType.RAIL_POWERED, 0x7);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x2, EAST);
            put(0x3, WEST);
            put(0x4, NORTH);
            put(0x5, SOUTH);
        }}, BlockType.RAIL_POWERED_ASC, 0x7);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, EAST);
            put(0x1, WEST);
            put(0x2, SOUTH);
            put(0x3, NORTH);
        }}, BlockType.STAIR, 0x3);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x1, EAST);
            put(0x2, WEST);
            put(0x3, SOUTH);
            put(0x4, NORTH);
            put(0x5, UP);
        }}, BlockType.TORCH, 0xF);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, DOWN);
            put(0x1, EAST);
            put(0x2, WEST);
            put(0x3, SOUTH);
            put(0x4, NORTH);
            put(0x5, UP);
        }}, BlockType.BUTTON, 0x7);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, DOWN);
            put(0x1, EAST);
            put(0x2, WEST);
            put(0x3, SOUTH);
            put(0x4, NORTH);
            put(0x5, UP);
            put(0x6, UP);
            put(0x7, DOWN);
        }}, BlockType.LEVER, 0x7);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, WEST);
            put(0x1, NORTH);
            put(0x2, EAST);
            put(0x3, SOUTH);
        }}, BlockType.DOOR, 0x3);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, NORTH);
            put(0x1, EAST);
            put(0x2, SOUTH);
            put(0x3, WEST);
        }}, BlockType.REDSTONE_REPEATER, 0x3);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x0, SOUTH);
            put(0x1, EAST);
        }}, BlockType.ANVIL, 0x1);

        addMappings(new HashMap<Integer, Integer>(){{}}, BlockType.MUSHROOM, 0xF);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x1, WEST);
            put(0x3, NORTH);
            put(0x7, SOUTH);
            put(0x9, EAST);
        }}, BlockType.MUSHROOM_CAP_CORNER, 0xF);

        addMappings(new HashMap<Integer, Integer>(){{
            put(0x2, NORTH);
            put(0x4, WEST);
            put(0x6, EAST);
            put(0x8, SOUTH);
        }}, BlockType.MUSHROOM_CAP_SIDE, 0xF);


    }


}
