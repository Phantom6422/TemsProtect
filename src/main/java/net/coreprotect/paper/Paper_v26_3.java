package net.coreprotect.paper;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.ShelfMushroom;

/**
 * Paper adapter for Minecraft 26.3.
 *
 * <p>26.3 adds shelf mushrooms, which are directional blocks that break when
 * the block they are attached to is removed. The Bukkit adapter tracks them as
 * side-attached blocks; on Paper we can resolve the attachment direction
 * directly instead of treating every neighbour as attached.</p>
 */
public class Paper_v26_3 extends Paper_v26_2 {

    @Override
    public boolean isAttached(Block block, Block scanBlock, BlockData blockData, int scanMin) {
        if (blockData instanceof ShelfMushroom) {
            Directional directional = (Directional) blockData;
            return scanBlock.getRelative(directional.getFacing().getOppositeFace()).getLocation().equals(block.getLocation());
        }

        return super.isAttached(block, scanBlock, blockData, scanMin);
    }

}
