package com.loncha.gothicjobsconstructor;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;

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
					if (bData == m.bloqueConstruirData.get(i)) {
						if (!p.hasPermission("gjobs.constructor"+m.nivelBloqueConstruir.get(i))) {
							if (!checkMesa(b).equals("mesa de trabajo")) {
								e.setCancelled(true);
								p.sendMessage(ChatColor.DARK_RED+"No sabes como construir ese bloque.");
							}
						} else {
							//AQUÍ VA EL CÓDIGO DE CONSTRUCCIÓN
							if (!m.materialParaBloque.get(i).equals("AIR")) {
								if (!checkMesa(b).equals("mesa de trabajo")) { 
									b.setMetadata("material", new FixedMetadataValue(m, m.materialParaBloque.get(i)));
									b.setMetadata("construido", new FixedMetadataValue(m, false));
									
									BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
						            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
						                @Override
						                public void run() {
						                	if (b.hasMetadata("construido")) {
						                		if (!b.getMetadata("construido").get(0).asBoolean()) {
						                			int number = (int)(Math.random() * 100) + 1;
						                			
						                			if (number <= 30) {
						                				b.setType(Material.AIR);
						                				reproducirSonido(p, Sound.BLOCK_STONE_BREAK, 10);
						                				p.sendMessage(ChatColor.DARK_RED + "No has usado nada para sujetar el bloque y se ha roto.");
						                			} else {
						                				b.getWorld().dropItem(b.getLocation(), new ItemStack(b.getType(), 1, b.getData()));
						                				b.setType(Material.AIR);
						                				reproducirSonido(p, Sound.BLOCK_STONE_BREAK, 10);
						                				p.sendMessage(ChatColor.DARK_RED + "No has usado nada para sujetar el bloque y se ha roto.");
						                			}
						                		}
						                	}
						                }
	
						            }, 100);
								}
							}
						}
					}
				} else {
					if (!p.hasPermission("gjobs.constructor"+m.nivelBloqueConstruir.get(i))) {
						if (!checkMesa(b).equals("mesa de trabajo")) {
							e.setCancelled(true);
							p.sendMessage(ChatColor.DARK_RED+"No sabes como construir ese bloque.");
						}
					} else {
						//AQUÍ VA EL CÓDIGO DE CONSTRUCCIÓN
						if (!m.materialParaBloque.get(i).equals("AIR")) {
							if (!checkMesa(b).equals("mesa de trabajo")) {
								b.setMetadata("material", new FixedMetadataValue(m, m.materialParaBloque.get(i)));
								b.setMetadata("construido", new FixedMetadataValue(m, false));
								
								BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
					                @Override
					                public void run() {
					                	if (b.hasMetadata("construido")) {
					                		if (!b.getMetadata("construido").get(0).asBoolean()) {
					                			int number = (int)(Math.random() * 100) + 1;
					                			
					                			if (number <= 30) {
					                				b.setType(Material.AIR);
					                				reproducirSonido(p, Sound.BLOCK_STONE_BREAK, 10);
					                				p.sendMessage(ChatColor.DARK_RED + "No has usado nada para sujetar el bloque y se ha roto.");
					                			} else {
					                				b.getWorld().dropItem(b.getLocation(), new ItemStack(b.getType(), 1, b.getData()));
					                				b.setType(Material.AIR);
					                				reproducirSonido(p, Sound.BLOCK_STONE_BREAK, 10);
					                				p.sendMessage(ChatColor.DARK_RED + "No has usado nada para sujetar el bloque y se ha roto.");
					                			}
					                		}
					                	}
					                }
	
					            }, 100);
							}
						}
					}
				}
			}
		}
		
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		int bData = b.getData();
		
		Player p = e.getPlayer();
		
		if (b.hasMetadata("material")) {
			b.removeMetadata("material", m);
			b.removeMetadata("construido", m);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		ItemStack itemInHand = p.getInventory().getItemInMainHand();
		String nombreItemInHand = "";
		
		if (itemInHand.hasItemMeta()) {
			nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
		} else {
			nombreItemInHand = itemInHand.getType().toString();
		}
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			if (b.hasMetadata("material")) {
				if (b.getMetadata("material").get(0).asString().equals(nombreItemInHand)) {
					restarObjeto(itemInHand, p);
					b.removeMetadata("material", m);
					int bData = b.getData();
					for (int i = 0; i < m.bloqueConstruir.size(); i++) {
						if (b.getTypeId() == m.bloqueConstruir.get(i)) {
							if (!b.getType().toString().contains("STAIRS")) {
								if (bData == m.bloqueConstruirData.get(i)) {
									if (m.herramientaParaBloque.get(i).contentEquals("AIR")) {
										b.removeMetadata("construido", m);
									}
								}
							} else {
								if (m.herramientaParaBloque.get(i).contentEquals("AIR")) {
									b.removeMetadata("construido", m);
								}
							}
						}
					}
				}
			} else {
				if (b.hasMetadata("construido")) {
					if (!b.getMetadata("construido").get(0).asBoolean()) {
						int bData = b.getData();
						for (int i = 0; i < m.bloqueConstruir.size(); i++) {
							if (b.getTypeId() == m.bloqueConstruir.get(i)) {
								if (!b.getType().toString().contains("STAIRS")) {
									if (bData == m.bloqueConstruirData.get(i)) {
										if (nombreItemInHand.equals(m.herramientaParaBloque.get(i))) {
											b.removeMetadata("construido", m);
										}
									}
								} else {
									if (nombreItemInHand.equals(m.herramientaParaBloque.get(i))) {
										b.removeMetadata("construido", m);
									}
								}
							}
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
	
	public void reproducirSonido(Player p, Sound sonido, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
        	if (p.getWorld() == players.getWorld()) {
				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
					
					players.getWorld().playSound(p.getLocation(), sonido, 1.0F, 0.01F);
				}
        	}
        }
	}
	
	public void restarObjeto(ItemStack item, Player p) {
		if (item.getAmount()-1 == 0) {
			p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		} else {
			item.setAmount(item.getAmount()-1);
			p.getInventory().setItemInMainHand(item);
		}
	}
}
