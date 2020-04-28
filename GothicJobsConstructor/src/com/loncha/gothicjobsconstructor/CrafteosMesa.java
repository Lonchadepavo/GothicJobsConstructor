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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;

import net.md_5.bungee.api.ChatColor;


public class CrafteosMesa implements Listener{
	Main m;
	ArrayList<String> tipoConstrucciones = new ArrayList<String>(Arrays.asList("mesa de trabajo","estante para cuero","estante para comida"));
	
	public CrafteosMesa(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Block b = e.getBlock();
		for (int i = 0; i < m.bloqueOrigen.size(); i++) {
			if (b.getType().toString().equalsIgnoreCase(m.bloqueOrigen.get(i))) {
				if (checkMesa(b) == "mesa de trabajo") {
					
					if (b.getType().toString().contains("STAIRS")) {
						b.setData((byte)(0));
					}
					
					if (m.bloqueOrigenData.get(i) == b.getData()) {				
						b.setMetadata("left", new FixedMetadataValue(m, "true"));
						b.setMetadata("tipo", new FixedMetadataValue(m,"constructor"));
						b.setMetadata("constructor", new FixedMetadataValue(m, "true"));	
						b.setMetadata("resultado", new FixedMetadataValue(m, m.bloqueResultado.get(i)));
						b.setMetadata(m.bloqueResultado.get(i), new FixedMetadataValue(m,"true"));
						b.setMetadata("resultadodata", new FixedMetadataValue(m, m.bloqueResultadoData.get(i)));
						b.setMetadata("herramientaconstructor", new FixedMetadataValue(m, m.herramientas.get(i)));
						b.setMetadata("residuo", new FixedMetadataValue(m, m.itemResiduo.get(i)));
						b.setMetadata("nivel", new FixedMetadataValue(m, m.nivelCrafteo.get(i)));	
					}
				}
			}		
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

			if (b.hasMetadata("tipo")) {
				if (b.getMetadata("tipo").get(0).asString().equals("constructor")) {
					if (b.getMetadata("herramientaconstructor").get(0).asString().equalsIgnoreCase(nombreItemInHand)) {
						if (p.hasPermission("gjobs.constructor"+b.getMetadata("nivel").get(0).asInt())) {
							b.setMetadata("tipo", new FixedMetadataValue(m,"constructor"));
							
							b.removeMetadata(b.getMetadata("resultado").get(0).asString(), m);
							b.setType(Material.getMaterial(b.getMetadata("resultado").get(0).asString()));
							b.setData((byte) (b.getMetadata("resultadodata").get(0).asInt()));

							if (!b.getMetadata("residuo").get(0).asString().equals("AIR")) {
								for (ItemStack item : m.itemsCustomConstructor) {
									String nombreItem = "";
									
									if (item.hasItemMeta()) {
										nombreItem = item.getItemMeta().getDisplayName();
									} else {
										nombreItem = item.getType().toString();
									}
									
									if (b.getMetadata("residuo").get(0).asString().equals(nombreItem)) {
										ItemStack residuo = item;
										p.getWorld().dropItem(b.getLocation(), residuo);
									}
								}
								
							}
	
							for (int i = 0; i < m.bloqueOrigen.size(); i++) {
								if (b.getType().toString().equalsIgnoreCase(m.bloqueOrigen.get(i))) {
									if (m.bloqueOrigenData.get(i) == b.getData()) {
										b.setMetadata("resultado", new FixedMetadataValue(m, m.bloqueResultado.get(i)));
										b.setMetadata(m.bloqueResultado.get(i), new FixedMetadataValue(m,"true"));
										b.setMetadata("resultadodata", new FixedMetadataValue(m, m.bloqueResultadoData.get(i)));
										b.setMetadata("herramientaconstructor", new FixedMetadataValue(m, m.herramientas.get(i)));
										b.setMetadata("residuo", new FixedMetadataValue(m, m.itemResiduo.get(i)));
										b.setMetadata("nivel", new FixedMetadataValue(m, m.nivelCrafteo.get(i)));
										
										reproducirSonido(p, Sound.ENTITY_SHULKER_BULLET_HIT, 10);
									
									}
								}
							}
						} else {
							p.sendMessage(ChatColor.RED+"No sabes lo que haces y rompes el bloque");
							
							b.removeMetadata("left", m);
							b.removeMetadata("constructor", m);
							b.removeMetadata("tipo", m);
							b.removeMetadata(b.getMetadata("resultado").get(0).asString(), m);
							b.setType(Material.AIR);
							
							reproducirSonido(p,Sound.BLOCK_STONE_BREAK, 10);
						}
					}
				}
			}
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			
			if (b.hasMetadata("tipo")) {
				if (b.getMetadata("tipo").get(0).asString().equals("constructor")) {
					ItemStack bloque = new ItemStack(Material.getMaterial(b.getType().toString()),1,b.getData());
					
					p.getWorld().dropItem(b.getLocation(), bloque);
					
					b.removeMetadata("left", m);
					b.removeMetadata("constructor", m);
					b.removeMetadata("tipo", m);
					b.removeMetadata(b.getMetadata("resultado").get(0).asString(), m);
					b.setType(Material.AIR);
				}
			}
		}
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
	
	public void enviarMensajeSimple(Player p, ChatColor color, String mensaje, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
        	if (p.getWorld() == players.getWorld()) {
				if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
					players.sendMessage(color+mensaje);
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
