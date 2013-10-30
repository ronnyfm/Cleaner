package parce.cl.subaru;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class Container {
	
	public static Map<String, ContainerComponent> getConatiners(Map<String, String> containerNameMap)
	{
		Map<String, ContainerComponent> containerMap = new HashMap<String, ContainerComponent>();
		
		File dir = new File("C:/CARMICHAEL/site_opt/content/current/private/containers");
		
		List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			
		ContainerComponent container =null;
		
		for (File file : files) {			
			String fileContainerName = "/"+file.getName().replaceAll(".container", "").replace(".", "/") +"/";
			container = new ContainerComponent(file.getName(),fileContainerName);
			
			boolean isContainerInUse =addToContainerNameMap( file, containerNameMap, fileContainerName);
			
			if(isContainerInUse)
			{
				container.setInUse(true);
			}
			containerMap.put(fileContainerName, container);	
		}
		return containerMap;
	}
	
	private static boolean addToContainerNameMap(File file, Map<String, String> containerNameMap, String fileConatainerName)
	{		
		 Nodes resultadosNamespaceXP = getContainerNodesFromXML(file,"//container");  
	     Element etiquetaNamespaceXP = (Element)resultadosNamespaceXP.get(0); 
	     String containerUri = etiquetaNamespaceXP.getAttributeValue("uri");
	     //String containerType = etiquetaNamespaceXP.getAttributeValue("type");
	     String containerType=null;
	     containerUri= fixPath(containerUri);
	     
	     containerNameMap.put(fileConatainerName,containerUri); 
	     
	     if(!containerUri.equalsIgnoreCase(fileConatainerName))
	     {
	    	System.out.println("uri: "+containerUri + " **********  " +fileConatainerName) ;
	     } 
	     
	     //busco los tag:type/events
	     File file2 = new File("C:/CARMICHAEL/site_opt/content/current/public"+containerUri+"article.xml");
	     Nodes resultadoTags = getContainerNodesFromXML(file2,"//tags/tag"); 
	     
	     if(resultadoTags!=null)
	     {
	    	 int p =0;
	    	 for(int i=0;i<resultadoTags.size();i++)
	        {
	        	Element tag = (Element)resultadoTags.get(i); 
	        	String type = tag.getAttributeValue("type");
	        	String name = tag.getAttributeValue("name");
	        	if(type!=null && type.equalsIgnoreCase("type") && name.equalsIgnoreCase("event"))
	        	{
	        		return true;
	        	}
	        	
	        	if(type.equalsIgnoreCase("interest")||
	        			type.equalsIgnoreCase("zipcode")||
	        			type.equalsIgnoreCase("posts")||
	        			type.equalsIgnoreCase("events")||
	        			type.equalsIgnoreCase("mobile_vehicle")||
	        			type.equalsIgnoreCase("mobile_vehicle_models")||
	        			type.equalsIgnoreCase("special-offers-region-zone")||
	        			type.equalsIgnoreCase("special-offers-program")||
	        			type.equalsIgnoreCase("special-offers-model-year")||
	        			type.equalsIgnoreCase("special-offers-program-order"))
	        	{
	        		return true;
	        	}
	        }
	     }
	     return false;

	}
	
	//busca si el container esta siendo referenciado en un directorio <container/> y retorna los archivos que lo estan referenciado
	public static void findContainersInPTPs(Map<String, ContainerComponent> containerMap, Map<String, String> containerNameMap)
	{		
		File dirPTP = new File("C:/CARMICHAEL/site_opt/content/current/private/ptp");
		List<File> filesdirPTP = (List<File>) FileUtils.listFiles(dirPTP, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File filedirPTP : filesdirPTP) {
			findContainersInPTP(filedirPTP, containerMap, containerNameMap);				
		}
	}
	
	private static void findContainersInPTP(File filedir, Map<String, ContainerComponent> containerMap, Map<String, String> containerNameMap)
	{
		try
		{
	        Nodes resultadosNamespaceXP = getContainerNodesFromXML(filedir,"//container");  
	        for(int i=0;i<resultadosNamespaceXP.size();i++)
	        {
	        	Element etiquetaNamespaceXP = (Element)resultadosNamespaceXP.get(i); 
	        	//ContainerComponent container = containerMap.remove(containerNameMap.get(etiquetaNamespaceXP.getAttributeValue("name")));
	        	
	        	String containerPath = finContainerPath(containerNameMap,etiquetaNamespaceXP.getAttributeValue("name"));
	        	ContainerComponent container =null;
	        	
	        	if(containerPath!=null)
	        	{
	        		container = containerMap.remove(containerPath);
	        	} 
	        	
	        	if(container!=null)
	        	{
	        		container.setInUse(true);
	        		containerMap.put(container.getPath(), container);
	        	}  else
	        	{
	        		int p=0;
	        	}
	        } 
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION ERROR:  " + e.getMessage());
		}		
	}
	
	private static Nodes getContainerNodesFromXML(File filedir, String expression )
	{
		try
		{
			FileInputStream file = new FileInputStream(filedir);
			
			// Creamos el builder XOM  
			Builder builder = new Builder();  
			// Construimos el arbol DOM a partir del fichero xml  
			Document doc = builder.build(file);
			// Obtenemos la etiqueta raíz  
			Element raiz = doc.getRootElement(); 
	
	        XPath xPath =  XPathFactory.newInstance().newXPath();
	
	        XPathContext context = new XPathContext("comp","http://www.subaru.com/schema");  
	        return raiz.query(expression,context); 
		}
        catch(Exception e)
		{
			System.out.println("EXCEPTION ERROR:  " + e.getMessage());
			return null;
		}
	}
		
	//busca si el container esta siendo referenciado en un directorio <container/> y retorna los archivos que lo estan referenciado
	public static void findContainersTemplates(Map<String, ContainerComponent> containerMap)
	{	
		File dirTEMPLATE = new File("C:/CARMICHAEL/site_opt/content/current/private/templates");
		List<File> filesdirTEMPLATES = (List<File>) FileUtils.listFiles(dirTEMPLATE, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File filedirTEMPLATE : filesdirTEMPLATES) {
			findContainersTemplate(filedirTEMPLATE, containerMap);
		}
	}
	
	private static void findContainersTemplate(File filedir, Map<String, ContainerComponent> containerMap)
	{
		try
		{
			org.jsoup.nodes.Document doc = Jsoup.parse(filedir, "UTF-8", "http://subaru.com/");
			Elements compGetComponents = doc.getElementsByTag("container");
			//org.jsoup.nodes.Element content = doc.getElementById("container");
			//Elements compGetComponents = content.getElementsByTag("comp:getComponent");
			//System.out.println("jsp:  "+ filedir.getAbsolutePath() + compGetComponents.size());
			for (org.jsoup.nodes.Element compGetComponent : compGetComponents) {
			  String componentName = compGetComponent.attr("name");
			  ContainerComponent container = containerMap.remove(componentName);
	        	if(container!=null)
	        	{
	        		container.setInUse(true);
	        		containerMap.put(container.getPath(), container);
	        	}  			  
			}     
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION ERROR:  " + e.getMessage());
		}		
	}
	
	public static void getContainContainersIncomponentMap(Map<String,ContainerComponent> containerMap, Map<String,ContainerComponent> componentMap )
	{
		for (Map.Entry<String,ContainerComponent> entry : componentMap.entrySet()) {
		    ContainerComponent component = entry.getValue();
		    String componentPath = (component.getName().replace("C:\\CARMICHAEL\\site_opt\\content\\current\\private","") +"\\").replace("\\", "/");
		    ContainerComponent container = containerMap.remove(componentPath);
        	if(container!=null)
        	{
        		container.setInUse(true);
        		containerMap.put(container.getPath(), container);
        	} 
		   
		}
	}
	
	public static void deleteEmptyNotPathContainers(Map<String, ContainerComponent> containerMap,Map<String, ContainerComponent> componenteMap, Map<String, String> containerNameMap) throws IOException
	{
		ArrayList<String> deleteList = new ArrayList<>();
		int ContNoexiste=0;
		int Contvacio=0;
		int ContExistenteConDatos=0;
		String content;
		File file = new File("containersEliminadosEmptyNotPath.txt");
		 
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for (Map.Entry<String,ContainerComponent> entry : containerMap.entrySet()) {
		    ContainerComponent container = entry.getValue();
		   
		    String containerPath = containerNameMap.get(container.getPath());
		   
		    if(containerPath!=null)
		    {
			    containerPath=fixPath(containerPath);
		    }
		    else{
		    	int p=0;}
		    String key = entry.getKey();
		    File dir2 = new File("C:/CARMICHAEL/site_opt/content/current/private"+containerPath);
		   
		    if(!dir2.exists())
			{
		    	File dir3 = new File("C:/CARMICHAEL/site_opt/content/current/public"+containerPath);
		    	if(!dir3.exists())
				{
		    		ContNoexiste++; 
		    		content = "container path no existente: " + container.getPath() +"\n";	 
					bw.write(content);
					dir3 = new File("C:/CARMICHAEL/site_opt/content/current/private/containers/"+container.getName());
					generateBackUpFile(dir3);
					dir3.delete();
					deleteList.add(key);
				}
		    	else
		    	{
		    		dir2 = dir3;
		    	}
			}
		    
			if(dir2.exists() && dir2.isDirectory() && dir2.list().length==0)
			{
					Contvacio++;
					content = "container vacio: " + container.getPath() +"\n";
					bw.write(content);
					//borro path fisico
					generateBackUpFile(dir2);
					dir2.delete();					
					File dir4 = new File("C:/CARMICHAEL/site_opt/content/current/private/containers/"+container.getName());
					//borro .container file
					generateBackUpFile(dir4);
					dir4.delete();					
					deleteList.add(key);
			}
			else
			{
				if(dir2.exists() && dir2.isDirectory())
				{
					ContExistenteConDatos++;
					if(dir2.list().length==1 && dir2.list(TrueFileFilter.INSTANCE)[0].contains(".mtl"))
					{
						String filename =dir2.list(TrueFileFilter.INSTANCE)[0];
						if(new File(dir2.getAbsolutePath() + "\\"+ filename).isFile())
						{								
							System.out.println("folder with 1 filecontainer: "+dir2.getAbsolutePath() + " ....... " + filename);
							Componente.findOnMTLs(filename,componenteMap);
						}						
					}
				}
			}		   
		}
		bw.close();
		for(String key:deleteList)
		{
			containerMap.remove(key);
		}
		
		System.out.println("numero no existentePath: "+ContNoexiste);
		System.out.println("numero existentes vacios: "+Contvacio);
		System.out.println("numero existentes con datos: "+ContExistenteConDatos);
	}
	
	public static void deleteContainertNoInUse(Map<String, ContainerComponent> containerMap) throws IOException
	{
		String content;
		File fileToDelete;
		File file = new File("containersEliminadosNotInUse.txt");
		 
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		int cont = 0;
		for (Map.Entry<String,ContainerComponent> entry : containerMap.entrySet()) {
		    ContainerComponent container = entry.getValue();	
    		if(!container.isInUse())
    		{
    			fileToDelete = new File("C:/CARMICHAEL/site_opt/content/current/private/containers/"+container.getName());
    			generateBackUpFile(fileToDelete);
    			fileToDelete.delete();
	    		content = entry.getKey() + " \n";	 
				bw.write(content);
				cont++;
    		}
		}
		bw.close();
		System.out.println("Se eliminaron containers: " +cont);
		
	}
	
	public static void generateBackUpFile(File file)
	{
		try {
			File fileBackup = new File(file.getPath().replace("\\site_opt\\", "\\site_opt_BACKUP\\").replace("C:\\CARMICHAEL\\",""));
			// if file doesnt exists, then create it
			if (!fileBackup.exists()) {				
					fileBackup.mkdirs();	
					Files.copy(file.toPath(), fileBackup.toPath(), REPLACE_EXISTING);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String finContainerPath(Map<String, String> containerNameMap, String key)
	{
		key = fixPath(key);
		String valueReturn = null;
		for (Map.Entry<String,String> entry : containerNameMap.entrySet()) {
		    String value = entry.getValue();
		    if(value.equalsIgnoreCase(key))
		    {
		    	valueReturn = entry.getKey();
		    	break;
		    }
		}
		if(valueReturn==null)
		{
			
			int p=0;
		}
		return valueReturn;
	}
	
	
	private static String fixPath(String containerPath)
	{
		if (!containerPath.startsWith("/"))
        {
	    	containerPath= "/"+containerPath;
        }
	    if (!containerPath.endsWith("/"))
        {
	    	containerPath= containerPath+"/";
        }
	    return containerPath;
	}
}
