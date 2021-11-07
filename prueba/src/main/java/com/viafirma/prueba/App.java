package com.viafirma.prueba;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

/**
 * Prueba Viafirma!
 *
 */
public class App {
	
    public static void main( String[] args ) {
    	readDirectory();
    }
    
    /**
     * readDirectory
     * Metodo que recibe como parametro la ruta de un directorio 
     * desde la variable de entorno VIAFIRMA_PATH y lo recorre
     */
    private static void readDirectory() {
        try {
        	//Se recoge la ruta del directorio asignado en la variable de entorno VIAFIRMA_PATH
            File f = new File(System.getenv().get("VIAFIRMA_PATH"));
            
            //Filtrado por extension de archivo pdf
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.toLowerCase().endsWith(".pdf");
                }
            };

            File[] files = f.listFiles(filter);
            
            //Conversion de array a lista
            List<File> listFiles = Arrays.asList(files);
            
            //Se recorre el listado y por cada uno se llama al metodo addPageToPDF pasandole como parametro el objeto File
            listFiles.forEach(e -> addPageToPDF(e));            
        } 
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
        	System.out.println("Fin");
    	}
    }
    
    /**
     * addPageToPDF
     * Metodo que por cada archivo pdf encontrado 
     * y si no tiene insertada una firma
     * le crea una pagina en blanco
     */
    private static void addPageToPDF(File file) {
    	PDDocument pdDocument = null;
    	try {
    		//carga del documento pdf
    	    pdDocument = PDDocument.load(new File(file.getPath()));
    	    
    	    //si no tiene firma, se le añade una página en blanco al final del documento
  	      	if (pdDocument.getSignatureDictionaries().isEmpty()) {
  	      		System.out.println("Añadiendo página en blanco a ".concat(file.getName().concat("...")));
	  	    	PDPage newPage = new PDPage();
	    	    pdDocument.addPage(newPage);
	    	    System.out.println("Página añadida");
	    	    //Llamada al metodo addSignatureToPDF
	    	    addSignatureToPDF(pdDocument);
	    	    pdDocument.save(file.getPath());
  	      	}    	       	    
    	} 
    	catch (IOException e) {
    	    e.printStackTrace();
    	} 
    	finally {
    	    if (pdDocument != null) {
    	        try {
    	            pdDocument.close();
    	        } 
    	        catch (IOException e) {
    	            e.printStackTrace();
    	        }
    	    }
    	}
	}
    
    /**
     * addSignatureToPDF
     * Metodo que recibe el archivo pdf y le inserta una firma 
     */
    private static void addSignatureToPDF(PDDocument pdDocument) throws IOException {
    	System.out.println("Añadiendo firma...");
    	//Creación de la firma
    	PDSignature pdSignature = new PDSignature();
		pdSignature.setFilter(PDSignature.FILTER_VERISIGN_PPKVS);
		pdSignature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_SHA1);
		pdSignature.setName("KSCodes");
		pdSignature.setLocation("WFH");
		pdSignature.setReason("Sample Signature test");
		pdSignature.setSignDate(Calendar.getInstance());
		//Se le añade la firma al archivo
		pdDocument.addSignature(pdSignature);
		System.out.println("Firma añadida");
    }
    
}
