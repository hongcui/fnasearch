package org.fna.fnasearch.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.fna.fnasearch.rdf.JenaRDFStoreHandler;

public class XMLImporter {
	private static XMLImporter instance = null;
	// used to balk other request to ensure thread-safety
	private boolean busy = false;
	// other needed member variable necessary for importing
	private String src = null; // source folder name
	private JenaRDFStoreHandler store = null; // destination store
	private String arc = null; // archive folder to put imported files
	private Logger logger = Logger.getLogger(this.getClass().getName()); // log file
	private boolean formatStore = false; //format the store before import
	
	//private constructor to disable instantiation
	private XMLImporter() {
		//disable instantiation		
	}
	
	//public getInstance method to retrieve existing instance or construct one
	//return the resource
	public synchronized static XMLImporter getInstance() {
		if (instance == null) {
			instance = new XMLImporter();
		}
		return instance;
	}

	//destroy the importer by releasing its reference
	private static void destroy() {
		instance = null;
	}
	
	
	//constructor, import from given folder into provided store, format store by specification
	private void construct(JenaRDFStoreHandler store,boolean formatStore, String src, String arc){
		this.store = store;
		this.formatStore = formatStore;
		this.src = src;
		this.arc = arc;
		this.logger.debug("using source folder "+this.src+";connected to store "+ (this.store!=null?this.store.toString():"null")+"; format store? "+this.formatStore+"; Target folder is "+this.arc);
	}
	
	//move the file from import folder to the new folder
	//retrun true on success and false on failure
	//throws ImporterException
	private boolean moveFile(File fromFile, File toFile, boolean deleteOld) throws IOException{
		boolean success = false;
		if(!fromFile.exists())throw new IOException("No such source file: "+ fromFile.getName());
		if(!fromFile.isFile())throw new IOException("Cannot copy directory: "+ fromFile.getName());
		if(!fromFile.canRead())throw new IOException("Source file is unreadable: "+ fromFile.getName());
		if(!toFile.canWrite())throw new IOException("Target file is unwritable: "+ toFile.getName());
		
		FileInputStream from = new FileInputStream(fromFile);
	    FileOutputStream to = new FileOutputStream(toFile);
	    try{
	    	byte[] buffer = new byte[4096];
	    	int bytesRead;
	    	this.logger.debug("writing file "+toFile.getAbsolutePath());
	    	while ((bytesRead = from.read(buffer)) != -1)to.write(buffer, 0, bytesRead); // write
	    	if(deleteOld)fromFile.delete();
	    	success = true;
	    }catch(Throwable t){
	    	throw new IOException("file closing error",t);
	    }finally {
	    	if(from!= null||to!=null)
	    		try {
	    			from.close();
	    			to.close();
	    		}catch(Throwable t){
	    			throw new IOException("file closing error",t);
	    		}
	    }
		return success;
	}
	
	//open file or folder, recursively
	private void openFiles(String path, XMLReader reader) throws ImporterException{
		//file name pattern
		Pattern file_extension = Pattern.compile("(?<=\\.)xml$");
		
		//Begin of import xml files in a folder
		File dir = new File(path);
		File[] files = dir.listFiles(); 
		if(files == null){
			this.logger.error("source path is not a folder "+dir.getAbsolutePath());
		}else if(files.length==0){
			this.logger.error("no file found "+dir.getAbsolutePath());
		}else{   	
			//iterate through files
			for(int i=0;i<files.length;i++) { 
				//if directory, recurse
				if(files[i].isDirectory()){
					this.openFiles(files[i].getAbsolutePath(),reader);
				}
				//if file, match the extension and import
				else if(files[i].isFile()){
					String filename = files[i].getName();
					Matcher file_matcher = file_extension.matcher(filename);
					if(file_matcher.find()){
						String fullpath = files[i].getAbsolutePath();
						this.logger.debug("found file "+fullpath);
						reader.getDoc(fullpath);
						this.logger.debug("importing file: "+reader.treatmentid);
						try{
							//import the file
							reader.readDoc();
							//move the imported file into archive folder
							File archive = new File(this.arc+"/"+reader.fnasrc+"/"+reader.fnavol);
							if(!(archive.exists()&&archive.isDirectory())){
								this.logger.debug("archive folder: "+archive.getAbsolutePath()+"; Exists? "+archive.exists());
								//creates the folder
								if(!archive.mkdirs()){
									this.logger.error("Folder making failed: "+archive.getAbsolutePath());
								}
							}
							//move the file now
							try{
								if(archive.isDirectory()){
									File archivedoc = new File(archive,reader.treatmentid+".xml");
									if(!archivedoc.exists()){
										archivedoc.createNewFile();
									}
									this.moveFile(files[i], archivedoc,true);
								}else{
									this.logger.error("File not moved; Is not a folder: "+archive.getAbsolutePath());
								}
							}catch(Throwable t){
								this.logger.error("Move file failed: from "+files[i].getAbsolutePath()+" to "+archive.getAbsolutePath());
								throw new ImporterException("Move file failed: "+files[i].getName(),t);
							}
						}catch(XMLReaderException e){
							this.logger.error(e.getMessage());
							throw new ImporterException("file importing error: "+files[i].getName(),e);
						}catch(Throwable t){
							this.logger.error("file importing failed: "+files[i].getAbsolutePath());
							throw new ImporterException("file importing error: "+files[i].getName(),t);
						}finally{
							file_matcher.reset();
						}
					}
				}else{
					throw new ImporterException("Invlid directory "+dir.getAbsolutePath());
				}
			} 
		}
	}
	
	//do the importing
	public void doImport(JenaRDFStoreHandler store,boolean formatStore,String src, String arc) throws ImporterException {
		//check if the instance of the importer is busy
		synchronized (this) {
			if (this.busy) {
				this.logger.debug("importer is busy, no go");
				return;
			}else{
				this.busy = true;
				this.logger.debug("importer is free, is set to busy now");
			}
		}
		//config the importer
		this.construct(store,formatStore,src,arc);
		//start importing, if exception thrown, reset the busy flag
		try{
			//format store if needed
			if(this.formatStore){
				if(this.store!=null){
					this.logger.debug("Formatting store...");
					this.store.formatStore();
				}
			}
			//visitor instantiation
			XMLReader visitor;
			if(this.store==null){
				visitor = new XMLReader();
			}else{
				visitor = new XMLReader(this.store);
			}
			
			// import here
			this.openFiles(this.src, visitor);
			//when things are done, reset the busy flag
			this.busy = false;
			this.logger.debug("doImport is finished, busy flag set to free");
		}catch (Throwable t){
			//reset the busy flag
			this.busy = false;
			this.logger.debug("exception encounted, busy flag set to free");
			//re-throw the exception
			throw new ImporterException("Importing error", t);
		}finally{
			//kill the store
			if(this.store!=null){
				this.store.closeConn();
				this.store.closeQuery();
				this.store.closeStore();
			}
			this.logger.debug("importer closed the store");
			XMLImporter.destroy();
		}
	}

}