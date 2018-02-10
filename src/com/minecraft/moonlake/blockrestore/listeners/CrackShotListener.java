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


package com.minecraft.moonlake.blockrestore.listeners;

import com.minecraft.moonlake.blockrestore.BlockRestorePlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CrackShotListener implements Listener {

    private final BlockRestorePlugin main;
    private final static String CRACK_SHOT_PROJECTILE_KEY = "projParentNode";

    public CrackShotListener(BlockRestorePlugin main) {
        this.main = main;
    }

    public BlockRestorePlugin getMain() {
        return main;
    }

    @EventHandler
    public void onHit(EntityExplodeEvent event) {
        // 处理武器子弹撞击方块事件
        Entity entity = event.getEntity();
        if(!(entity instanceof Projectile)) return; // 实体不为弹丸则返回
        if(!(entity.hasMetadata(CRACK_SHOT_PROJECTILE_KEY))) return; // 实体没有 CrackShot 的属性则返回
        // 否则将 CrackShot 的子弹破坏的方块添加到恢复队列
        getMain().getBlockRestoreManager().addBlockRestore(event.blockList());
    }
}
