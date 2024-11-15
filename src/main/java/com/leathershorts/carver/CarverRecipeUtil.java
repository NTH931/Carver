package com.leathershorts.carver;

import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;

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
    public static final String[] ITEMS = {
        "_stairs",
        "_fence",
        "_fence_gate",
        "_pressure_plate",
        "_sign",
        "_slab",
        "_trapdoor"
    };

    public static void create() {
        if (!OUTPUT_FOLDER.exists()) {
            throw new RuntimeException("Could not find folder \"recipe\" at " + OUTPUT_FOLDER.getAbsolutePath());
        }

        // Generate a recipe for each wood and item combination
        for (Identifier wood : WOODS) {
            String baseName = wood.getPath().replaceAll("_planks$", "");

            for (String itemSuffix : ITEMS) {
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
                saveRecipeToFile(recipeJson, output);
            }
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

    private static void saveRecipeToFile(String recipeJson, Identifier output) {
        // File path based on recipe output item name
        File outputFile = new File(OUTPUT_FOLDER, output.getPath() + ".json");

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
