package com.loncha.gothicjobsconstructor;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;
import org.fusesource.jansi.Ansi.Color;

import net.md_5.bungee.api.ChatColor;

public class CrafteoCemento implements Listener{
	Main m;
	
	public CrafteoCemento(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			Block b = e.getClickedBlock();
			
			ItemStack itemInHand = p.getInventory().getItemInMainHand();
			String nombreItemInHand = "";
			
			if (itemInHand.hasItemMeta()) {
				nombreItemInHand = itemInHand.getItemMeta().getDisplayName();
			} else {
				nombreItemInHand = itemInHand.getType().toString();
			}
			
			if (b.getType() == Material.CAULDRON) {
				Cauldron c = (Cauldron) b.getState().getData();
				
				//Si el caldero tiene agua
				if (!c.isEmpty()) {
					
					//Si el caldero no tiene estado se le asigna el estado ingredientes
					if (m.listaIngredientes.contains(nombreItemInHand)) {
						if (!b.hasMetadata("estado")) {
							b.setMetadata("estado", new FixedMetadataValue(m,"ingredientes"));
							b.setMetadata("left", new FixedMetadataValue(m, "true"));
							//Comprobar si tiene fuego debajo
							
							Location ldown = new Location(b.getWorld(),b.getX(),b.getY()-1,b.getZ());
							if (ldown.getBlock().getType() == Material.FIRE) {
								b.setMetadata("calor", new FixedMetadataValue(m, "true"));
							}
						}
					}
					
					if (b.hasMetadata("estado")) {
						e.setCancelled(true);
					

					//Comprueba en que estado está el caldero
						switch(b.getMetadata("estado").get(0).asString()) {	
							case "ingredientes":
								//Comprueba si no estás intentado usar un palo para mezclar
								if (!nombreItemInHand.equalsIgnoreCase("Palo para mezclar")) {
									if (m.listaIngredientes.contains(nombreItemInHand)) {
										if (b.hasMetadata(nombreItemInHand)) {
											int data = b.getMetadata(nombreItemInHand).get(0).asInt();	
											b.setMetadata(nombreItemInHand, new FixedMetadataValue(m, data+1));
											b.setMetadata(nombreItemInHand+"data", new FixedMetadataValue(m,itemInHand.getData().getData()));
											
											if (itemInHand.getAmount()-1 == 0) {
												p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
											} else {
												itemInHand.setAmount(itemInHand.getAmount()-1);
												p.getInventory().setItemInMainHand(itemInHand);
											}
											
											p.sendMessage(ChatColor.GREEN+"Introduces un ingrediente en el caldero");
											
								            reproducirSonido(p,Sound.ITEM_BUCKET_FILL,5);
											
										} else {
											b.setMetadata(nombreItemInHand, new FixedMetadataValue(m, 1));
											b.setMetadata(nombreItemInHand+"data", new FixedMetadataValue(m,itemInHand.getData().getData()));
											
											if (itemInHand.getAmount()-1 == 0) {
												p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
											} else {
												itemInHand.setAmount(itemInHand.getAmount()-1);
												p.getInventory().setItemInMainHand(itemInHand);
											}
											
											p.sendMessage(ChatColor.GREEN+"Introduces un ingrediente en el caldero");
											
								            reproducirSonido(p,Sound.ITEM_BUCKET_FILL,5);
										}
									}
								//Si usas un palo para mezclar cambia el estado del caldero (ya no puedes echar más ingredientes)
								} else {
									b.setMetadata("estado", new FixedMetadataValue(m,"mezclar"));
						            reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY,5);
						            p.sendMessage(ChatColor.YELLOW+"Remueves la mezcla");
								}
								
								break;
							
							case "mezclar":
								b.setMetadata("estado", new FixedMetadataValue(m,"espera"));
					            reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY,5);
					            p.sendMessage(ChatColor.YELLOW+"Remueves la mezcla");
								break;
								
							case "espera":
								ArrayList<String> ingredientes = new ArrayList<String>();
								ArrayList<Integer> data = new ArrayList<Integer>();
								ArrayList<Integer> cantidades = new ArrayList<Integer>();
								
								for (int i = 0; i < m.listaIngredientes.size(); i++) {
									if (b.hasMetadata(m.listaIngredientes.get(i))) {
										ingredientes.add(m.listaIngredientes.get(i));
										cantidades.add(b.getMetadata(m.listaIngredientes.get(i)).get(0).asInt());
										data.add(b.getMetadata(m.listaIngredientes.get(i)+"data").get(0).asInt());
									}
								}
								
								int numeroReceta = buscarMatch(ingredientes, cantidades, data);
								
								if (numeroReceta != -1) {
									if (p.hasPermission("gjobs.constructor"+m.nivelReceta.get(numeroReceta))) {
										String resultadoReceta = m.resultadoReceta.get(numeroReceta);
										int resultadoRecetaData = m.resultadoRecetaData.get(numeroReceta);
										int resultadoRecetaCantidad = m.cantidadResultadoReceta.get(numeroReceta);
										int tiempoReceta = m.tiempoPreparacion.get(numeroReceta);
										String herramienta = m.herramientaReceta.get(numeroReceta);
										Boolean calor = m.calor.get(numeroReceta);
										
										if (tiempoReceta > 0) {
											//Se pasa el tiempo a ticks de minecraft
											tiempoReceta *= 20;
											
											enviarMensajeSimple(p,ChatColor.YELLOW,"La mezcla ha empezado a hervir",10);
											if (calor) {
												if (b.hasMetadata("calor")) {
										            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
										            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
										                @Override
										                public void run() {
										                	b.setMetadata("resultadoreceta", new FixedMetadataValue(m,resultadoReceta));
										                	b.setMetadata(resultadoReceta, new FixedMetadataValue(m, "true"));
										                	b.setMetadata("resultadorecetadata", new FixedMetadataValue(m,resultadoRecetaData));
										                	b.setMetadata("cantidadresultado", new FixedMetadataValue(m,resultadoRecetaCantidad));
										                	b.setMetadata("herramienta", new FixedMetadataValue(m,herramienta));
										                	b.setMetadata("estado", new FixedMetadataValue(m,"recoger"));
															enviarMensajeSimple(p,ChatColor.GREEN,"Parece que la mezcla está lista.",10);
												            reproducirSonido(p,Sound.ENTITY_HORSE_EAT,5);
										                }
										            }, tiempoReceta);
												} else {
													//Se echa a perder la receta
													enviarMensajeSimple(p,ChatColor.RED,"La mezcla no ha terminado de asentarse y se ha echado a perder.",10);
													b.removeMetadata("left", m);
													b.removeMetadata("constructor", m);
													b.removeMetadata("estado", m);
													b.removeMetadata("calor", m);
													
													if (b.hasMetadata("resultadoreceta")) {
														b.removeMetadata(b.getMetadata("resultadoreceta").get(0).asString(), m);
													}
													
													for (String s : m.listaIngredientes) {
														b.removeMetadata(s, m);
													}
													
													BlockState d = b.getState();
									                d.getData().setData((byte) (c.getData()-1));
									                d.update();
													
										            reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY_LAVA,5);
												}
											} else {
												if (b.hasMetadata("calor")) {
													//Se echa a perder la receta
													enviarMensajeSimple(p,ChatColor.RED,"La mezcla ha hervido y se ha echado a perder",10);
													b.removeMetadata("left", m);
													b.removeMetadata("constructor", m);
													b.removeMetadata("estado", m);
													b.removeMetadata("calor", m);
													
													if (b.hasMetadata("resultadoreceta")) {
														b.removeMetadata(b.getMetadata("resultadoreceta").get(0).asString(), m);
													}
													
													for (String s : m.listaIngredientes) {
														b.removeMetadata(s, m);
													}
													
													BlockState d = b.getState();
									                d.getData().setData((byte) (c.getData()-1));
									                d.update();
													
										            reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY_LAVA,5);
												} else {
										            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
										            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
										                @Override
										                public void run() {
										                	b.setMetadata("resultadoreceta", new FixedMetadataValue(m,resultadoReceta));
										                	b.setMetadata(resultadoReceta, new FixedMetadataValue(m, "true"));
										                	b.setMetadata("resultadorecetadata", new FixedMetadataValue(m,resultadoRecetaData));
										                	b.setMetadata("cantidadresultado", new FixedMetadataValue(m,resultadoRecetaCantidad));
										                	b.setMetadata("herramienta", new FixedMetadataValue(m,herramienta));
										                	b.setMetadata("estado", new FixedMetadataValue(m,"recoger"));
															enviarMensajeSimple(p,ChatColor.GREEN,"Parece que la mezcla está lista.",10);
												            reproducirSonido(p,Sound.ENTITY_HORSE_EAT,5);
										                }
										            }, tiempoReceta);	
												}
											}
										} else {
											if (calor) {
												
												if (b.hasMetadata("calor")) {
								                	b.setMetadata("resultadoreceta", new FixedMetadataValue(m,resultadoReceta));
								                	b.setMetadata(resultadoReceta, new FixedMetadataValue(m, "true"));
								                	b.setMetadata("resultadorecetadata", new FixedMetadataValue(m,resultadoRecetaData));
								                	b.setMetadata("cantidadresultado", new FixedMetadataValue(m,resultadoRecetaCantidad));
								                	b.setMetadata("herramienta", new FixedMetadataValue(m,herramienta));
								                	b.setMetadata("estado", new FixedMetadataValue(m,"recoger"));
													enviarMensajeSimple(p,ChatColor.GREEN,"Parece que la mezcla está lista.",10);
										            reproducirSonido(p,Sound.ENTITY_HORSE_EAT,5);
												} else {
													//Se echa a perder la receta
													enviarMensajeSimple(p,ChatColor.RED,"La mezcla no ha terminado de asentarse y se ha echado a perder.",10);
													b.removeMetadata("left", m);
													b.removeMetadata("constructor", m);
													b.removeMetadata("estado", m);
													b.removeMetadata("calor", m);
													
													if (b.hasMetadata("resultadoreceta")) {
														b.removeMetadata(b.getMetadata("resultadoreceta").get(0).asString(), m);
													}
													
													for (String s : m.listaIngredientes) {
														b.removeMetadata(s, m);
													}
													
													BlockState d = b.getState();
									                d.getData().setData((byte) (c.getData()-1));
									                d.update();
													
										            reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY_LAVA,5);
												}
											} else {
												if (b.hasMetadata("calor")) {
													//Se echa a perder la receta
													enviarMensajeSimple(p,ChatColor.RED,"La mezcla ha hervido y se ha echado a perder.",10);
													b.removeMetadata("left", m);
													b.removeMetadata("constructor", m);
													b.removeMetadata("estado", m);
													b.removeMetadata("calor", m);
													
													if (b.hasMetadata("resultadoreceta")) {
														b.removeMetadata(b.getMetadata("resultadoreceta").get(0).asString(), m);
													}
													
													for (String s : m.listaIngredientes) {
														b.removeMetadata(s, m);
													}
													
													BlockState d = b.getState();
									                d.getData().setData((byte) (c.getData()-1));
									                d.update();
									                
									                reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY_LAVA,5);
												} else {
								                	b.setMetadata("resultadoreceta", new FixedMetadataValue(m,resultadoReceta));
								                	b.setMetadata(resultadoReceta, new FixedMetadataValue(m, "true"));
								                	b.setMetadata("resultadorecetadata", new FixedMetadataValue(m,resultadoRecetaData));
								                	b.setMetadata("cantidadresultado", new FixedMetadataValue(m,resultadoRecetaCantidad));
								                	b.setMetadata("herramienta", new FixedMetadataValue(m,herramienta));
								                	b.setMetadata("estado", new FixedMetadataValue(m,"recoger"));
								                	p.sendMessage(ChatColor.GREEN+"Parece que la mezcla está lista");
										            reproducirSonido(p,Sound.ENTITY_HORSE_EAT,5);
												}
											}
										}
									} else {
										enviarMensajeSimple(p,ChatColor.RED,"No sabes lo que haces y echas a perder la mezcla.",10);
										b.removeMetadata("left", m);
										b.removeMetadata("constructor", m);
										b.removeMetadata("estado", m);
										b.removeMetadata("calor", m);
										
										if (b.hasMetadata("resultadoreceta")) {
											b.removeMetadata(b.getMetadata("resultadoreceta").get(0).asString(), m);
										}
										
										for (String s : m.listaIngredientes) {
											b.removeMetadata(s, m);
										}
										
										BlockState d = b.getState();
						                d.getData().setData((byte) (c.getData()-1));
						                d.update();
										
							            reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY_LAVA,5);
									}
								} else {
									enviarMensajeSimple(p,ChatColor.RED,"La mezcla toma un color oscuro, se ha echado a perder.",10);
									b.removeMetadata("left", m);
									b.removeMetadata("constructor", m);
									b.removeMetadata("estado", m);
									b.removeMetadata("calor", m);
									
									if (b.hasMetadata("resultadoreceta")) {
										b.removeMetadata(b.getMetadata("resultadoreceta").get(0).asString(), m);
									}
									
									for (String s : m.listaIngredientes) {
										b.removeMetadata(s, m);
									}
									
									BlockState d = b.getState();
					                d.getData().setData((byte) (c.getData()-1));
					                d.update();
									
						            reproducirSonido(p,Sound.ITEM_BUCKET_EMPTY_LAVA,5);
								}
								
								break;
							
							case "recoger":
								//Si estás usando la herramienta adecuada
								if (b.getMetadata("herramienta").get(0).asString().equalsIgnoreCase(nombreItemInHand)) {
									for (ItemStack item : m.itemsCustomConstructor) {
										String nombreItem = "";
										
										if (item.hasItemMeta()) {
											nombreItem = item.getItemMeta().getDisplayName();
										} else {
											nombreItem = item.getType().toString();
										}
										if (b.getMetadata("resultadoreceta").get(0).asString().equalsIgnoreCase(nombreItem)) {
											if (b.getMetadata("resultadorecetadata").get(0).asInt() == item.getData().getData()) {
												int cantidadItem = b.getMetadata("cantidadresultado").get(0).asInt();
												
												if (cantidadItem > 0) {
													b.getLocation().getWorld().dropItem(b.getLocation(), item);
													cantidadItem--;
													b.setMetadata("cantidadresultado", new FixedMetadataValue(m, cantidadItem));
													
										            reproducirSonido(p,Sound.ITEM_BOTTLE_FILL,5);
													if (cantidadItem == 0) {
														b.removeMetadata("left", m);
														b.removeMetadata("constructor", m);
														b.removeMetadata("estado", m);
														b.removeMetadata("calor", m);
														
														p.sendMessage("No queda nada en el caldero");
														
														if (b.hasMetadata("resultadoreceta")) {
															b.removeMetadata(b.getMetadata("resultadoreceta").get(0).asString(), m);
														}
														
														for (String s : m.listaIngredientes) {
															b.removeMetadata(s, m);
														}
														
														BlockState d = b.getState();
										                d.getData().setData((byte) (c.getData()-1));
										                d.update();
													}
												}
												
												
											}
										}
									}
								}
								break;
	
						}
					}
					
				}
			}
		}
	}
	
	public int buscarMatch(ArrayList<String> ingredientes, ArrayList<Integer> cantidades, ArrayList<Integer> datas) {
		int nIngredientes = ingredientes.size();
		
		while(nIngredientes >= 0) {
			for (int i = m.ingredientesReceta.size()-1; i >= 0; i--) {
				ArrayList<String> tempIng = m.ingredientesReceta.get(i);
				ArrayList<Integer> tempData = m.ingredientesRecetaData.get(i);
				ArrayList<Integer> tempCant = m.cantidadIngredientesReceta.get(i);
				
				if (tempIng.size() == nIngredientes) {
					int contadorIngredientes = 0;
	
					for (String s : ingredientes) {
						if (tempIng.contains(s)) {
							int posicionIng = ingredientes.indexOf(s);
							int posicionCantidad = tempIng.indexOf(s);
	
							if (tempData.get(posicionCantidad) == datas.get(posicionIng)) {
								if (tempCant.get(posicionCantidad) == cantidades.get(posicionIng)) {
									contadorIngredientes++;
								}
							}
						}
						
					}
					
					if (contadorIngredientes == tempIng.size()) {
						//RECETA ENCONTRADA
						return i;
					}
				}
			}
			nIngredientes--;
		}
		return -1;
	}
	
	public void reproducirSonido(Player p, Sound sonido, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
			if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
				players.getWorld().playSound(p.getLocation(), sonido, 1.0F, 0.01F);
			}
        }
	}
	
	public void enviarMensajeSimple(Player p, ChatColor color, String mensaje, int rango) {
        for (Player players : Bukkit.getOnlinePlayers()) {
			if (p.getLocation().distanceSquared(players.getLocation()) <= 10) {
				players.sendMessage(color+mensaje);
			}
        }
	}
	
	 public static void ordenamientoBurbujaDescendente(ArrayList<ArrayList<String>> sortarray){	  
		 ArrayList<String> temp;
		 
		 for (int index= 0; index < sortarray.size()-1; index++) {
			 for (int compare = 0; compare< sortarray.size()-1; compare++) {
				 if (sortarray.get(compare).size()<sortarray.get(compare+1).size()){
					 temp = sortarray.get(compare);
					 sortarray.set(compare, sortarray.get(compare+1));
					 sortarray.set(compare+1,temp);
		     
				 }
		    
			 }
		 }
		 
	 }
}
