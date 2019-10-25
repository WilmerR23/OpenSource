package Reto;

import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BaseEntity {
	
	private transient ExcelHelper ExcelHelper;
	public int Id = 0;
	public boolean Estado = true;

	public ArrayList<Map<String,String>> getProperties() {
		ArrayList<Map<String,String>> properties = new ArrayList<Map<String,String>>();
		
		Map<String,String> propId = new HashMap<String,String>();
		propId.put("Id", "int");
		
		Map<String,String> propEstado = new HashMap<String,String>();
		propEstado.put("Estado", "boolean");
		

		properties.add(propId);
		properties.add(propEstado);
		
		for (Field field: getClass().getDeclaredFields()) {		
			Map<String,String> prop = new HashMap<String,String>();
			prop.put(field.getName(), field.getType().getName());
			properties.add(prop);
		}
				
		return properties;
	}
	
	public BaseEntity() throws InvalidFormatException, IOException, Exception {
		ExcelHelper = new ExcelHelper();
	}
	
	public String save() throws Exception {
		return ExcelHelper.save(this);
	}
	
	public ArrayList<Map<String, String>> getAll() throws Exception {
		return ExcelHelper.findAll(this);
	}

	public BaseEntity find(String id) throws Exception {
		int rowId = Integer.parseInt(id);
		return ExcelHelper.find(rowId,this, null);
	}
	
	public boolean delete(String id) throws Exception {
		int rowId = Integer.parseInt(id);
		return ExcelHelper.delete(rowId);
	}
	

	//Metodo que coloca la data en los campos de la entidad con la clases del paquete reflection para guardar o actualizar
	public void setData(Map<String,String> productos) {
		
		if (productos.containsKey("Id")) {			
			Id = Integer.parseInt(productos.get("Id"));
		}

		for (Field field: getClass().getDeclaredFields()) {
			if (!productos.containsKey(field.getName())) {
				continue;
			}
			
			try {
				switch (field.getType().getName()) {
					case "java.lang.String":
						String cadena = String.valueOf(productos.get(field.getName()));
						field.set(this,cadena);
						break;
				/*
				 * case "java.util.Date": try { SimpleDateFormat formatter = new
				 * SimpleDateFormat("yyyy-MM-dd"); String dateInString =
				 * productos.get(field.getName()); Date date =
				 * formatter.format(arg0)(dateInString); field.set(this, date); } catch
				 * (ParseException e) { e.printStackTrace(); } break;
				 */
					case "int":
						field.setInt(this, Integer.parseInt(productos.get(field.getName())));
						break;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
}
