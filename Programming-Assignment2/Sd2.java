
/*Name: 0
*CWID: 0
*
*General instructions:
*==>> Open the program in eclipse or other IDE for JAVA
*==>> Start a new project and copy my code in a new class file.
*==>> Run the program, and results should be in the console window
*==>> There should be one table, it is the answer for second query.
*	
*Data Structures:
*==>>HashMap: a data structure used to implement an associative array, a structure that can map keys to values. 
*==>>Chosen reason:Considering the mapping between product and corresponding values,using HashMap can easily find relations.
* 		
*More detailed algorithm are clarified in comments.
*/

import java.sql.*;
import java.util.*;

public class Sd2 {
	public static void main(String[] args){
		String usr ="postgres";
		String pwd ="722722";
		String url ="jdbc:postgresql://localhost:5432/postgres";
		
		Sd2 sd2=new Sd2();                                           //Considering the use of inner class
		
		try{
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}

		catch(Exception e){
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try{
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			
			String cust = null;
			String prod= null;
			int month=0;
			int quant = 0;	
			
			//use nested HashMap to store all necessary information, key is customer
			HashMap<String, HashMap<String,HashMap<Integer, ReportData>>> report2=new HashMap<>();
					
			while (rs.next()){
				cust = rs.getString("cust");    // extract useful information
				prod = rs.getString("prod");  
				month=rs.getInt("month"); 
				quant = rs.getInt("quant");
				
				//if the biggest HashMap is null, create a new one.				
				if(report2==null){                                         
					report2=new HashMap<>();
				}
				
                //key is product				
				HashMap<String,HashMap<Integer, ReportData>> productMap=report2.get(cust); 
				
				//if the middle HashMap is null, create a new one.
				if(productMap==null){                                     
					productMap=new HashMap<>();
				}
				
				//key is month
				HashMap<Integer, ReportData> monthMap=productMap.get(prod);  
				
				//if the smallest HashMap is null, create a new one.
				if(monthMap==null){                                        
					monthMap=new HashMap<>();
				}
			
				SaleData currentData=sd2.new SaleData(cust, prod, quant,month);
				ReportData reportData=monthMap.get(month);
				
				//if monthMap is null, add quantity and count, if not, update it.
				if(!monthMap.containsKey(month)){
					reportData=sd2.new ReportData(quant, 1);	
				}else{
					reportData.setReportQuant(reportData.getReportQuant()+quant);
      				reportData.setReportCount(reportData.getReportCount()+1);							
				}
				
				//put corresponding information to each HashMap step by step
				monthMap.put(month, reportData);
				productMap.put(prod, monthMap);		
				report2.put(cust, productMap);
			}
			
			//print the table for the second query
			System.out.println("");
			System.out.println("Report #2:");
			System.out.println("CUSTOMER PRODUCT  MONTH  BEFORE_AVG  AFTER_AVG");
			System.out.println("======== =======  =====  ==========  ========="); 
			
			//use nested while loop to get every keys and values step by step
			Iterator<String> iterator=report2.keySet().iterator();	
			while(iterator.hasNext()){                                 
				String custKey=iterator.next();
				HashMap<String,HashMap<Integer, ReportData>> custValue=report2.get(custKey);
				
				Iterator<String> prodIterator=custValue.keySet().iterator();
				while(prodIterator.hasNext()){
					String prodKey=prodIterator.next();
					HashMap<Integer, ReportData> prodbValue=custValue.get(prodKey);	
					
					Iterator<Integer> subIterator=prodbValue.keySet().iterator();
					while(subIterator.hasNext()){
						Integer subKey=subIterator.next();
						ReportData subValue=prodbValue.get(subKey);		
						
						//make the table formatted
						String cust0=String.format("%-9s",custKey);
				    	String prod0=String.format("%-9s",prodKey);			    	
				    	String month0=String.format("%5s",subKey);	
				    	
				    	//considering null value condition and make the table formatted 
				    	String BEFORE_AVG=String.format("%12s","<NULL>");		    	
				    	if(subKey!=1){ 		
				    		if(prodbValue.get(subKey-1)!=null){       //for customer and product, get the average sale before each month 
				    			BEFORE_AVG=String.format("%12s",prodbValue.get(subKey-1).reportQuant/prodbValue.get(subKey-1).reportCount);
				    		}
				    	}
				    	
				    	String AFTER_AVG=String.format("%11s","<NULL>");
				    	if(subKey!=12){		
				    		if(prodbValue.get(subKey+1)!=null){       //for customer and product, get the average sale after each month
				    			AFTER_AVG=String.format("%11s",prodbValue.get(subKey+1).reportQuant/prodbValue.get(subKey+1).reportCount);
				    		}
				    	}
				    	
				    	//final print
				    	System.out.println(cust0 + prod0 + month0 + BEFORE_AVG + AFTER_AVG);
			        }
				}
			}	    	    
		}

		catch(SQLException e){
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}	
	
	//use inner class to encapsulate SaleData and Setters, Getters and constructor functions
	private class SaleData {	   
		String cust;
		String prod;
		int quant;
		int month;
		
		public SaleData(int month) {
			super();
			this.month = month;
		}

		public SaleData(String cust, String prod,int quant,int month) {
			super();
			this.cust = cust;
			this.prod = prod;
			this.quant = quant;	
			this.month=month;
		}

		public String getCust() {
			return cust;
		}

		public String getProd() {
			return prod;
		}

		public int getQuant() {
			return quant;
		}

		public int getMonth() {
			return month;
		}

		public void setCust(String cust) {
			this.cust = cust;
		}

		public void setProd(String prod) {
			this.prod = prod;
		}

		public void setQuant(int quant) {
			this.quant = quant;
		}

		public void setMonth(int month) {
			this.month = month;
		}			
	}
	
	//use inner class to encapsulate ReportData and Setters, Getters and constructor functions
	private class ReportData {
		int reportQuant;
		int reportCount;
		
		public int getReportQuant() {
			return reportQuant;
		}
		public void setReportQuant(int reportQuant) {
			this.reportQuant = reportQuant;
		}
		public int getReportCount() {
			return reportCount;
		}
		public void setReportCount(int reportCount) {
			this.reportCount = reportCount;
		}
		public ReportData( int reportQuant, int reportCount) {
			super();
			this.reportQuant = reportQuant;
			this.reportCount = reportCount;
		}				
	}	
}

