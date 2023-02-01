package com.ge.execute;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.ge.api.MetadataLoginUtil;
import com.sforce.soap.metadata.CustomField;
import com.sforce.soap.metadata.CustomObject;
import com.sforce.soap.metadata.FileProperties;
import com.sforce.soap.metadata.ListMetadataQuery;
import com.sforce.soap.metadata.LookupFilter;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.ReadResult;
import com.sforce.soap.metadata.ValidationRule;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Execute {
	
	public static void main(String[] args) {
		try {
			
			List<String> headerRow = new ArrayList<String>();
		    headerRow.add("Name");
		    headerRow.add("Type");
		    headerRow.add("length");
		    headerRow.add("Label");
		  
		    List<String> firstRow = new ArrayList<String>();
		    firstRow.add("1111");
		    firstRow.add("Gautam");
		    firstRow.add("India");
		    List<List<String>> recordToAdd = new ArrayList<List<String>>();
		    recordToAdd.add(headerRow);
		    //recordToAdd.add(firstRow);
			
		/**	String host = "3.234.164.81";
	    String port = "80";
	    System.out.println("Using proxy: " + host + ":" + port);
	    System.setProperty("http.proxyHost", host);
	    System.setProperty("http.proxyPort", port); **/
	   // System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
		
		MetadataConnection con = MetadataLoginUtil.login();
		ConnectorConfig config = con.getConfig();
		System.out.println(config.getServiceEndpoint()+"--"+config.getSessionId());
		config.setProxy("3.234.164.81", 80);
	//	List<String> colist = listCustomObjects(con);
		List<String> colistrefined = new ArrayList<>();
			
		/** for(int i=0; i<10; i++) {
			colistrefined.add(colist.get(i));
		} **/
		colistrefined.add("Opportunity");
		String[] cobjects = new String[]{"GE_ES_Request_Type__c"};
		List<List<String>> objdetails = readCustomObjectSync(con,colistrefined);
		//List<List<String>> objdetails =  listCustomObjects(con);
		for(int k=0;k<objdetails.size(); k++) {
			List<String> lst = objdetails.get(k);
		//	System.out.println("-----"+lst);
			recordToAdd.add(lst);
			//vallist1.remove(k);
			}
		
		try {
			XLSCreate cls = new XLSCreate(recordToAdd);
		    cls.createExcelFile();
		    System.out.println(recordToAdd.size());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
		//getvalrules(con);
		//System.out.println(valrules);
		
	} catch (ConnectionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public static List<List<String>> listCustomObjects(MetadataConnection con) {
	List<String> colist = new ArrayList<>();
	List<List<String>> returnlist = new ArrayList<List<String>>();
	List<String> firstRow = null;
	List<String> objrow = null;
	try {
	    ListMetadataQuery query = new ListMetadataQuery();
	  query.setType("CustomObject");
	    // query.setType("StandardObject");
	    //query.setFolder(null);
	    double asOfVersion = 40.0;
	    // Assuming that the SOAP binding has already been established.
	    FileProperties[] lmr = con.listMetadata(
	        new ListMetadataQuery[] {query}, asOfVersion);
	    if (lmr != null) {
	    	int count = 0;
	      for (FileProperties n : lmr) {
	    	  colist.add(n.getFullName()); 
	    	  count++;
	    	   
	        System.out.println(count+"  Component fullName: " + n.getFullName());
	       // firstRow.add(n.getType());
	        System.out.println("Component type: " + n.getCreatedDate());
	        firstRow = new ArrayList<String>();
	        firstRow.add(n.getFullName());
	        Date currentDate = n.getCreatedDate().getTime();
	        SimpleDateFormat formatter=new SimpleDateFormat("DD-MMM-yyyy");  
	        String currentDate1=formatter.format(currentDate);  
	        firstRow.add(currentDate1);
	        returnlist.add(firstRow);
	      }
	    }            
	  } catch (ConnectionException ce) {
	    ce.printStackTrace();
	  }
	return returnlist;
	}

public static List listValidationRules(MetadataConnection con) {
	  
	List<String> validationruless = new LinkedList<String>();
	try {
		  
	    ListMetadataQuery query = new ListMetadataQuery();
	    query.setType("ValidationRule");
	    //query.setFolder(null);
	    double asOfVersion = 40.0;
	    // Assuming that the SOAP binding has already been established.
	    FileProperties[] lmr = con.listMetadata(
	        new ListMetadataQuery[] {query}, asOfVersion);
	    if (lmr != null) {
	    	int count = 0;
	      for (FileProperties n : lmr) {
	    	   count++;
	        System.out.println(count+"  Component fullName: " + n.getFullName());
	        System.out.println("Component type: " + n.getType());
	        validationruless.add(n.getFullName());
	      }
	      System.out.println(validationruless);
	    }            
	  } catch (ConnectionException ce) {
	    ce.printStackTrace();
	  }
	return validationruless;
	}

public static List<List<String>> readCustomObjectSync(MetadataConnection con, List cobjects) {
	List<List<String>> returnlist = new ArrayList<List<String>>();
	List<String> firstRow = null;
	List<String> objrow = null;
	String[] cobjectasarray = new String[cobjects.size()];
	for(int i=0; i<cobjects.size();i++) {
		cobjectasarray[i] = (String) cobjects.get(i);
		}
	try {
    	
        ReadResult readResult = con
                .readMetadata("CustomObject", cobjectasarray);
        Metadata[] mdInfo = readResult.getRecords();
        System.out.println("Number of component info returned: "
                + mdInfo.length);
        for (Metadata md : mdInfo) {
            if (md != null) {
                CustomObject obj = (CustomObject) md;
                System.out.println("Custom object full name: "
                        + obj.getFullName());
                System.out.println("Label: " + obj.getLabel());
                System.out.println("Number of custom fields: "
                        + obj.getFields().length);
                System.out.println("Sharing model: "
                        + obj.getSharingModel());
                CustomField[] flds = obj.getFields();
                objrow = new ArrayList<String>();
                objrow.add(obj.getFullName());
                returnlist.add(objrow);
                for(CustomField fl : flds) {
                	String fulname = fl.getFullName();
                	int len = fl.getLength();
                	
                	
                LookupFilter lukupfilter = fl.getLookupFilter();
                String lbl = fl.getLabel();
                String type = null;
                if(fl.getType() != null) {
                type = fl.getType().toString();
                }
                System.err.println(fulname+"--"+type+"--"+len+"--"+lbl);
                firstRow = new ArrayList<String>();
    		    firstRow.add(fulname);
    		    firstRow.add(type);
    		    firstRow.add(String.valueOf(len));
    		    firstRow.add(lbl);
    		    returnlist.add(firstRow);
                }
                int ln = obj.getFields().length;
                
               
    		  //  firstRow.add(obj.getFields().length);
    		  // firstRow.add(obj.getSharingModel());
    		  //  firstRow.add(obj.getErrorMessage());
    		    
                
            } else {
                System.out.println("Empty metadata.");
            }
        }
    } catch (ConnectionException ce) {
        ce.printStackTrace();
    }
    return returnlist;
}

public static List<List<String>> readValidationRule(MetadataConnection con, String[] vRuleName) {
	List<List<String>> returnlist = new ArrayList<List<String>>();
	List<String> firstRow = null;
	try {
    	
    	 
        ReadResult readResult = con
                .readMetadata("ValidationRule", vRuleName);
        Metadata[] mdInfo = readResult.getRecords();
        System.out.println("Number of component info returned: "
                + mdInfo.length);
        for (Metadata md : mdInfo) {
            if (md != null) {
            	ValidationRule obj = (ValidationRule) md;
          System.out.println("Val Rule Name: "
                        + obj.getFullName());
                System.out.println("Description: " + obj.getDescription());
                System.out.println("Formula: "
                        + obj.getErrorConditionFormula());
                System.out.println("Dispaly Field: "
                        + obj.getErrorDisplayField());
                System.out.println("Error Message: "
                        + obj.getErrorMessage());
            	firstRow = new ArrayList<String>();
    		    firstRow.add(obj.getFullName());
    		    firstRow.add(obj.getDescription());
    		    firstRow.add(obj.getErrorConditionFormula());
    		    firstRow.add(obj.getErrorDisplayField());
    		    firstRow.add(obj.getErrorMessage());
    		    returnlist.add(firstRow);
            } else {
                System.out.println("Empty metadata.");
	            }
           
	        }
       
	    } catch (ConnectionException ce) {
	        ce.printStackTrace();
	    }
	return returnlist;
	}

public static void getvalrules(MetadataConnection con) {
	List<String> headerRow = new ArrayList<String>();
    headerRow.add("Val Rule Name");
    headerRow.add("Description");
    headerRow.add("Formula");
    headerRow.add("Dispaly Field");
    headerRow.add("Error Message");
    List<String> firstRow = new ArrayList<String>();
    firstRow.add("1111");
    firstRow.add("Gautam");
    firstRow.add("India");
    List<List<String>> recordToAdd = new ArrayList<List<String>>();
    recordToAdd.add(headerRow);
	List<String> vallist =  new LinkedList<String>() {
	};
	vallist = listValidationRules(con);
	System.out.println(vallist);
	int run = vallist.size()/10;
//	int run =  vallist.size() mod 10;
	String[] valrules = new String[10];
	int count = 0;
	System.out.println(valrules.length);
	LinkedList<String> OppValRules = new LinkedList<String>();
	LinkedList<String> AcctValRules = new LinkedList<String>();
	for(String str : vallist) {
		if(str.startsWith("GE_ES_Request_Type__c")) {
				OppValRules.add(str);
			} else if(str.startsWith("Account")) {
				AcctValRules.add(str);
			}
	
		//count++;
	}
	
	if(OppValRules.size()>0) {
		int numberofopptyrules = OppValRules.size();
		System.out.println("numberofopptyrules= "+numberofopptyrules);
		int looprun = numberofopptyrules/10;
		
		int decimal = numberofopptyrules % 10;
		LinkedList<String> toberemoved = new LinkedList<String>();
		if(decimal>0) {
			looprun = looprun + 1;
		}
		System.out.println("looprun = "+looprun);
		for(int cnt=1; cnt <= looprun; cnt++) {
			
				if(OppValRules.size()>9) {
					for(int lp=0; lp< 10; lp++) {
						valrules[lp] = OppValRules.get(lp);
						toberemoved.add(OppValRules.get(lp));
					}
					
				}else {
					
					for(int i=0;i<valrules.length;i++)
					{
						valrules[i] = null;
					}
					
					for(int lp=0; lp<OppValRules.size() ; lp++) {
						valrules[lp] = OppValRules.get(lp);
						toberemoved.add(OppValRules.get(lp));
					}
				}
				System.out.println("opp size = "+OppValRules.size());
			OppValRules.removeAll(toberemoved);
			List<List<String>> vallist1 = readValidationRule(con,valrules);
			for(int k=0;k<vallist1.size(); k++) {
				List<String> lst = vallist1.get(k);
			//	System.out.println("-----"+lst);
				recordToAdd.add(lst);
				//vallist1.remove(k);
				}
		}
		try {
				XLSCreate cls = new XLSCreate(recordToAdd);
			    cls.createExcelFile();
			    System.out.println(recordToAdd.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
	
}
