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
import com.minecraft.moonlake.blockrestore.data.BlockRestore;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BlockRestoreManager extends BukkitRunnable {

    private final BlockRestorePlugin main;
    private final List<BlockRestore> restoreList;
    private WorldGuardHook worldGuardHook;

    public BlockRestoreManager(BlockRestorePlugin main) {
        this.main = main;
        this.restoreList = new ArrayList<>();
    }

    public BlockRestorePlugin getMain() {
        return main;
    }

    public void reload() {
        // 重新加载配置文件数据
        if(getMain().getConfig().getBoolean("WorldGuardHook", true)) {
            // 开启 WorldGuard 的功能
            try {
                this.worldGuardHook = new WorldGuardHook(getMain());
                this.getMain().getLogger().info("已成功挂钩到 WorldGuard 插件, 区域检测功能已开启.");
            } catch (Exception e) {
                getMain().getLogger().log(Level.SEVERE, "错误: 挂钩到 WorldGuard 插件时出错:", e);
            }
        }
    }

    public void start() {
        // 开始并运行队列检测定时器
        runTaskTimer(getMain(),3 * 20L, getMain().getConfig().getLong("CheckPeriod", 20L));
        getMain().getLogger().info("方块恢复队列检测定时器已成功运行...");
        // 先清空一次恢复队列
        restoreList.clear();
    }

    public void end() {
        // 结束并关闭检测定时器
        cancel();
        // 检测是否需要恢复队列方块
        if(getMain().getConfig().getBoolean("DisableRestore", true)) {
            // 开启关闭则恢复
            if(!restoreList.isEmpty()) {
                // 不为空则开始进行恢复
                getMain().getLogger().info("检测到恢复队列还存在恢复方块...");
                getMain().getLogger().info("正在将方块恢复队列的所有方块恢复中, 请等待...");
                // 用流来调用方块恢复函数进行恢复
                restoreList.stream().parallel().forEach(BlockRestore::restore);
                // 控制台提示
                getMain().getLogger().info("已成功将恢复队列的方块进行恢复完成...");
                // 清空恢复队列
                restoreList.clear();
            }
        }
    }

    public void addBlockRestore(Block block) {
        // 将指定方块添加到恢复队列

        // 验证方块的 WorldGuard 区域
        if(worldGuardHook != null && worldGuardHook.checkRegion(block)) {
            // 开启功能并且不符合则阻止破坏方块并返回
            // 由于 CrackShot 强制将方块设置成空气, 所以可以添加到恢复队列
            // 之后呢过个100毫秒吧进行恢复
            restoreList.add(new BlockRestore(block, 5)); // 默认 5 毫秒
            return;
        }
        // 验证该方块是否可以进行恢复
        Integer value = validateIsSet(block);
        if(value != null) // 不为 null 则说明设置则添加到队列
            restoreList.add(new BlockRestore(block, value));
    }

    public void addBlockRestore(List<Block> blockList) {
        // 将指定方块列表添加到恢复队列
        blockList.parallelStream().forEach(this::addBlockRestore);
    }

    @SuppressWarnings("deprecation")
    private Integer validateIsSet(Block block) {
        // 验证指定方块是否可以进行恢复
        String type = block.getType().name();
        int id = block.getType().getId();
        /*int data = block.getData();

        getMain().getLogger().info("data: " + id + "-" + data);

        if(data == 0 && getMain().getConfig().isSet("List." + type))
            // Type
            return getMain().getConfig().getInt("List." + type + ".Value");
        else if(data == 0 && getMain().getConfig().isSet("List." + id))
            // Id
            return getMain().getConfig().getInt("List." + id + ".Value");
        else if(data != 0 && getMain().getConfig().isSet("List." + type + "-" + data))
            // Type-Data
            return getMain().getConfig().getInt("List." + type + "-" + data + ".Value");
        else if(data != 0 && getMain().getConfig().isSet("List." + id + "-" + data))
            // Id-Data
            return getMain().getConfig().getInt("List." + id + "-" + data + ".Value");
        else
            // None
            return null;*/
        if(getMain().getConfig().isSet("List."+ type))
            return getMain().getConfig().getInt("List." + type);
        else if(getMain().getConfig().isSet("List." + id))
            return getMain().getConfig().getInt("List." + id);
        else
            return null;
    }

    @Override
    public void run() {
        // 方块恢复定时器更新函数
        if(restoreList.isEmpty()) return; // 如果队列为空则返回

        List<BlockRestore> confirm = restoreList.parallelStream().filter(BlockRestore::validate).collect(Collectors.toList());
        confirm.forEach(BlockRestore::restore); // 将符合的方块恢复数据给恢复
        restoreList.removeAll(confirm); // 之后从方块恢复队列删除
    }
}
