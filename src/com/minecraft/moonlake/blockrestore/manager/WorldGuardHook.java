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


package com.minecraft.moonlake.blockrestore.manager;

import com.minecraft.moonlake.blockrestore.BlockRestorePlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class WorldGuardHook {

    private final BlockRestorePlugin main;
    private final WorldGuardPlugin worldGuard;

    public WorldGuardHook(BlockRestorePlugin main) {
        this.main = main;
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if(plugin == null || !plugin.isEnabled()) {
            // 插件不存在或未开启
            getMain().getLogger().log(Level.SEVERE, "错误: 检测到插件 WorldGuard 未存在或未开启, 功能无法 Hook, 请重试.");
            throw new RuntimeException();
        }
        this.worldGuard = ((WorldGuardPlugin) plugin);
    }

    public BlockRestorePlugin getMain() {
        return main;
    }

    public boolean checkRegion(Block block) {
        // 检测方块区域
        RegionManager regionManager = worldGuard.getRegionManager(block.getWorld());
        return regionManager.getApplicableRegions(block.getLocation()).getRegions().parallelStream()
                .map(ProtectedRegion::getType).filter(type -> type != RegionType.GLOBAL).count() > 0;
    }
}
