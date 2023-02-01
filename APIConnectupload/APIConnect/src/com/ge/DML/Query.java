package com.ge.DML;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Code_Migration_Task__c;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Query {
	private static final Logger logger = Logger.getLogger("Query");


/**
 * 
 * 
 * @return list
 */
	public LinkedList<Properties> queryRecords() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
		//logger.info(resourceBundle.getString("first_name") + ": Armando");
		LinkedList<Properties> lst = new LinkedList();

		final String USERNAME = resourceBundle.getString("USERNAME");
		// This is only a sample. Hard coding passwords in source files is a bad practice.
		final String PASSWORD = resourceBundle.getString("PASSWORD"); 
		final String URL = resourceBundle.getString("URL"); 
		EnterpriseConnection connection = null;
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(USERNAME);
		config.setPassword(PASSWORD);
		config.setAuthEndpoint(URL);
		if(resourceBundle.getString("PROXY").equalsIgnoreCase("Yes")) {
			config.setProxy(resourceBundle.getString("PROXYHOST"), Integer.valueOf(resourceBundle.getString("PROXYPORT")));
		}
		
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
			//Query the CMT records
			//Accounts - a0GC000000NVjLyMAL
			//Oppty - a0GC000000AMteLMAT
			//MArketing - a0GC000000Fo2sO
			//PRM a0GC000000Q9sQh
			//GRID a0GC0000018FQwAMAW
			//Requirement__r.Acceptance_Criteria_Met__c=true
			String soqlQuery = "SELECT "+resourceBundle.getString("SOQLQueryFields")+" FROM "+resourceBundle.getString("SOQLqueryObject")+" where Migration_Approach__c != 'Manual' AND Sprint__c = '2' AND Requirement__r.Project__c="+resourceBundle.getString("SCRUM")+" AND Requirement_Release__c="+resourceBundle.getString("Release")+"";
			System.out.println("soqlQuery==="+soqlQuery);
			qResult = connection.query(soqlQuery);
			//connection.quer
			boolean done = false;
			if (qResult.getSize() > 0) {
				System.out.println("Logged-in user can see a total of "
						+ qResult.getSize() + " contact records.");
				while (!done) {
					Properties prop;
					SObject[] records = qResult.getRecords();
					for (int i = 0; i < records.length; ++i) {
						Code_Migration_Task__c con = (Code_Migration_Task__c) records[i];
						String fName = con.getName();
						String lName = con.getTask_Type__c();
						String objectname = con.getObject_Name__c();
						//String instruction = con.getMigration_Instructions__c();
						String instruction = con.getComponent_API__c();
						String status = con.getStatus__c();
						System.out.println("API Name   ="+objectname+"ComponentType   ="+lName);
						
						prop = new Properties();
						prop.setProperty("Name", lName);
						prop.setProperty("ComponentType", fName);
						if(objectname!=null)
							prop.setProperty("ObjectAPI", objectname);
						
						prop.setProperty("Status", status);
						if(instruction != null)
							prop.setProperty("instruction", instruction.trim());
						lst.add(prop);
					}
					System.out.println("========="+lst);
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
		return lst;
	}
	
	/**
	 * 
	 * 
	 * @param prop
	 * @return
	 */
	public LinkedList<Properties> morecomponents(Properties prop) {
		LinkedList<Properties> lst = new LinkedList();
		lst.add(prop);
		return lst;
		
		
	}

}
