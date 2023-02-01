package com.ge.DML;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.ge.api.MetadataLoginUtil;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Contact;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Insert {
	
	private static final Logger logger = Logger.getLogger("Insert");

	
	
	public void queryRecords() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
		logger.info(resourceBundle.getString("first_name") + ": Armando");


		 final String USERNAME = resourceBundle.getString("USERNAME");
	     // This is only a sample. Hard coding passwords in source files is a bad practice.
	     final String PASSWORD = resourceBundle.getString("PASSWORD"); 
	     final String URL = resourceBundle.getString("URL"); 
		EnterpriseConnection connection = null;
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(USERNAME);
		config.setPassword(PASSWORD);
		config.setAuthEndpoint(URL);
		config.setProxy(resourceBundle.getString("PROXYHOST"), Integer.valueOf(resourceBundle.getString("PROXYPORT")));
		// Set the username and password if your proxy must be authenticated
		//config.setProxyUsername(proxyUsername);
		//config.setProxyPassword(proxyPassword);
		try {
		    connection = new EnterpriseConnection(config);
		   // etc.
		} catch (ConnectionException ce) {
		  ce.printStackTrace();
		}
		   QueryResult qResult = null;
		   try {
			   String obj = resourceBundle.getString("SOQLqueryObject");
			  
			   
		      String soqlQuery = "SELECT "+resourceBundle.getString("SOQLQueryFields")+" FROM "+resourceBundle.getString("SOQLqueryObject")+" LIMIT 10";
		      qResult = connection.query(soqlQuery);
		      boolean done = false;
		      if (qResult.getSize() > 0) {
		         System.out.println("Logged-in user can see a total of "
		            + qResult.getSize() + " contact records.");
		         while (!done) {
		        	 Properties prop;
		            SObject[] records = qResult.getRecords();
		            for (int i = 0; i < records.length; ++i) {
		               prop = new Properties();
		 			 
		               Contact con = (Contact) records[i];
		               String fName = con.getFirstName();
		               String lName = con.getLastName();
		               prop.setProperty("CMT Name", lName);
		               prop.setProperty("ComponentType", fName);
		               if (fName == null) {
		                  System.out.println("CMT Name " + (i + 1) + ": " + lName);
		               } else {
		                  System.out.println("Component Type " + (i + 1) + ": " + fName
		                        + " " + lName);
		               }
		              
		            }
		            if (qResult.isDone()) {
		               done = true;
		            } else {
		               qResult = connection.queryMore(qResult.getQueryLocator());
		            }
		         }
		      } else {
		         System.out.println("No records found.");
		      }
		      System.out.println("\nQuery succesfully executed.");
		   } catch (ConnectionException ce) {
		      ce.printStackTrace();
		   }
	
		}

}
