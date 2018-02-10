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


package com.minecraft.moonlake.blockrestore;

import com.minecraft.moonlake.blockrestore.listeners.CrackShotListener;
import com.minecraft.moonlake.blockrestore.manager.BlockRestoreManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class BlockRestorePlugin extends JavaPlugin {

    private BlockRestoreManager blockRestoreManager;

    public BlockRestorePlugin() {
    }

    @Override
    public void onEnable() {
        if(!setupCrackShot()) {
            this.getLogger().log(Level.SEVERE, "前置神枪手 CrackShot 插件加载失败.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.initFolder();
        this.blockRestoreManager = new BlockRestoreManager(this);
        this.blockRestoreManager.reload();
        this.blockRestoreManager.start();
        this.getServer().getPluginManager().registerEvents(new CrackShotListener(this), this);
        this.getLogger().info("方块恢复 BlockRestore 插件 v" + getDescription().getVersion() + " 成功加载.");
    }

    @Override
    public void onDisable() {
        // 关闭检测定时器
        getBlockRestoreManager().end();
    }

    private void initFolder() {
        if(!getDataFolder().exists())
            getDataFolder().mkdirs();
        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists())
            saveDefaultConfig();
    }

    public BlockRestoreManager getBlockRestoreManager() {
        return blockRestoreManager;
    }

    private boolean setupCrackShot() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("CrackShot");
        return plugin != null && plugin.isEnabled();
    }
}
