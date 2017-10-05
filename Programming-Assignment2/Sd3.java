

/*Name:0
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

public class Sd3 {
	public static void main(String[] args){
		String usr ="postgres";
		String pwd ="722722";
		String url ="jdbc:postgresql://localhost:5432/postgres";
		
		String product;        //initialize each variables
		int quantity=0;;
		int theMonth;
		
		//use nested HashMap to store all necessary information, key is product
		HashMap<String, HashMap<Integer, ReportData>> report3=new HashMap<>();  
		
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
								
			while (rs.next()){                     //extract useful information
				product = rs.getString("prod");  	    
				quantity = rs.getInt("quant");
				theMonth=rs.getInt("month");
				
				//key is month
				HashMap<Integer, ReportData> monthMap=report3.get(product);    //used product as key for first hashmap,month as key of second hashmap

				//if the inner HashMap is null, create a new one.
				if(monthMap==null){
					monthMap=new HashMap<>();
				}
				
				ReportData reportData=monthMap.get(theMonth);
				
				//if monthMap is null, add information, if not, update it.
				//for each month, store maximum quantity, total quantity and total count of products
				if(!monthMap.containsKey(theMonth)){
					reportData=new ReportData(quantity, quantity, 1);  //initial data
				}else{
					//set max quantity
					reportData.setMaxQuant(Math.max(reportData.getMaxQuant(), quantity));
					reportData.setTotalQuant(reportData.getTotalQuant()+quantity);
					reportData.setTotalCount(reportData.getTotalCount()+1);
				}
				
				//put corresponding information to each HashMap step by step
				monthMap.put(theMonth,reportData);
				report3.put(product, monthMap);											
			}
			
			//second scan
			rs = stmt.executeQuery("SELECT * FROM Sales");
			
			while(rs.next()){
				product = rs.getString("prod");  	    
				quantity = rs.getInt("quant");
				theMonth=rs.getInt("month");
				
				//key is month
				HashMap<Integer, ReportData> monthMap=report3.get(product); 
				
				//previous month
				//for the following month, try to find a matching value. And then update before values
				if(monthMap.containsKey(theMonth+1)){
					//get month's average and maximum sale
					ReportData currentMonthData= monthMap.get(theMonth+1);
				    int currentAvg=currentMonthData.getTotalQuant()/currentMonthData.getTotalCount();
			    	int currentMax = currentMonthData.getMaxQuant(); 
					
					ReportData lastMonthReport= monthMap.get(theMonth+1);
					if(quantity>=currentAvg && quantity<=currentMax){
						lastMonthReport.setBeforeCount(lastMonthReport.getBeforeCount()+1);
					}
					monthMap.put(theMonth+1, lastMonthReport);
				}
				
				//next month
				//for the previous month, try to find a matching value. And then update after values
				if(monthMap.containsKey(theMonth-1)){
					ReportData currentMonthData= monthMap.get(theMonth-1);
				    int currentAvg=currentMonthData.getTotalQuant()/currentMonthData.getTotalCount();
			    	int currentMax = currentMonthData.getMaxQuant(); 
					
					ReportData nextMonthReport= monthMap.get(theMonth-1);
					if(quantity>=currentAvg && quantity<=currentMax){
						nextMonthReport.setAfterCount(nextMonthReport.getAfterCount()+1);
					}
					monthMap.put(theMonth-1, nextMonthReport);
				}
				report3.put(product, monthMap);
				
			} 
			
			//print the table for the third query
			System.out.println("");
			System.out.println("Report #3:");
			System.out.println("PRODUCT MONTH  BEFORE_TOT  AFTER_TOT");
			System.out.println("======= =====  ==========  ========="); 
             
			Iterator<String> iterator=report3.keySet().iterator();		
			while(iterator.hasNext()){
				String prodKey=iterator.next();
				HashMap<Integer, ReportData> prodValue=report3.get(prodKey);
				
				Iterator<Integer> subIterator=prodValue.keySet().iterator();
				while(subIterator.hasNext()){
					Integer monthKey=subIterator.next();
				    ReportData monthValue=prodValue.get(monthKey);
				    
			    	//considering null value condition and make the table formatted 
			    	String prod0=String.format("%-9s",prodKey);
			    	String month0=String.format("%4s",monthKey);
			    	
				    //for previous month, get those counts of sale had quantities between that month's average and maximum sale
			    	String beforeTot=String.format("%12s","<NULL>");
			    	if(prodValue.get(monthKey-1)!=null){
			    		beforeTot=String.format("%12s",monthValue.getBeforeCount());
			    	}
			    	
			    	//for following month, get those counts of sale had quantities between that month's average and maximum sale
			    	String afterTot=String.format("%11s","<NULL>");
			    	if(prodValue.get(monthKey+1)!=null){		
			    		afterTot=String.format("%11s",monthValue.getAfterCount());  		
			    	}
			    	
			    	//final print
			    	System.out.println(prod0 + month0 + beforeTot + afterTot);
				}
			}  	    	    
		}

		catch(SQLException e){
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}

	
	//use inner class to encapsulate ReportData and Setters, Getters and constructor functions
	public static class ReportData{
		int maxQuant;
		int totalQuant;
		int totalCount;	
		int beforeCount;
		int afterCount;
		
		public ReportData(int maxQuant, int totalQuant, int totalCount) {
			super();
			this.maxQuant = maxQuant;
			this.totalQuant = totalQuant;
			this.totalCount = totalCount;
		}
		public int getBeforeCount() {
			return beforeCount;
		}
		public void setBeforeCount(int beforeCount) {
			this.beforeCount = beforeCount;
		}
		public int getAfterCount() {
			return afterCount;
		}
		public void setAfterCount(int afterCount) {
			this.afterCount = afterCount;
		}
		
		public int getMaxQuant() {
			return maxQuant;
		}
		public void setMaxQuant(int maxQuant) {
			this.maxQuant = maxQuant;
		}
		public int getTotalQuant() {
			return totalQuant;
		}
		public void setTotalQuant(int totalQuant) {
			this.totalQuant = totalQuant;
		}
		public int getTotalCount() {
			return totalCount;
		}
		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}	
	}
}
