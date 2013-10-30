package parce.cl.subaru;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class ContainerCheck {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File dir = new File("C:/CARMICHAEL/site_opt/content/current/private/containers");
		
		System.out.println("starting at:"+ (new Date()).toString());
		System.out.println("Getting all files in " + dir.getCanonicalPath() + " including those in subdirectories");
		List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		int containerNumber = 0;
		for (File file : files) {
			//System.out.println("*** container: " + file.getName().replaceAll(".container", ""));
			
			String fileConatainerName = file.getName().replaceAll(".container", "").replace(".", "/");
			//String fileConatainerName = "/disclaimer/global/";
			try
			{
				//verifico si el container esta siendo usado en algu ptp
				ArrayList<String> ptpList = findContainersInPTP(fileConatainerName);				
				
				if(ptpList.size()==0)
				{
					/*ptpList = findContainersInTEMPLATE(fileConatainerName);	
					
					if(ptpList.size()==0)
					{
						File dirPublic = new File("C:/CARMICHAEL/site_opt/content/current/public/"+fileConatainerName);
						File dirPrivate = new File("C:/CARMICHAEL/site_opt/content/current/private/components/"+fileConatainerName);
						
						//busco en la parte publica si existe la direccion
						if(dirPublic.isDirectory())
						{
							List<File> filesdirPublic = (List<File>) FileUtils.listFiles(dirPublic, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
							for (File filedirPublic : filesdirPublic) {
								System.out.println("*** archivos: " + filedirPublic.getName());
							}
						}
						
						//busco en la parte privada si existe la direccion
						if(dirPrivate.isDirectory())
						{
							List<File> filesdirPrivate = (List<File>) FileUtils.listFiles(dirPrivate, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
							for (File filedirPrivate : filesdirPrivate) {
								System.out.println("*** archivos: " + filedirPrivate.getName());
							}
						}
					}*/
				}
				else
				{					
					System.out.println("*** CONTAINER EN USO: " + fileConatainerName);
				}
				
			}
			catch(Exception e)
			{
				System.out.println("EXCEPTION ERROR:  " + e.getMessage());
			}
			
			/*if(cont>0)
			{
				break;
			}
			
			cont++;*/
			containerNumber++;
		}
		System.out.println("*** END container check total: " + containerNumber);
		System.out.println("ending at:"+ (new Date()).toString());
	}
	
	//busca si el container esta siendo referenciado en un directorio <container/> y retorna los archivos que lo estan referenciado
	private static ArrayList<String> findContainers(String fileConatainerName, File dir)
	{
		ArrayList<String> ptpList =  new ArrayList<String>();
		
		List<File> filesdirPTP = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File filedirPTP : filesdirPTP) {
			//parseo en XML el ptp para buscar x elemento comp:getComponent - container
			if(containContainer(filedirPTP, fileConatainerName))
			{				
				ptpList.add(filedirPTP.getAbsolutePath());
			}
		}
		return ptpList;
	}
	
	//busca si el container esta siendo referenciado en un ptp <container/> y retorna los ptp que estan ciendo referenciados
	private static ArrayList<String> findContainersInPTP(String fileConatainerName)
	{
		File dirPTP = new File("C:/CARMICHAEL/site_opt/content/current/private/ptp");
		return findContainers(fileConatainerName, dirPTP);
	}
	
	//busca si el container esta siendo referenciado en un template <container/> y retorna los template que estan ciendo referenciados
	private static ArrayList<String> findContainersInTEMPLATE(String fileConatainerName)
	{
		File dirTEMPLATE = new File("C:/CARMICHAEL/site_opt/content/current/private/templates");
		return findContainers(fileConatainerName, dirTEMPLATE);
	}
	
	private static boolean containContainer(File filedir, String fileConatainerName)
	{
		boolean hasContainer = false;
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
	        String expression = "//container[@name='"+fileConatainerName+"/']";
	        //String expression = "//comp:getComponent";
	        //String expression = "/prototype/box/comp:getComponent/container[@name='"+fileConatainerName+"/']";
	        // Hacemos la búsqueda pasando el XPathContext  
	        Nodes resultadosNamespaceXP = raiz.query(expression,context);  
	        for(int i=0;i<resultadosNamespaceXP.size();i++)
	        {
	        	Element etiquetaNamespaceXP = (Element)resultadosNamespaceXP.get(i); 
	        	if(etiquetaNamespaceXP.getAttributeValue("name").equals(fileConatainerName + "/"))
	        	{
	        		System.out.println("-- EXITO EN CONTRO CONTAINER ("+fileConatainerName+") : " + filedir.getAbsolutePath());
	        	}  
	        }        
            
		}
		catch(Exception e)
		{
			System.out.println("EXCEPTION ERROR:  " + e.getMessage());
		}
		return hasContainer;
		
	}

}
