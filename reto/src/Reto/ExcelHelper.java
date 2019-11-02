package Reto;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.istack.Nullable;

import ws.CedulaValida;

public class ExcelHelper {
	
	public String save(BaseEntity entity) throws Exception {
		 try {
			 
			 	URL url = new URL("http://localhost:8082/CedulaWS/cedulaServlet?wsdl");
				QName qname = new QName("http://ws/", "CedulaValidaImpService");

			    Service service = Service.create(url, qname);

			    CedulaValida hello = service.getPort(CedulaValida.class);
			    
		    	//Convertir la entidad en objeto tipo json para obtener sus valores genericamente 
				JsonObject data = (new Gson()).toJsonTree(entity).getAsJsonObject();

     		    String Cedula = data.get("Cedula").getAsString();
     		    
     		    
			    boolean valido = hello.CedulaEsValida(Cedula);
			    
			    if (valido) {

	     		    BaseEntity entidad = find("'" + Cedula + "'",new BaseEntity(),"select * from persona where Cedula =");
	     		    
	     		    if(entidad.Id == 0) {
			    	
					  String query = "";
					  String columns = "";

					  for (Map<String,String> item: entity.getProperties()) { 
						  String itemText = String.valueOf(item.keySet().toArray()[0]);
						  String itemTypeText = String.valueOf(item.values().toArray()[0]);
						  if (itemText != "Id") {
							  switch (itemTypeText) {
								case "java.lang.String":
									  columns += itemText + ",";
									  query += "'"+ data.get(itemText).getAsString() + "'" + ",";
									break;
								case "int": case "boolean":
									  columns += itemText +",";
									  query += data.get(itemText).getAsString() + ",";
									break;
						  }
					   }
					  }
					  
					  Class.forName("com.mysql.jdbc.Driver");  
						Connection con=DriverManager.getConnection(  
						"jdbc:mysql://localhost:3306/opensourcedb","root","");  
						//here sonoo is database name, root is username and password  
						Statement stmt=con.createStatement();  
						String values = query.substring(0, query.length() - 1);
						String col = columns.substring(0, columns.length() - 1);
						String queryExcute = "";
						 if (entity.Id == 0) {
								queryExcute = "insert into persona ("+ col +") values(" + values + ")";
						 } else {
							    String queryValues = "set ";
							    String[] colArray = col.split(",");
							    String[] valuesArray = values.split(",");
							    for (int x =0; x < colArray.length; x++) {
							    	queryValues += colArray[x] + " = " + valuesArray[x] + ",";
							    }
							    queryValues = queryValues.substring(0, queryValues.length() - 1);
								queryExcute = "update persona "+ queryValues +" where id =" + entity.Id;
						 }
						stmt.executeUpdate(queryExcute);  
						con.close();
						return "Satisfactorio.";
	     		    } else {
						return "Esta cédula ya existe en el sistema.";
	     		    }
			    } else {
			    	return "Cédula no valida.";
			    }
		  } catch (Exception ex) { 
		    	return "Ha ocurrido un error: " + ex.getMessage();
			  }
	}
	
	public boolean delete(int personaId) throws Exception {
		
		  try {
			  Class.forName("com.mysql.jdbc.Driver");  
				Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost:3306/opensourcedb","root","");  
				//here sonoo is database name, root is username and password  
				Statement stmt=con.createStatement();  
				stmt.executeUpdate("Update persona set Estado = 0 Where Id =" + personaId);  
				con.close();
		  
		  return true;
		  } catch (Exception ex) { 
			  throw ex;
			  }
	}
	
	public ArrayList<Map<String, String>> findAll(BaseEntity entity) throws Exception {

		ArrayList<Map<String, String>> productos = new ArrayList<Map<String,String>>(); 
		
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost:3306/opensourcedb","root","");  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("select * from persona where Estado = 1");  
			String pg;
			while(rs.next())  {
				Map<String, String> producto = new HashMap<String, String>(); 
				  for (Map<String,String> item: entity.getProperties()) { 
				  producto.put(String.valueOf(item.keySet().toArray()[0]), rs.getString(String.valueOf(item.keySet().toArray()[0])));
				  }		
				  productos.add(producto);
			}
			con.close();  
			}catch(Exception e){ 
				System.out.println(e);
			}  
		
		return productos;
	}

	public BaseEntity find(Object queryParameter, BaseEntity entity, @Nullable String query) throws Exception {
			
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost:3306/opensourcedb","root","");  
			//here sonoo is database name, root is username and password  
			Statement stmt=con.createStatement();  
			
			String queryExecute;
			
			if (query != null) {
				queryExecute = query;
			} else {
				queryExecute = "select * from persona where Id =";
			}
			
			ResultSet rs=stmt.executeQuery(queryExecute + queryParameter);  
			String pg;

			if (rs.next()) {
			 Map<String, String> personas = new HashMap<String, String>(); 
			 for (Map<String,String> item: entity.getProperties()) { 
				 String textItem = rs.getString(String.valueOf(item.keySet().toArray()[0]));
				 personas.put(String.valueOf(item.keySet().toArray()[0]), textItem);
			 }
			 entity.setData(personas);
			}
			
			con.close();  
			
			}catch(Exception e){ 
				System.out.println(e);
			}  
		 
		 return entity;
	}
	

	/*
	 * //Retorna las columnas que tiene la primera fila de la hoja del excel public
	 * Map<String,Integer> getColumns(XSSFRow firstRow) { Map<String, Integer>
	 * columns = new HashMap<String, Integer>(); Iterator<Cell> cells =
	 * firstRow.cellIterator(); int cellIndex = 0; while (cells.hasNext()) { Cell
	 * cell = cells.next(); columns.put(cell.getStringCellValue(), cellIndex++); }
	 * return columns; }
	 */
}
