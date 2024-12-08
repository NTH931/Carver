package com.leathershorts.carver;

import net.fabricmc.api.ModInitializer;

public class Carver implements ModInitializer {
	public static final String MOD_ID = "carver";

	@Override
	public void onInitialize() {
		System.out.println("Registered 150 carver recipes");
//		CarverRecipeUtil.createWood();
//		CarverRecipeUtil.createStone();
//		CarverRecipeUtil.createMisc();
	}
}