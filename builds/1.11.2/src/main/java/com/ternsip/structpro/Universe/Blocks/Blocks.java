package com.ternsip.structpro.Universe.Blocks;

import com.ternsip.structpro.Logic.Configurator;
import com.ternsip.structpro.Utils.Selector;
import com.ternsip.structpro.Utils.Report;
import com.ternsip.structpro.Utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* Blocks control class */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class Blocks extends net.minecraft.init.Blocks {

    /* Default vanilla blocks by classical indices */
    private static final Block[] blocks = new ArrayList<Block>() {{
        /* Construct default blocks */
        final Block[] vanilla = new Block[256];
        for (int blockID = 0; blockID < 256; ++blockID) {
            Block block = Block.getBlockById(blockID);
            vanilla[blockID] = block == AIR && blockID != 0 ? null : block;
        }
        /* Construct block replaces */
        final HashMap<Block, Block> replace = new HashMap<Block, Block>() {{
            Selector<Block> names = new Selector<Block>() {{
                for (Block block : vanilla) {
                    if (block != null && block.getRegistryName() != null) {
                        add(block.getRegistryName().getResourcePath(), block);
                    }
                }
                add("NULL", null);
            }};
            for (String name : Configurator.REPLACE_BLOCKS) {
                List<String> tokens = Utils.tokenize(name, "->");
                if (tokens.size() == 2) {
                    try {
                        Pattern srcPattern = Pattern.compile(tokens.get(0), Pattern.CASE_INSENSITIVE);
                        Pattern dstPattern = Pattern.compile(tokens.get(1), Pattern.CASE_INSENSITIVE);
                        for (Block blockSrc : names.select(srcPattern)) {
                            for (Block blockDst : names.select(dstPattern)) {
                                put(blockSrc, blockDst);
                            }
                        }
                    } catch (PatternSyntaxException pse) {
                        new Report().post("BAD PATTERN", pse.getMessage()).print();
                    }
                }
            }
        }};
        /* Add all blocks */
        for (Block block : vanilla) {
            add(block == null || !replace.containsKey(block) ? block : replace.get(block));
        }
    }}.toArray(new Block[256]);

    /* Check if the block have vanilla index */
    public static boolean isVanillaID(int blockID) {
        return blockID >= 0 && blockID < 256;
    }

    /* Block id from block*/
    public static int blockID(Block block) {
        return Block.getIdFromBlock(block);
    }

    /* Block from block id */
    public static Block idToBlock(int blockID) {
        return isVanillaID(blockID) ? blocks[blockID] : null;
    }

    /* Block from block state */
    public static Block getBlock(IBlockState state) {
        return state.getBlock();
    }

    /* Block id from block state */
    public static int blockID(IBlockState block) {
        return blockID(getBlock(block));
    }

    /* Block state from block */
    public static IBlockState state(Block block) {
        return block.getDefaultState();
    }

    /* Block metadata from block state */
    public static int getMeta(IBlockState state) {
        return state.getBlock().getMetaFromState(state);
    }

    /* Block state from block and metadata */
    public static IBlockState state(Block block, int meta) {
        IBlockState result = state(block);
        try {
            result = block.getStateFromMeta(meta);
        } catch (Throwable ignored) {}
        return result;
    }
}
