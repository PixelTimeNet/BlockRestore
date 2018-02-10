/*
 * Copyright (C) 2017 The MoonLake Authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.minecraft.moonlake.blockrestore.data;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class BlockRestore {

    private final World world;
    private final int x, y, z;
    private final Material material;
    private final int data;
    private final long timestamp;
    private final int value;

    public BlockRestore(World world, int x, int y, int z, Material material, int data, int value) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
        this.data = data;
        this.timestamp = System.currentTimeMillis(); // timestamp
        this.value = value;
    }

    public BlockRestore(Block block, int value) {
        this(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getType(), block.getData(), value);
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Material getMaterial() {
        return material;
    }

    public int getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getValue() {
        return value;
    }

    public boolean validate() {
        // 验证方块的恢复时间
        return System.currentTimeMillis() - timestamp >= value;
    }

    public void restore() {
        // 恢复方块
        Block block = world.getBlockAt(x, y, z);
        block.setType(material);
        block.setData((byte) data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockRestore that = (BlockRestore) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        if (data != that.data) return false;
        if (timestamp != that.timestamp) return false;
        if (value != that.value) return false;
        if (!world.equals(that.world)) return false;
        return material == that.material;
    }

    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + material.hashCode();
        result = 31 * result + data;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return "BlockRestore{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", material=" + material +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
