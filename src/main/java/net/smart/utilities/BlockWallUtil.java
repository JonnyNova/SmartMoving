package net.smart.utilities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockWallUtil {
    /**
     * This has become private in {@link BlockWall}, duplicate the logic here.
     */
    public static boolean canConnectTo(Block wall, IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Material otherMaterial = iblockstate.getMaterial();
        Block other = iblockstate.getBlock();
        return (other != Blocks.BARRIER && other != wall && !(other instanceof BlockFenceGate))
                && (otherMaterial.isOpaque() && iblockstate.isFullCube()) &&  otherMaterial != Material.GOURD;
    }
}
