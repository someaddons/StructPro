package com.ternsip.structpro.universe.blocks;

import net.minecraft.block.*;

import java.util.HashMap;
import java.util.Map;


/**
 * Transforms block metadata
 * Are able to rotate n times and flip
 * Directions can be combined with each other using pipe
 * @author Ternsip
 */
@SuppressWarnings({"WeakerAccess"})
public class Directions {

    /** Block rotation type enumeration */
    public enum BlockType {
        LOG, BED, RAIL_NORMAL, RAIL_CURVE, RAIL_ASC, RAIL_POWERED, RAIL_POWERED_ASC, TORCH, STAIR, CHEST, SIGNPOST,
        DOOR, LEVER, BUTTON, REDSTONE_REPEATER, TRAPDOOR, VINE, ANVIL,
        MUSHROOM, MUSHROOM_CAP_CORNER, MUSHROOM_CAP_SIDE, IDLE
    }

    public static final int UNKNOWN = 0x00;
    public static final int SOUTH = 0x01;
    public static final int WEST = 0x02;
    public static final int EAST = 0x04;
    public static final int NORTH = 0x08;
    public static final int UP = 0x10;
    public static final int DOWN = 0x20;

    /** Mapping block rotation type to it meta to direction map */
    private static final Map<BlockType, HashMap<Integer, Integer>> metaToDirection = new HashMap<>();

    /** Mapping block rotation type to it direction to metadata map */
    private static final Map<BlockType, HashMap<Integer, Integer>> directionToMeta = new HashMap<>();

    /** Masks for each block rotation type */
    private static final Map<BlockType, Integer> masks = new HashMap<>();

    /**
     * Detect if block type is double directed
     * Double directed blocks have two directions for example south-west
     * @return Block double directed
     */
    public static boolean isDoubleDirected(BlockType blockType) {
        return blockType == BlockType.MUSHROOM_CAP_CORNER || blockType == BlockType.RAIL_CURVE;
    }

    /**
     * Get block direction by meta and block type
     * @param meta Block metadaa
     * @param blockType Block rotation type
     * @return Block direction
     */
    public static Integer getDirection(int meta, BlockType blockType) {
        if (metaToDirection.containsKey(blockType)) {
            HashMap<Integer, Integer> metaToDir = metaToDirection.get(blockType);
            if (metaToDir.containsKey(meta)) {
                return metaToDir.get(meta);
            }
        }
        return UNKNOWN;
    }

    /**
     * Get metadata by block and direction
     * @param defaultMeta Metadata that will be returned in unefined case
     * @param direction Block direction
     * @param blockType block rotation type
     */
    public static Integer getMeta(int defaultMeta, int direction, BlockType blockType) {
        if (directionToMeta.containsKey(blockType)) {
            HashMap<Integer, Integer> biMap = directionToMeta.get(blockType);
            if (biMap.containsKey(direction)) {
                return biMap.get(direction);
            }
        }
        return defaultMeta;
    }

    /**
     * Get block type by block state
     * @param uBlock Target block
     * @param meta Block metadata value
     * @return Block rotation type
     */
    public static BlockType getBlockType(UBlock uBlock, int meta) {
        Block block = uBlock.getBlock();
        if (    block instanceof BlockBed ||
                block instanceof BlockPumpkin ||
                block instanceof BlockFenceGate ||
                block instanceof BlockEndPortalFrame ||
                block instanceof BlockTripWireHook ||
                block instanceof BlockCocoa) {
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
                uBlock.getID() == UBlocks.WALL_SIGN.getID() ||
                block instanceof BlockSkull ||
                block instanceof BlockDispenser ||
                block instanceof BlockPistonBase ||
                block instanceof BlockPistonExtension ||
                block instanceof BlockHopper) {
            return BlockType.CHEST;
        }
        if (uBlock.getID() == UBlocks.STANDING_SIGN.getID()) {
            return BlockType.SIGNPOST;
        }
        if (block instanceof BlockDoor) {
            return BlockType.DOOR;
        }
        if (block instanceof BlockButton) {
            return BlockType.BUTTON;
        }
        if (    block instanceof BlockRedstoneRepeater ||
                block instanceof BlockRedstoneComparator) {
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

    /**
     * Get metadata mask that covers rotations
     * @param blockType block rotation type
     * @return Mask that covers rotations
     */
    public static int getMask(BlockType blockType) {
        return masks.getOrDefault(blockType, 0);
    }

    /**
     * Add mappings from meta to direction map and block type and mask
     * In case the opposite direction not exists it will be added automatically
     * @param metaToDir Metadata to direction map
     * @param blockType Block rotation type
     * @param mask Metadata mask that covers rotations
     */
    private static void addMappings(HashMap<Integer, Integer> metaToDir, BlockType blockType, int mask) {
        HashMap<Integer, Integer> dirToMeta = new HashMap<>();
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
