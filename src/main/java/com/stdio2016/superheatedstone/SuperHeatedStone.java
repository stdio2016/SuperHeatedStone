package com.stdio2016.superheatedstone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = SuperHeatedStone.MODID, version = SuperHeatedStone.VERSION)
public class SuperHeatedStone
{
    public static final String MODID = "superheatedstone";
    public static final String VERSION = "1.0";
    public static final Item HOT_STONE, HEATED_STONE, LIQUID_STONE, SUPER_HEATED_STONE;
    public static final Item[] items;

    private static Item createItem(String unlocalizedName) {
        return new Item().setUnlocalizedName(unlocalizedName).setRegistryName(unlocalizedName);
    }

    static {
        HOT_STONE = createItem("hot_stone").setCreativeTab(CreativeTabs.MATERIALS);
        HEATED_STONE = createItem("heated_stone").setCreativeTab(CreativeTabs.MATERIALS);
        SUPER_HEATED_STONE = createItem("super_heated_stone").setCreativeTab(CreativeTabs.MATERIALS);
        LIQUID_STONE = createItem("liquid_stone").setCreativeTab(CreativeTabs.MATERIALS);
        items = new Item[] {HOT_STONE, HEATED_STONE, SUPER_HEATED_STONE, LIQUID_STONE};
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        for (Item item : items) {
            GameRegistry.register(item);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.addSmelting(new ItemStack(Blocks.STONE, 1, 0), new ItemStack(HOT_STONE), 0.1f);
        GameRegistry.addSmelting(HOT_STONE, new ItemStack(HEATED_STONE), 0.1f);
        GameRegistry.addSmelting(HEATED_STONE, new ItemStack(SUPER_HEATED_STONE), 0.1f);
        GameRegistry.addSmelting(SUPER_HEATED_STONE, new ItemStack(LIQUID_STONE), 0.1f);
        GameRegistry.addSmelting(LIQUID_STONE, new ItemStack(Blocks.MAGMA), 0.1f);
        GameRegistry.registerFuelHandler(new IFuelHandler() {
            @Override
            public int getBurnTime(ItemStack itemStack) {
                final int ONE_BURN = 200;
                if (itemStack.getItem() == HOT_STONE) return 2 * ONE_BURN;
                if (itemStack.getItem() == HEATED_STONE) return 4 * ONE_BURN;
                if (itemStack.getItem() == SUPER_HEATED_STONE) return 8 * ONE_BURN;
                if (itemStack.getItem() == LIQUID_STONE) return 16 * ONE_BURN;
                return 0;
            }
        });
    }

    private void addImageForItem(Item item) {
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        modelMesher.register(item, 0, new ModelResourceLocation(this.MODID+":"+item.getUnlocalizedName().substring(5), "inventory"));
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            for (Item item : items) {
                addImageForItem(item);
            }
        }
    }
}
