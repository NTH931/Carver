package com.leathershorts.carver;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;

public class Carver implements ModInitializer {
	public static final String MOD_ID = "carver";

	@Override
	public void onInitialize() {
		System.out.println("Registered 77 stonecutter recipes");
		//CarverRecipeUtil.create();
	}
}