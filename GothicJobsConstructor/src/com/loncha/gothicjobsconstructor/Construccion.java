package com.loncha.gothicjobsconstructor;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Construccion implements Listener{
	Main m;
	
	public Construccion(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Block b = e.getBlock();
		int bData = b.getData();
		
		Player p = e.getPlayer();
		
		for (int i = 0; i < m.bloqueConstruir.size(); i++) {
			if (b.getTypeId() == m.bloqueConstruir.get(i)) {
				if (!b.getType().toString().contains("STAIRS")) {
					if (bData == m.bloqueConstruirData.get(i)) {
						if (!p.hasPermission("gjobs.constructor"+m.nivelBloqueConstruir.get(i))) {
							e.setCancelled(true);
						}
					}
				} else {
					if (!p.hasPermission("gjobs.constructor"+m.nivelBloqueConstruir.get(i))) {
						e.setCancelled(true);
					}
				}
			}
		}
		
	}
	
}
