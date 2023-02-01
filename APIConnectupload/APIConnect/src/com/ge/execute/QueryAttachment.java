package com.ge.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import com.ge.api.MetadataLoginUtil;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Attachment;
import com.sforce.soap.enterprise.sobject.Code_Migration_Task__c;
import com.sforce.soap.enterprise.sobject.ContentBody;
import com.sforce.soap.enterprise.sobject.ContentVersion;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.ws.ConnectorConfig;

public class QueryAttachment {
	public static void main(String[] args) {
		try {
			ResourceBundle resourceBundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
		//logger.info(resourceBundle.getString("first_name") + ": Armando");
		LinkedList<Properties> lst = new LinkedList();

		final String USERNAME = resourceBundle.getString("USERNAMEDPRD");
		// This is only a sample. Hard coding passwords in source files is a bad practice.
		final String PASSWORD = resourceBundle.getString("PASSWORPRD"); 
		final String URL = resourceBundle.getString("URLPRD"); 
		EnterpriseConnection connection = null;
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(USERNAME);
		config.setPassword(PASSWORD);
		config.setAuthEndpoint(URL);
		if(resourceBundle.getString("PROXY").equalsIgnoreCase("Yes")) {
			config.setProxy(resourceBundle.getString("PROXYHOST"), Integer.valueOf(resourceBundle.getString("PROXYPORT")));
		}
		connection = new EnterpriseConnection(config);
		System.out.println(config.getServiceEndpoint()+"--"+config.getSessionId());
		QueryResult qResult = null;
		String soqlQuery = "SELECT Id,Name,Body,ParentId,OwnerId,ContentType FROM Attachment where ParentId in ('a3N12000002QofXEAS'," + 
				"'a3N1H000000N9a4UAC',"+
				"'a3N1H000002usILUAY',"+
				"'a3N1H000002uzZYUAY'"+
")";
		System.out.println("soqlQuery==="+soqlQuery);
		qResult = connection.query(soqlQuery);
		System.out.println(qResult);
		boolean done = false;
		if (qResult.getSize() > 0) {
			System.out.println("Logged-in user can see a total of "
					+ qResult.getSize() + " Attachment records");
			while (!done) {
				Properties prop;
				SObject[] records = qResult.getRecords();
				for (int i = 0; i < records.length; ++i) {
					Attachment con = (Attachment) records[i];
					
					int size = con.getBody().length;
					File f1=new File("C:\\Users\\212628638\\Documents\\SMAX\\"+con.getParentId()+'-'+con.getName());
					byte[] buffer= new byte[size];
					buffer = con.getBody();
					FileOutputStream output = new FileOutputStream(f1);
					output.write(buffer);

					
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
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
