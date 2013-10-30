package parce.cl.subaru;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ContainerCheckJustPTP {


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		System.out.println("STARTING at: "+(new Date()).toString());
		//inicializacion...
		Map<String, ContainerComponent> component2Map = Componente.getComponents();
		System.out.println("numero de componentes inicial: "+component2Map.size());
		Map<String, String> containerNameMap = new HashMap<String,String>();
		Map<String, ContainerComponent> containerMap = Container.getConatiners(containerNameMap);
		
		setContainerEventsType(containerMap);
		
		NonUseContainer.setNonUseContainer(containerMap, containerNameMap);
		
		System.out.println("numero de containers inicial: "+containerMap.size());
		
		//busco componentes en uso y seteo bandera entru si los encuentra
		Componente.findComponentes(component2Map,Componente.TYPE_PTP);
		Componente.findComponentes(component2Map,Componente.TYPE_TEMPLATE);
		Componente.findComponentes(component2Map,Componente.TYPE_MTL);
		
		//IMPRIMO EN TXT COMPONENTES EN USO Y LOS QUE NO
	    printMapInUseAndNotInUse(component2Map, "COMPONENTE_USO_Y_NO_USO");
		
	    //elimino los componentes que no estan siendo referenciados en PTP TEMPLATE or MTL
		Componente.deleteComponentNoInUse(component2Map);	
		removeFromMapNotInUse(component2Map,"COMPONENTES_Deleated");
		System.out.println("numero de componentes after deleated: "+component2Map.size());
		
		//IMPRIMO EN TXT COMPONENTES EN USO Y LOS QUE NO
		printMapInUseAndNotInUse(component2Map, "COMPONENTE_USO");
		
		
		Container.findContainersInPTPs(containerMap,containerNameMap);
		
		Container.findContainersTemplates(containerMap);
		
		//Container.getContainContainersIncomponentMap(containerMap,component2Map);
		
		//IMPRIMO EN TXT CONTAINERS EN USO Y LOS QUE NO
		printMapInUseAndNotInUse(containerMap,"CONTAINERS_USO_Y_NO_USO");
		
		//elimino los containers que estan vacios o que no tienen path valido (private or public)
		Container.deleteEmptyNotPathContainers(containerMap,component2Map, containerNameMap);
		//elimino los componentes que no estan siendo referenciados en PTP TEMPLATE or MTL
		Container.deleteContainertNoInUse(containerMap);	
		removeFromMapNotInUse(containerMap,"CONTAINERS_Deleated");
		
		//IMPRIMO EN TXT CONTAINERS EN USO Y LOS QUE NO
		printMapInUseAndNotInUse(containerMap,"CONTAINERS_USO");		
		
		
		System.out.println("ENDING at: "+(new Date()).toString());
		
		
	}
	
	private static void printMapInUseAndNotInUse(Map<String, ContainerComponent> map, String mapName)
	{
		int cont=0;
		int cont2=0;
		for (Map.Entry<String,ContainerComponent> entry : map.entrySet()) {
		    String key = entry.getKey();
		    ContainerComponent container = entry.getValue();
		    if(!container.isInUse())
		    {
		    	//System.out.println(container.getName() +" : "+container.getPath());
		    	cont++;
		    }else
		    {
		    	cont2++;
		    }
		}		
		System.out.println(mapName + " not in use total : "+cont);
		System.out.println(mapName + "  in use total : "+cont2);
		try
		{
			printTxtComponentMap(map, mapName);
		}catch(Exception e)
		{
			System.out.println("ERROR: ---- "+e.getMessage());
		}
	}
	
	public static void printTxtComponentMap(Map<String, ContainerComponent> componentrMap, String mapName) throws IOException
	{
		String content;
		File file = new File(mapName+".txt");
		 
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for (Map.Entry<String,ContainerComponent> entry : componentrMap.entrySet()) {
		    ContainerComponent container = entry.getValue();		    
    		content = entry.getKey() + " - " + container.isInUse() +" \n";	 
			bw.write(content);
		}
		bw.close();
	}
	
	private static void removeFromMapNotInUse(Map<String, ContainerComponent> map, String mapName)
	{
		try
		{
			ArrayList<String> deleteList = new ArrayList<>();
			int cont=0;
			Map<String, ContainerComponent> mapDeleted = new HashMap<String, ContainerComponent>();
			for (Map.Entry<String,ContainerComponent> entry : map.entrySet()) {
			    ContainerComponent container = entry.getValue();
			    String key = entry.getKey();
			    if(!container.isInUse())
			    {
			    	deleteList.add(key);			    	
			    	cont++;
			    }
			}
			System.out.println(mapName + " not in use total : "+cont+" de un total de :" +map.size());
			for(String key:deleteList)
			{
				ContainerComponent containerComponent= map.remove(key);
		    	mapDeleted.put(key,containerComponent);
			}
			
			
			printTxtComponentMap(map, mapName);
		}catch(Exception e)
		{
			System.out.println("ERROR: ---- "+e.getMessage());
		}
	}
	
	public static void setContainerEventsType(Map<String, ContainerComponent> map)
	{
		int cont=0;
		for (Map.Entry<String,ContainerComponent> entry : map.entrySet()) {
		    String key = entry.getKey();
		    ContainerComponent container = entry.getValue();
		    if(container.getType()!=null)
		    {
			    if(container.getType().equalsIgnoreCase("event"))
			    {
			    	container.setInUse(true);
			    	cont++;
			    }
		    }else{
		    	int p=0;
		    }
		}	
		System.out.println("numero de container events: "+cont);
	}
	
	//obtengo todos los container y los almaceno en un Map
	/*	private static void deleteContainersAndCheckEmptyDirectories(Map<String, ContainerComponent> containerMap,Map<String, ContainerComponent> componenteMap) throws IOException
		{
			int ContNoexiste=0;
			int Contvacio=0;
			int ContExistenteConDatos=0;
			String content;
			File file = new File("containersEliminados.txt");
			 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (Map.Entry<String,ContainerComponent> entry : containerMap.entrySet()) {
			    ContainerComponent container = entry.getValue();
			    File dir2 = new File("C:/CARMICHAEL/site_opt/content/current/private"+container.getPath());
			    if(!dir2.exists())
				{
			    	dir2 = new File("C:/CARMICHAEL/site_opt/content/current/public"+container.getPath());
			    	if(!dir2.exists())
					{
			    		ContNoexiste++;			    		
			    		dir2 = new File("C:/CARMICHAEL/site_opt/content/current/private/containers/"+container.getName());
			    		dir2.delete();
			    		//if(container.isInUse())
			    		//System.out.println("------------dice que esta en use: "+container.getPath());
			    		content = "container path no existente: " + container.getPath() +"\n";	 
						bw.write(content);
					}
				}
				if(dir2.exists() && dir2.isDirectory()&&dir2.list().length==0)
				{
						Contvacio++;
						content = "container vacio: " + container.getPath() +"\n";
						bw.write(content);
						dir2.delete();
						dir2 = new File("C:/CARMICHAEL/site_opt/content/current/private/containers/"+container.getName());
						dir2.delete();
				}else
				{
					
					int p=0;
					if(dir2.isDirectory())
					{
						ContExistenteConDatos++;
						if(dir2.list().length==1 && dir2.list(TrueFileFilter.INSTANCE)[0].contains(".mtl"))
						{
							String filename =dir2.list(TrueFileFilter.INSTANCE)[0];
							if(new File(dir2.getAbsolutePath() + "\\"+ filename).isFile())
							{								
								System.out.println("folder container: "+dir2.getAbsolutePath() + " ....... " + filename);
								Componente.findOnMTLs(filename,componenteMap);
							}
							else{
								p=0;
							}
							
						}
					}
				}
			   
			}
			bw.close();
			System.out.println("numero no existentePath: "+ContNoexiste);
			System.out.println("numero existentes vacios: "+Contvacio);
			System.out.println("numero existentes con datos: "+ContExistenteConDatos);
		}*/
	
	//obtengo todos los container y los almaceno en un Map
	/*private static Map<String, ContainerComponent> getEmptyDirectories()
	{
		Map<String, ContainerComponent> containerMap = new HashMap<String, ContainerComponent>();
		
		File dir = new File("C:/CARMICHAEL/site_opt/content/current/private/components");
		
		List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
				
		for (String fileString : dir.list()) {
			File dir2 = new File("C:/CARMICHAEL/site_opt/content/current/private/components/"+fileString);
			if(dir2.isDirectory())
			{				
				
				List<File> filesComponente = (List<File>) FileUtils.listFiles(dir2, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			
				if(filesComponente==null || filesComponente.isEmpty())
				{
					System.out.println("directorio: "+dir2.getAbsolutePath());
				}
			
			}
			//String fileConatainerName = "/"+dir2.getName().replaceAll(".container", "").replace(".", "/") +"/";
			//containerMap.put(fileConatainerName, new Container(dir2.getName(),fileConatainerName));			
		}
		return containerMap;
	}*/
	
	//obtengo todos los container y los almaceno en un Map
	
	
	
	
	
	
	
	
	//busca si el container esta siendo referenciado en un directorio <container/> y retorna los archivos que lo estan referenciado
	/*private static void findComponentsTemplates(Map<String, ContainerComponent> containerMap, File dir)
	{		
		List<File> filesdirTEMPLATES = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File filedirPTP : filesdirTEMPLATES) {
			containComponentsTemplates(filedirPTP, containerMap, 1);
		}
	}
		
	private static void containComponentsTemplates(File filedir, Map<String, ContainerComponent> componentMap, int findType)
	{
		try
		{
			ContainerComponent container;
			org.jsoup.nodes.Document doc = Jsoup.parse(filedir, "UTF-8", "http://subaru.com/");
			Elements compGetComponents = doc.getElementsByTag("comp:getComponent");
			for (org.jsoup.nodes.Element compGetComponent : compGetComponents) {
				String componentName = compGetComponent.attr("name");
				String componentype = compGetComponent.attr("type");				 
	        	if(!componentMap.containsKey( componentype+"/"+componentName))
	        	{
	        		container = new ContainerComponent(componentype, componentype+"/"+componentName);
	        		container.setInUse(true);
	        		componentMap.put(container.getPath(), container);
	        	}
			}     
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION ERROR:  " + e.getMessage());
		}		
	}*/
	
	
	
	/*private static void setComponentFromQueryFindIncludeMtl(Map<String,ContainerComponent> componentMap )
	{
		ContainerComponent container;
		for(String component: queryFindIncludeMtl)
		{
			container = new ContainerComponent(component, component+"/whatEver.mtl");
    		container.setInUse(true);
    		componentMap.put(container.getPath(), container);
		}
		
		for(String component: queryFindgetContentMtl)
		{
			container = new ContainerComponent(component, component+"/whatEver.mtl");
    		container.setInUse(true);
    		componentMap.put(container.getPath(), container);
		}
	}*/
	
}
