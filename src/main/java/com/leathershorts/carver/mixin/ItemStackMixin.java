package com.leathershorts.carver.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void injectGetName(CallbackInfoReturnable<Text> info) {
        if (getItem() == Items.STONECUTTER) {
            info.setReturnValue(Text.literal("Carver"));
        }
    }

}