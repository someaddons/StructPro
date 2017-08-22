package com.ternsip.structpro.universe.world;

import com.ternsip.structpro.Structpro;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

@SuppressWarnings({"NullableProblems"})
public class WorldData extends WorldSavedData {

    public static final String DATA_NAME = Structpro.MODID;
    private NBTTagCompound data = new NBTTagCompound();

    public WorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        data = tag.getCompoundTag("Data");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setTag("Data", data);
    }

    public NBTTagCompound getData() {
        return data;
    }

}
