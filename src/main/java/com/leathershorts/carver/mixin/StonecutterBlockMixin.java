package com.leathershorts.carver.mixin;

import net.minecraft.block.StonecutterBlock;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.BlockState;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(StonecutterBlock.class)
public class StonecutterBlockMixin {
    /**
     * @author LeaTHeR_SHoRTs
     * @reason Changing the name of the stonecutter's UI element
     */
    @Overwrite
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        // Return a custom title for the stonecutter GUI
        Text CUSTOM_TITLE = Text.translatable("container.carver");  // Use your custom translation key
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> new StonecutterScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos)), CUSTOM_TITLE);
    }
}
