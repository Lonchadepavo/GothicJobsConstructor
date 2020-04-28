package com.loncha.gothicjobsconstructor;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import net.md_5.bungee.api.ChatColor;

public class Construccion implements Listener{
	Main m;
	
	ArrayList<String> tipoConstrucciones = new ArrayList<String>(Arrays.asList("mesa de trabajo","estante para cuero","estante para comida"));
	
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
					System.out.println(bData);
					System.out.println(m.bloqueConstruirData.get(i));
					if (bData == m.bloqueConstruirData.get(i)) {
						if (!p.hasPermission("gjobs.constructor"+m.nivelBloqueConstruir.get(i))) {
							if (!checkMesa(b).equals("mesa de trabajo")) {
								e.setCancelled(true);
								p.sendMessage(ChatColor.DARK_RED+"No sabes como construir ese bloque.");
							}
						}
					}
				} else {
					if (!p.hasPermission("gjobs.constructor"+m.nivelBloqueConstruir.get(i))) {
						if (!checkMesa(b).equals("mesa de trabajo")) {
							e.setCancelled(true);
							p.sendMessage(ChatColor.DARK_RED+"No sabes como construir ese bloque.");
						}
					}
				}
			}
		}
		
	}
	
	public String checkMesa(Block b) {	
		Location lMesa = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ());
		Location lEstante = new Location(b.getWorld(), b.getLocation().getX(), b.getLocation().getY()+1, b.getLocation().getZ());
		
		//COMPRUEBA SI ES UNA MESA DE TRABAJO DE PIEDRA
		if (lMesa.getBlock().getType() == Material.COBBLESTONE_STAIRS) {
			Block mesaCentro = lMesa.getBlock();
			Location lMesa1 = new Location(mesaCentro.getWorld(), mesaCentro.getX()-1, mesaCentro.getY(), mesaCentro.getZ());
			Location lMesa2 = new Location(mesaCentro.getWorld(), mesaCentro.getX()+1, mesaCentro.getY(), mesaCentro.getZ());
			Location lMesa3 = new Location(mesaCentro.getWorld(), mesaCentro.getX(), mesaCentro.getY(), mesaCentro.getZ()-1);
			Location lMesa4 = new Location(mesaCentro.getWorld(), mesaCentro.getX(), mesaCentro.getY(), mesaCentro.getZ()+1);
			
			Location[] locations = {lMesa1, lMesa2, lMesa3, lMesa4};
			
			for (Location l : locations) {
				if (l.getBlock().getType() == Material.COBBLESTONE_STAIRS) {
					return tipoConstrucciones.get(0);
				}
			}
		}
		
		//COMPRUEBA SI ES UN ESTANTE DE CUERO
		if (b.getType().toString().contains("FENCE")) {
			Location posicion1 = new Location (b.getWorld(), b.getLocation().getX()-1, b.getLocation().getY()-1, b.getLocation().getZ());
			Location posicion2 = new Location (b.getWorld(), b.getLocation().getX()+1, b.getLocation().getY()-1, b.getLocation().getZ());
			Location posicion3 = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ()-1);
			Location posicion4 = new Location (b.getWorld(), b.getLocation().getX(), b.getLocation().getY()-1, b.getLocation().getZ()+1);
			
			Block estanteCentro = lEstante.getBlock();
			Location lEstante1 = new Location(estanteCentro.getWorld(), estanteCentro.getX()-1, estanteCentro.getY(), estanteCentro.getZ());
			Location lEstante2 = new Location(estanteCentro.getWorld(), estanteCentro.getX()+1, estanteCentro.getY(), estanteCentro.getZ());
			Location lEstante3 = new Location(estanteCentro.getWorld(), estanteCentro.getX(), estanteCentro.getY(), estanteCentro.getZ()-1);
			Location lEstante4 = new Location(estanteCentro.getWorld(), estanteCentro.getX(), estanteCentro.getY(), estanteCentro.getZ()+1);
			
			Location[] locations = {lEstante1, lEstante2, lEstante3, lEstante4};
			
			for (Location l : locations) {
				if (l.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(2);
				}
			}
			
			if (posicion1.getBlock().getType().toString().contains("FENCE")) {
				if (posicion2.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(1);
				}
			} else if (posicion3.getBlock().getType().toString().contains("FENCE")) {
				if (posicion4.getBlock().getType().toString().contains("FENCE")) {
					return tipoConstrucciones.get(1);
				}
			}
		}
		
		return "nada";
	}
	
}
