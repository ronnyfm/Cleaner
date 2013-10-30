package parce.cl.subaru;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import static java.nio.file.StandardCopyOption.*;

public class Componente {

	public static final String TYPE_PTP = "PTP";
	public static final String TYPE_TEMPLATE = "TEMPLATE";
	public static final String TYPE_MTL = "MTL";
	
	public static final String[] queryFindIncludeMtl = {"/0_898/image_hero_with_nav.html.mtl",
												"/0_898/nav_hero_bluetooth.html.mtl",
												"/buildyourown/accessories.html.mtl",
												"/buildyourown/template_vars.html.mtl",
												"/buildyourown/part-request.html.mtl",
												"/buildyourown/part-viewer.html.mtl",
												"/buildyourown/part-options.html.mtl",
												"/buildyourown/colors.html.mtl",
												"/buildyourown/template_vars.html.mtl",
												"/buildyourown/part-request.html.mtl",
												"/buildyourown/part-viewer.html.mtl",
												"/buildyourown/part-options.html.mtl",
												"/buildyourown/index.html.mtl",
												"/buildyourown/part-pre_content_wrapper.html.mtl",
												"/buildyourown/part-loader.html.mtl",
												"/buildyourown/part-moduletitle.html.mtl",
												"/buildyourown/vehicles-cards.html.mtl",
												"/buildyourown/modal_compare.html.mtl",
												"/buildyourown/trim.html.mtl",
												"/buildyourown/tabs.html.mtl",
												"/buildyourown/modal_template.html.mtl",
												"/buildyourown/modal_compare.html.mtl",
												"/buildyourown/modal_changevehicle.html.mtl",
												"/buildyourown/modal_changemodel.html.mtl",
												"/buildyourown/exterior360.html.mtl",
												"/buildyourown/interior360.html.mtl",
												"/buildyourown/modal_compare.html.mtl",
												"/buildyourown/modal_paymentcalculator.html.mtl",
												"/buildyourown/modal_packages.html.mtl",
												"/buildyourown/modal_accessories.html.mtl",
												"/buildyourown/modal_transmissionoptions.html.mtl",
												"/buildyourown/modal_verifychange.html.mtl",
												"/buildyourown/modal_choiceremove.html.mtl",
												"/buildyourown/modal_verifychoice.html.mtl",
												"/buildyourown/modal_verifysimilar.html.mtl",
												"/buildyourown/modal_verifyincluded.html.mtl",
												"/buildyourown/modal_removeincluded.html.mtl",
												"/buildyourown/modal_financingoffer.html.mtl",
												"/buildyourown/modal_changedealer.html.mtl",
												"/buildyourown/modal_savevehicle.html.mtl",
												"/buildyourown/modal_alreadybuilt.html.mtl",	
												"/buildyourown/modal_color_package.html.mtl",
												"/buildyourown/modal_package_color.html.mtl",
												"/buildyourown/modal_color_unavailable.html.mtl",
												"/buildyourown/modal_package_unavailable.html.mtl",
												"/buildyourown/models.html.mtl",
												"/buildyourown/part-pre_content_wrapper.html.mtl",
												"/buildyourown/part-loader.html.mtl",
												"/buildyourown/part-moduletitle.html.mtl",
												"/buildyourown/vehicles-cards.html.mtl",
												"/buildyourown/models-cards.html.mtl",
												"/buildyourown/packages.html.mtl",
												"/buildyourown/template_vars.html.mtl",
												"/buildyourown/part-request.html.mtl",
												"/buildyourown/part-viewer.html.mtl",
												"/buildyourown/part-options.html.mtl",
												"/buildyourown/tabs.html.mtl",
												"/buildyourown/ajax.feature.html-test.mtl", 
												"/compare/competitive.html.mtl",
												"/compare/complete-comparison.html.mtl",
												"/compare/social-share.html.mtl",
												"/compare/share-email.html.mtl",
												"/safety/live-love.html.mtl",
												"/safety/live.html.mtl",
												"/safety/love.html.mtl",
												"/safety/story-submission.html.mtl",
												"/vsp/features-category-title.html.mtl",
												"/pre-order-modal/zip-code.html.mtl",
												"/vsp/social-share.html.mtl",
												"/vsp/share-email.html.mtl",
												"/vsp/veh-specs-content.html.mtl",
												"/vsp/veh-specs-features.html.mtl",
												"/xv_hybrid_preprod/hero.html.mtl",
												"/pre-order-modal/zip-code.html.mtl"};
	
	public static final String[] queryFindcomp_getComponentMtl = {"/0_282_form/owners_mysubaru_custom_login.html.mtl"};
	
	
	public static Map<String, ContainerComponent> getComponents()
	{
		Map<String, ContainerComponent> componentMap = new HashMap<String, ContainerComponent>();
		
		File dir = new File("C:/CARMICHAEL/site_opt/content/current/private/components");
		
		List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
				
		for (File file : files) {	
			if(!file.isDirectory())
			{
				String fileConatainerName = file.getPath();
				componentMap.put(fileConatainerName, new ContainerComponent(file.getParentFile().getPath(),fileConatainerName));	
			}
			else
			{
				int p=0;
			}
		}
		return componentMap;
	}
	
	private static void setComponentFromQueryFindStatic(Map<String,ContainerComponent> componentMap )
	{
		File dir;
		ContainerComponent componente;
		String path;
    	for (String mtl:queryFindIncludeMtl) {
    		dir = new File("C:/CARMICHAEL/site_opt/content/current/private/components"+mtl);
    		path = dir.getPath();
    		componente = componentMap.remove(path);
        	if(componente!=null)
        	{
        		componente.setInUse(true);
        		componentMap.put(componente.getPath(), componente);
        	}else
        	{
        		int p=0;
        		System.out.println("EXCEPTION ERROR MTL:  " + path);
        	}
		}
    	
    	for (String mtl:queryFindcomp_getComponentMtl) {
    		dir = new File("C:/CARMICHAEL/site_opt/content/current/private/components"+mtl);
    		path = dir.getPath();
    		componente = componentMap.remove(path);
    		if(componente!=null)
        	{
        		componente.setInUse(true);
        		componentMap.put(componente.getPath(), componente);
        	}else
        	{
        		int p=0;
        	}
		}	
	}
		
	public static void findComponentes(Map<String, ContainerComponent> componentMap, String type)
	{	
		File dirPTP = null;
		
		switch(type)
		{
			case "PTP": dirPTP = new File("C:/CARMICHAEL/site_opt/content/current/private/ptp");
				break;
			case "TEMPLATE": dirPTP = new File("C:/CARMICHAEL/site_opt/content/current/private/templates");
				break;
			case "MTL": dirPTP = new File("C:/CARMICHAEL/site_opt/content/current/private/components");
			break;
		}
		
		List<File> filesdirPTP = (List<File>) FileUtils.listFiles(dirPTP, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File filedirPTP : filesdirPTP) {
			
			switch(type)
			{
				case "PTP":findComponentesInPTP(filedirPTP, componentMap);
					break;
				case "TEMPLATE":findComponentesInTEMPLATE(filedirPTP, componentMap);
					break;
				case "MTL":findComponentesInMTL(filedirPTP, componentMap);
				break;
			}
			
		}
	}

	private static void findComponentesInPTP(File filedir, Map<String, ContainerComponent> componentMap)
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
	        String expression = "//comp:getComponent"; 
	        Nodes resultadosNamespaceXP = raiz.query(expression,context); 
	        ContainerComponent container;
	        String componentype;
	        String componentName;
	        Element etiquetaNamespaceXP;	        
	        File dir;
	        ContainerComponent componente;
			String path;
	        for(int i=0;i<resultadosNamespaceXP.size();i++)
	        {
	        	etiquetaNamespaceXP = (Element)resultadosNamespaceXP.get(i); 
	        	componentName = etiquetaNamespaceXP.getAttributeValue("name");
				componentype = etiquetaNamespaceXP.getAttributeValue("type");	
				dir = new File("C:/CARMICHAEL/site_opt/content/current/private/components/"+componentype+"/"+componentName);
	    		path = dir.getPath();
	    		componente = componentMap.remove(path);
	    		if(componente!=null)
	        	{
	        		componente.setInUse(true);
	        		componentMap.put(componente.getPath(), componente);
	        	}else
	        	{
	        		int p=0;
	        		System.out.println("EXCEPTION ERROR PTP:  " + path);
	        	}
	    		
	        } 
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION ERROR:  " + e.getMessage());
		}		
	}
			
	private static void findComponentesInTEMPLATE(File filedir, Map<String, ContainerComponent> componentMap)
	{
		try
		{
			org.jsoup.nodes.Document doc = Jsoup.parse(filedir, "UTF-8", "http://subaru.com/");
			Elements compGetComponents = doc.getElementsByTag("comp:getComponent");
	        String componentype;
	        String componentName;	        
	        File dir;
	        ContainerComponent componente;
			String path;
			for (org.jsoup.nodes.Element compGetComponent : compGetComponents) {
				componentName = compGetComponent.attr("name");
				componentype = compGetComponent.attr("type");	
				dir = new File("C:/CARMICHAEL/site_opt/content/current/private/components/"+componentype+"/"+componentName);
	    		path = dir.getPath();
	    		componente = componentMap.remove(path);
	    		if(componente!=null)
	        	{
	        		componente.setInUse(true);
	        		componentMap.put(componente.getPath(), componente);
	        	}else
	        	{
	        		int p=0;
	        		System.out.println("EXCEPTION ERROR TEMPLATE:  " + path);
	        	}
			}     
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION ERROR:  " + e.getMessage());
		}
	}
	
	private static void findComponentesInMTL(File filedir, Map<String, ContainerComponent> componentMap)
	{
		setComponentFromQueryFindStatic(componentMap);
	}
			
	public static void deleteComponentNoInUse(Map<String, ContainerComponent> componentMap) throws IOException
	{
		String content;
		File fileToDelete;
		File file = new File("componenentesEliminados.txt");
		 
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		int cont = 0;
		for (Map.Entry<String,ContainerComponent> entry : componentMap.entrySet()) {
		    ContainerComponent container = entry.getValue();	
    		if(!container.isInUse())
    		{
    			fileToDelete = new File(entry.getKey());
    			generateBackUpFile(fileToDelete);
	    		fileToDelete.delete();
	    		content = entry.getKey() + " \n";	 
				bw.write(content);
				cont++;
    		}
		}
		bw.close();
		System.out.println("Se eliminaron componentes: " +cont);
		
	}
	
	public static void findOnMTL(String textoToFind, String filePath )
	{
		try{
			Scanner scanner = new Scanner(new File(filePath));
			String currentLine;
			int lineNumber = 0;
			while(scanner.hasNext())
			{
				currentLine = scanner.nextLine();
			    if(currentLine.contains(textoToFind))
			    {
			         //Do task
			    	System.out.println("linea: "+lineNumber+" - texto " + textoToFind + " encontrado en:" + filePath);
			    }
			    lineNumber++;
			}
			scanner.close();
		}
		catch(Exception ex)
		{
			System.out.println("rebentooooooooo:" + filePath);
		}
	}
	
	public static void findOnMTLs(String textoToFind, Map<String, ContainerComponent> componentMap )
	{
		for (Map.Entry<String,ContainerComponent> entry : componentMap.entrySet()) {
		    ContainerComponent component = entry.getValue();	
    		if(component.isInUse())
    		{
    			findOnMTL(textoToFind, entry.getKey());				
    		}
		}
	}
	
	public static void generateBackUpFile(File file)
	{
		try {
			File fileBackup = new File(file.getPath().replace("\\site_opt\\", "\\site_opt_BACKUP\\").replace("C:\\CARMICHAEL\\",""));
			// if file doesnt exists, then create it
			if (!fileBackup.exists()) {				
					fileBackup.mkdirs();			
			}
			Files.copy(file.toPath(), fileBackup.toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
