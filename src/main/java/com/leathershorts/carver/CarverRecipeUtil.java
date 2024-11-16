package com.leathershorts.carver;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;

public class CarverRecipeUtil {
    public static final File OUTPUT_FOLDER = new File("../src/main/resources/data/carver/recipe");
    public static final Identifier[] WOODS = {
        Identifier.of("minecraft", "oak_planks"),
        Identifier.of("minecraft", "birch_planks"),
        Identifier.of("minecraft", "dark_oak_planks"),
        Identifier.of("minecraft", "acacia_planks"),
        Identifier.of("minecraft", "spruce_planks"),
        Identifier.of("minecraft", "jungle_planks"),
        Identifier.of("minecraft", "bamboo_planks"),
        Identifier.of("minecraft", "mangrove_planks"),
        Identifier.of("minecraft", "cherry_planks"),
        Identifier.of("minecraft", "crimson_planks"),
        Identifier.of("minecraft", "warped_planks")
    };
    public static final String[] WOOD_ITEMS = {
        "_stairs",
        "_fence",
        "_fence_gate",
        "_pressure_plate",
        "_sign",
        "_slab",
        "_trapdoor"
    };
    public static final String[] UNIQUE_ITEMS = {
        "crafting_table",
        "chiseled_bookshelf",
        "ladder"
    };
    public static final Map<Identifier, Identifier> ORE_TO_PRODUCT = new HashMap<>();
    static {
        // Raw ores
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "iron_ore"), Identifier.of("minecraft", "raw_iron"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_iron_ore"), Identifier.of("minecraft", "raw_iron"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "gold_ore"), Identifier.of("minecraft", "raw_gold"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_gold_ore"), Identifier.of("minecraft", "raw_gold"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "copper_ore"), Identifier.of("minecraft", "raw_copper"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_copper_ore"), Identifier.of("minecraft", "raw_copper"));

        // Standard drops
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "coal_ore"), Identifier.of("minecraft", "coal"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_coal_ore"), Identifier.of("minecraft", "coal"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "emerald_ore"), Identifier.of("minecraft", "emerald"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_emerald_ore"), Identifier.of("minecraft", "emerald"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "lapis_ore"), Identifier.of("minecraft", "lapis_lazuli"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_lapis_ore"), Identifier.of("minecraft", "lapis_lazuli"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "diamond_ore"), Identifier.of("minecraft", "diamond"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_diamond_ore"), Identifier.of("minecraft", "diamond"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "redstone_ore"), Identifier.of("minecraft", "redstone"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "deepslate_redstone_ore"), Identifier.of("minecraft", "redstone"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "nether_gold_ore"), Identifier.of("minecraft", "gold_ingot"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "ancient_debris"), Identifier.of("minecraft", "netherite_scrap"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "nether_quartz_ore"), Identifier.of("minecraft", "quartz"));
        ORE_TO_PRODUCT.put(Identifier.of("minecraft", "amethyst_block"), Identifier.of("minecraft", "amethyst_shard"));
    }

    public static void createWood() {
        if (!OUTPUT_FOLDER.exists()) {
            throw new RuntimeException("Could not find folder \"recipe\" at " + OUTPUT_FOLDER.getAbsolutePath());
        }

        // Generate a recipe for each wood and item combination
        for (Identifier wood : WOODS) {
            String baseName = wood.getPath().replaceAll("_planks$", "");

            for (String itemSuffix : WOOD_ITEMS) {
                int count;
                switch(itemSuffix) {
                    //  case "_stairs", "_boat", "_fence", "_fence_gate" -> count = 1;
                    case "_sign", "_slab" -> count = 2;
                    case "_trapdoor" -> count = 3;
                    case "_pressure_plate" -> count = 4;
                    default -> count = 1;
                }
                Identifier output = Identifier.of(wood.getNamespace(), baseName + itemSuffix);
                String recipeJson = createStonecutterRecipeJson(wood, output, count);
                saveRecipeToFile(recipeJson, output, null);
            }
        }

        for (Identifier woodType : WOODS) {
            for (String uniqueItem : UNIQUE_ITEMS) {
                // Create the input and output identifiers for the recipe
                Identifier input = Identifier.of("minecraft", woodType.getPath());
                Identifier output = Identifier.of("minecraft", uniqueItem);

                System.out.println("Input: " + input);
                System.out.println("Output: " + output);

                int count;
                if (uniqueItem.equals("ladder")) {
                    count = 4;
                } else /* uniqueItem.equals("crafting_table") || uniqueItem.equals("chiseled_bookshelf") */ {
                    count = 1;
                }

                // Generate the Stonecutter recipe
                String recipeJson = createStonecutterRecipeJson(input, output, count);

                // Save the recipe to a file
                saveRecipeToFile(recipeJson, output, woodType.getPath());
            }
        }
    }

    public static void createStone() {
        // Iterate through the ore to product map and generate the recipes
        for (Map.Entry<Identifier, Identifier> entry : ORE_TO_PRODUCT.entrySet()) {
            Identifier ore = entry.getKey();
            Identifier product = entry.getValue();

            String recipeJson;
            switch (ore.toString()) {
                case "minecraft:ancient_debris" -> recipeJson = createStonecutterRecipeJson(ore, product, 1);
                case "minecraft:amethyst_block" -> recipeJson = createStonecutterRecipeJson(ore, product, 4);
                default -> recipeJson = createStonecutterRecipeJson(ore, product, 2);
            }

            saveRecipeToFile(recipeJson, product, ore.toString().contains("deepslate") ? "deepslate" : null);
        }
    }

    private static String createStonecutterRecipeJson(Identifier input, Identifier output, int count) {
        return """
        {
            "type": "minecraft:stonecutting",
            "ingredient": "%s",
            "result": {
                "id": "%s",
                "count": %d
            }
        }
        """.formatted(input.toString(), output.toString(), count);
    }

    private static void saveRecipeToFile(String recipeJson, Identifier output, @Nullable String uniqueId) {
        // File path based on recipe output item name
        File outputFile = new File(OUTPUT_FOLDER, output.getPath() + (uniqueId != null ? "_" + uniqueId : "") + ".json");

        try (FileWriter writer = new FileWriter(outputFile)) {
            // Write the manually created JSON string to the file
            writer.write(recipeJson);

            if (!outputFile.exists()) {
                throw new FileSystemException("File " + outputFile.getName() + " does not exist.");
            }
        } catch (IOException e) {
            System.err.println("Failed to write recipe for " + output + ": " + e.getMessage());
        }
    }
}
