package com.github.Crupette.biomechanics.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import javax.crypto.Mac;

public class MaceratorRecipe implements Recipe<Inventory> {
    protected final Identifier id;
    protected final Ingredient input;

    protected final int time;
    protected final ItemStack result;

    public MaceratorRecipe(Identifier id, Ingredient input, int time, ItemStack result) {
        this.id = id;
        this.input = input;
        this.time = time;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return this.input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inv) {
        ItemStack out = this.result.copy();
        CompoundTag compoundTag = inv.getStack(0).getTag();
        if (compoundTag != null) {
            out.setTag(compoundTag.copy());
        }
        return out;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getOutput() {
        return this.result;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BiomechanicsRecipes.MACERATOR_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BiomechanicsRecipes.MACERATOR;
    }

    public int getTime() {
        return this.time;
    }

    public static class Serializer implements RecipeSerializer<MaceratorRecipe>{
        public Serializer() {
        }

        @Override
        public MaceratorRecipe read(Identifier id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(JsonHelper.getObject(json, "input"));
            int time = JsonHelper.getInt(json, "time");
            ItemStack result = ShapedRecipe.getItemStack(JsonHelper.getObject(json, "result"));
            return new MaceratorRecipe(id, input, time, result);
        }

        public MaceratorRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            int time = packetByteBuf.readInt();
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new MaceratorRecipe(identifier, ingredient, time, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, MaceratorRecipe maceratorRecipe) {
            maceratorRecipe.input.write(packetByteBuf);
            packetByteBuf.writeInt(maceratorRecipe.time);
            packetByteBuf.writeItemStack(maceratorRecipe.result);
        }
    }
}
