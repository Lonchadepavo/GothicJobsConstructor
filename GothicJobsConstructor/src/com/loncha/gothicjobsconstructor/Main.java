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
	CrafteoCemento cCemento = new CrafteoCemento(this);
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
	
	//Arraylists para el cemento
	List<String> listaIngredientes = new ArrayList<String>();
	
	ArrayList<String> resultadoReceta = new ArrayList<String>();
	ArrayList<Integer> resultadoRecetaData = new ArrayList<Integer>();
	ArrayList<Integer> cantidadResultadoReceta = new ArrayList<Integer>();
	ArrayList<ArrayList<String>> ingredientesReceta = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<Integer>> ingredientesRecetaData = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> cantidadIngredientesReceta = new ArrayList<ArrayList<Integer>>();
	ArrayList<String> herramientaReceta = new ArrayList<String>();
	ArrayList<Boolean> calor = new ArrayList<Boolean>();
	ArrayList<Integer> tiempoPreparacion = new ArrayList<Integer>();
	ArrayList<Integer> nivelReceta = new ArrayList<Integer>();
	
	ArrayList<ItemStack> itemsCustomConstructor = new ArrayList<ItemStack>();
	
	//Bloques que se pueden construir por nivel
	ArrayList<Integer> bloqueConstruir = new ArrayList<Integer>();
	ArrayList<Integer> bloqueConstruirData = new ArrayList<Integer>();
	ArrayList<Integer> nivelBloqueConstruir = new ArrayList<Integer>();
	
	
	FileConfiguration configFile;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.cMesa, this);
		getServer().getPluginManager().registerEvents(this.cCemento, this);
		getServer().getPluginManager().registerEvents(this.construccion, this);
		
		getCommand("reloadconstructor").setExecutor(new Reload(this));
		
		rellenarListaCrafteosMesa();
		rellenarListaRecetas();
		rellenarListaIngredientes();
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
				
				bloqueOrigen.add(s);
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
	
	public void rellenarListaRecetas() {
		File config = new File("plugins/GothicJobsConstructor/recetas.yml");
		configFile = new YamlConfiguration();
		
		try {
			//Resetear arrays
			resultadoReceta = new ArrayList<String>();
			resultadoRecetaData = new ArrayList<Integer>();
			cantidadResultadoReceta = new ArrayList<Integer>();
			ingredientesReceta = new ArrayList<ArrayList<String>>();
			ingredientesRecetaData = new ArrayList<ArrayList<Integer>>();
			cantidadIngredientesReceta = new ArrayList<ArrayList<Integer>>();
			herramientaReceta = new ArrayList<String>();
			calor = new ArrayList<Boolean>();
			tiempoPreparacion = new ArrayList<Integer>();
			nivelReceta = new ArrayList<Integer>();
			
			configFile.load(config);
			
			for (String s : getCustomConfig().getConfigurationSection("recetas").getKeys(false)) {
				String[] tempResultadoReceta = getCustomConfig().getString("recetas."+s+".resultado").split(",");
				resultadoReceta.add(tempResultadoReceta[0]);
				resultadoRecetaData.add(Integer.valueOf(tempResultadoReceta[1]));
				cantidadResultadoReceta.add(Integer.valueOf(tempResultadoReceta[2]));
				
				List<String> tempIngredientesRaw = getCustomConfig().getStringList("recetas."+s+".ingredientes");
				
				ArrayList<String> tempIngredientes = new ArrayList<String>();
				ArrayList<Integer> tempIngredientesRecetaData = new ArrayList<Integer>();
				ArrayList<Integer> tempCantidadIngredientes = new ArrayList<Integer>();
				
				for (String s2 : tempIngredientesRaw) {
					String[] temp2 = s2.split(",");
					tempIngredientes.add(temp2[0]);
					tempIngredientesRecetaData.add(Integer.valueOf(temp2[1]));
					tempCantidadIngredientes.add(Integer.valueOf(temp2[2]));
				}
				
				ingredientesReceta.add(tempIngredientes);
				ingredientesRecetaData.add(tempIngredientesRecetaData);
				cantidadIngredientesReceta.add(tempCantidadIngredientes);
				tiempoPreparacion.add(getCustomConfig().getInt("recetas."+s+".tiempo"));
				nivelReceta.add(getCustomConfig().getInt("recetas."+s+".nivel"));
				
				herramientaReceta.add(getCustomConfig().getString("recetas."+s+".herramienta"));
				calor.add(getCustomConfig().getBoolean("recetas."+s+".calor"));
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void rellenarListaIngredientes() {
		File config = new File("plugins/GothicJobsConstructor/recetas.yml");
		configFile = new YamlConfiguration();
		
		try {
			//Resetear arrays
			listaIngredientes = new ArrayList<String>();
			
			configFile.load(config);
			
			listaIngredientes = getCustomConfig().getStringList("ingredientes");

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
			
			configFile.load(config);
			
			for (String s : getCustomConfig().getConfigurationSection("bloques").getKeys(false)) {
				System.out.println(s);
				bloqueConstruir.add(getCustomConfig().getInt("bloques."+s+".idbloque"));
				bloqueConstruirData.add(getCustomConfig().getInt("bloques."+s+".data"));
				nivelBloqueConstruir.add(getCustomConfig().getInt("bloques."+s+".nivel"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getCustomConfig() {
		return this.configFile;
	}

}
