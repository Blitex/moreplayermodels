package net.minecraft.client.renderer.entity.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

/**
 * Created by Alex Nava on 7/10/2017.
 */
public class ModelsTab extends CreativeTabs{

    public ModelsTab(int index, String label) {
        super(index, label);
    }

    @Override
    public Item getTabIconItem() {
        return Items.EMERALD;
    }
}
