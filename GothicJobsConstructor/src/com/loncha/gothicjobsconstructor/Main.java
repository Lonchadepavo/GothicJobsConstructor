package com.loncha.gothicjobsconstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	CrafteosMesa cMesa = new CrafteosMesa(this);
	Construccion construccion = new Construccion(this);
	
	//ArrayLists de la configuración (se guardan en orden el bloque, la herramienta necesaria, el reusltado, el residuo y el nivel, 
	//si en alguno no hay nada se escribe un "")
	ArrayList<String> bloqueOrigen = new ArrayList<String>();
	ArrayList<Integer> bloqueOrigenData = new ArrayList<Integer>();
	ArrayList<String> herramientas = new ArrayList<String>();
	ArrayList<String> bloqueResultado = new ArrayList<String>();
	ArrayList<Integer> bloqueResultadoData = new ArrayList<Integer>();
	ArrayList<String> itemResiduo = new ArrayList<String>();
	ArrayList<Integer> nivelCrafteo = new ArrayList<Integer>();
	
	ArrayList<ItemStack> itemsCustomConstructor = new ArrayList<ItemStack>();
	
	//Bloques que se pueden construir por nivel
	ArrayList<Integer> bloqueConstruir = new ArrayList<Integer>();
	ArrayList<Integer> bloqueConstruirData = new ArrayList<Integer>();
	ArrayList<Integer> nivelBloqueConstruir = new ArrayList<Integer>();
	ArrayList<String> materialParaBloque = new ArrayList<String>();
	ArrayList<String> herramientaParaBloque = new ArrayList<String>();
	
	
	FileConfiguration configFile;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.cMesa, this);
		getServer().getPluginManager().registerEvents(this.construccion, this);
		
		getCommand("reloadconstructor").setExecutor(new Reload(this));
		
		rellenarListaCrafteosMesa();
		cargarItemsCustom();
		cargarBloquesConstruccion();
		
	}
	
	public void rellenarListaCrafteosMesa() {
		File config = new File("plugins/GothicJobsConstructor/config.yml");
		configFile = new YamlConfiguration();
		
		try {
			//Resetear los arrays
			bloqueOrigen = new ArrayList<String>();
			bloqueOrigenData = new ArrayList<Integer>();
			herramientas = new ArrayList<String>();
			bloqueResultado = new ArrayList<String>();
			bloqueResultadoData = new ArrayList<Integer>();
			itemResiduo = new ArrayList<String>();
			nivelCrafteo = new ArrayList<Integer>();
			
			configFile.load(config);
			
			for (String s : getCustomConfig().getConfigurationSection("bloques").getKeys(false)) {
				String dataOrigen = getCustomConfig().getString("bloques."+s+".dataorigen");
				String dataResultado = getCustomConfig().getString("bloques."+s+".dataresultado");
				String herramienta = getCustomConfig().getString("bloques."+s+".herramienta");
				String resultado = getCustomConfig().getString("bloques."+s+".resultado");
				String residuo = getCustomConfig().getString("bloques."+s+".residuo");
				int nivel = getCustomConfig().getInt("bloques."+s+".nivel");
				
				bloqueOrigen.add(getCustomConfig().getString("bloques."+s+".tipo"));
				herramientas.add(herramienta);
				bloqueResultado.add(resultado);
				if (residuo != null) {
					itemResiduo.add(residuo);
				}
				
				if (dataOrigen == null) {
					bloqueOrigenData.add(0);
				} else {
					bloqueOrigenData.add(Integer.parseInt(dataOrigen));
				}
				
				if (dataResultado == null) {
					bloqueOrigenData.add(0);
				} else {
					bloqueResultadoData.add(Integer.parseInt(dataResultado));
				}
				nivelCrafteo.add(nivel);
				
			}

		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void cargarItemsCustom(){
		File config = new File("plugins/GothicJobsConstructor/itemscustom.yml");
		
		configFile = new YamlConfiguration();
		
		try {
			//Resetear arrays
			itemsCustomConstructor = new ArrayList<ItemStack>();
			
			configFile.load(config);
		
			if (getCustomConfig().getConfigurationSection("items").getKeys(true) != null) {
				for (String s : getCustomConfig().getConfigurationSection("items").getKeys(false)) {
					String nombre, material;
					int cantidad, data;
					List<String> lore = new ArrayList<String>();
					
					nombre = getCustomConfig().getString("items."+s+".nombre");
					material = getCustomConfig().getString("items."+s+".material");
					data = getCustomConfig().getInt("items."+s+".data");
					cantidad = getCustomConfig().getInt("items."+s+".cantidad");
					lore = getCustomConfig().getStringList("items."+s+".lore");
					
					ItemStack is = new ItemStack(Material.getMaterial(material), cantidad, (short)data);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(nombre);
					im.setLore(lore);
					is.setItemMeta(im);
					
					itemsCustomConstructor.add(is);
					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cargarBloquesConstruccion() {
		File config = new File("plugins/GothicJobsConstructor/bloques.yml");
		
		configFile = new YamlConfiguration();
		try {
			//Resetear arrays
			bloqueConstruir = new ArrayList<Integer>();
			bloqueConstruirData = new ArrayList<Integer>();
			nivelBloqueConstruir = new ArrayList<Integer>();
			materialParaBloque = new ArrayList<String>();
			herramientaParaBloque = new ArrayList<String>();
			
			configFile.load(config);
			
			for (String s : getCustomConfig().getConfigurationSection("bloques").getKeys(false)) {
				bloqueConstruir.add(getCustomConfig().getInt("bloques."+s+".idbloque"));
				bloqueConstruirData.add(getCustomConfig().getInt("bloques."+s+".data"));
				nivelBloqueConstruir.add(getCustomConfig().getInt("bloques."+s+".nivel"));
				materialParaBloque.add(getCustomConfig().getString("bloques."+s+".material"));
				herramientaParaBloque.add(getCustomConfig().getString("bloques."+s+".herramienta"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getCustomConfig() {
		return this.configFile;
	}

}
