package Reto;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/v1")
public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public Gson gson = new Gson();
	public BaseEntity entity;
    
	//Metodo que obtiene el request de tipo post hecho desde la pagina web
	 @Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	    
			try {
				response.getWriter().append(gson.toJson(handleRequest(request, response,"POST")));
			} catch (Exception e) {
				throw new ServletException(e.getMessage());
			}
		} 

		//Metodo que obtiene el request de tipo delete hecho desde la pagina web
	 @Override
		protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	   
			try {
				response.getWriter().append(gson.toJson(handleRequest(request, response,"DELETE")));
			} catch (Exception e) {
				throw new ServletException(e.getMessage());
			}
		}
	 

		//Metodo que obtiene el request de tipo get hecho desde la pagina web
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.getWriter().append(gson.toJson(handleRequest(request, response,"GET")));
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
	}


	//Metodo que procesa el request en base al tipo de argumento recibido
   	public Object handleRequest(HttpServletRequest request, HttpServletResponse response, String method) throws Exception {
   		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
		String pathInfo = request.getPathInfo();
        initEntity();
		if (pathInfo != null || method == "POST") {
			//Atributos de la url del request
			String[] pathAttributes = pathInfo == null ? "/".split("/") : pathInfo.split("/");
			switch (method) { 
			case "POST":  
		    	if (entity == null) {
		        	throw new ServletException("Route not found");
		    	}
		    	if (pathAttributes.length == 2) {
					entity.find(pathAttributes[1]);
		    	}
		    	
				Map<String, String> data = new HashMap<String, String>();
				
				for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet())
				{
					String value = "";
					if (param.getValue() != null && param.getValue().length > 0) {
						value = param.getValue()[0];
					}
					data.put(param.getKey(), value);
				}
				entity.setData(data);
			   String oprState = entity.save();
				
				return oprState;
			case "DELETE":		    	  
					 return entity.delete(pathAttributes[1]);
			case "GET":
			         return entity.find(pathAttributes[1]);
			}
		} else if (method == "GET") {
			return entity.getAll();
		} else {
			throw new ServletException("Ruta no encontrada");
		}
		return null;
	}

	public void initEntity() {
		entity = null;
	}
}
