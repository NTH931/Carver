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
    /**
     * Items created
     * - Crafting Table
     * - Chiseled bookshelf
     * - Ladder
     * - Barrel
     * - Composter
     * - Stick
     * <br>
     * All mosaic variants of bamboo planks
     * All wood variants of
     * - Stairs
     * - Fence
     * - Fence Gate
     * - Pressure Plate
     * - Slab
     * - Sign
     * - Trapdoor
     * <br>
     * All ores to their raw variants
     * Nether gold ore to Gold Ingot
     * Ancient Debris to Netherite Scrap
     * Amethyst Block to Amethyst Shard
     * Glowstone to Glowstone Dust
     * Block of Quartz to Quartz
     */

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
        "ladder",
        "composter",
        "barrel",
        "stick"
    };
    public static final Map<Identifier, Identifier> ORE_TO_PRODUCT = new HashMap<>();
    private static final Map<Identifier, Map<Identifier, Integer>> MISC_TABLE = new HashMap<>();

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

        MISC_TABLE.put(Identifier.of("minecraft", "glowstone"), Map.of(Identifier.of("minecraft", "glowstone_dust"), 4));
        MISC_TABLE.put(Identifier.of("minecraft", "quartz_block"), Map.of(Identifier.of("minecraft", "quartz"), 4));
        MISC_TABLE.put(Identifier.of("minecraft", "wool"), Map.of(Identifier.of("minecraft", "string"), 4));
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
            Identifier input = Identifier.of("minecraft", woodType.getPath());

            if (input.equals(Identifier.of("minecraft", "bamboo_planks"))) {
                saveRecipeToFile(
                    createStonecutterRecipeJson(
                        input,
                        Identifier.of("minecraft", "bamboo_mosaic"),
                        1
                    ),
                    Identifier.of("minecraft", "bamboo_mosaic"),
                    woodType.getPath()
                );

                saveRecipeToFile(
                    createStonecutterRecipeJson(
                        Identifier.of("minecraft", "bamboo_mosaic"),
                        Identifier.of("minecraft", "bamboo_mosaic_slab"),
                        2
                    ),
                    Identifier.of("minecraft", "bamboo_mosaic_slab"),
                    woodType.getPath()
                );

                saveRecipeToFile(
                    createStonecutterRecipeJson(
                        Identifier.of("minecraft", "bamboo_mosaic"),
                        Identifier.of("minecraft", "bamboo_mosaic_stairs"),
                        1
                    ),
                    Identifier.of("minecraft", "bamboo_mosaic_stairs"),
                    woodType.getPath()
                );
            }

            for (String uniqueItem : UNIQUE_ITEMS) {
                // Create the input and output identifiers for the recipe
                Identifier output = Identifier.of("minecraft", uniqueItem);

                int count;
                if (uniqueItem.equals("ladder")) {
                    count = 4;
                } else if (uniqueItem.equals("stick")) {
                    count = 16;
                } else /* uniqueItem.equals("crafting_table") || uniqueItem.equals("chiseled_bookshelf") */ {
                    count = 1;
                }

                // Generate the Stonecutter recipe
                String recipeJson = createStonecutterRecipeJson(input, output, count);

                // Save the recipe to a file
                saveRecipeToFile(recipeJson, output, woodType.getPath());
            }
        }

        System.out.println("[Carver] Registered Woods");
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

        System.out.println("[Carver] Registered Stones");
    }

    public static void createMisc() {
        for (Map.Entry<Identifier, Map<Identifier, Integer>> entry : MISC_TABLE.entrySet()) {
            Identifier key = entry.getKey();
            Identifier value = null;
            int productAmount = 1;

            Map<Identifier, Integer> nestedEntry = entry.getValue();

            for (Map.Entry<Identifier, Integer> innerEntry : nestedEntry.entrySet()) {
                value = innerEntry.getKey();
                productAmount = innerEntry.getValue();
            }

            if (value != null) {
                saveRecipeToFile(
                    createStonecutterRecipeJson(key, value, productAmount),
                    value,
                    "block"
                );
            } else {
                System.err.println("Could not create recipe for " + key + '.');
            }
        }

        System.out.println("[Carver] Registered Misc");
    }

    private static String createStonecutterRecipeJson(Identifier input, Identifier output, int count) {
        return """
        {
            "type": "minecraft:stonecutting",
            "ingredient": { "item": "%s" },
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
