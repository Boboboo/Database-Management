
/*Name: Bo Zhang
*CWID: 10411943
*
*General instructions:
*==>> Open the program in eclipse or other IDE for JAVA
*==>> Start a new project and copy my code in a new class file.
*==>> Run the program, and results should be in the console window
*==>> There should be one table, it is the answer for first query.
*	
*Data Structures:
*==>>HashMap: a data structure used to implement an associative array, a structure that can map keys to values. 
*==>>Chosen reason:Considering the mapping between product and corresponding values,using HashMap can easily find relations.
*
*Description of Algorithm:
*==>>Pseudo code: 
*  	 initiate classes,HahsMap and arguments for different kinds of information;
*  	 while(has next row from database) {
*  		get information of this new row; 
*  		if(product exists){
*  		    find the same product in record; 
*  			if(new quant < record Min) {update the Min value; }
*           if(new quant > record Max) {update the Max value; }
*           sum = sum + quant;
*           number + 1;
*       else{add new product information; } 			   	    
*  	}
*   print table;
*  		
*More detailed algorithm are clarified in comments.
*/
import java.sql.*;
import java.util.*;

public class sdap1 {
	public static void main(String[] args){
		String usr ="postgres"; 
		String pwd ="722722";        
		String url ="jdbc:postgresql://localhost:5432/postgres";
		
		String cust,prod,date,state;	//customer.product,date and state for this combination	
		int month,day,year,quant;       //month,day,year and quantity for this combination
		//create a HashMap to store all information to be reported,String and ReportData are the types of products and corresponding report data.
		HashMap<String,ReportData> report1= new HashMap<>();  
		  		
		try{
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		}catch(Exception e){
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}
		
		try{
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			
			while (rs.next()) {
				// each time gets a new line, extract information from rs 
				cust = rs.getString("cust");  
				prod = rs.getString("prod");  	 
				month = rs.getInt("month");  
				day = rs.getInt("day");  
				year = rs.getInt("year");  
				state = rs.getString("state"); 
				quant = rs.getInt("quant"); 
				
				//set date format
				if(month<10 )         
					date="0"+month+"/";  
				else date=month+"/";		
				if(day<10)   
					date=date+"0"+day+"/";  
				else date=date+day+"/";
			    date=date+year;
			    
			    //create SaleData currentData with args
			    SaleData currentData= new SaleData(cust,prod,date,state,quant);   
			    
			    if(report1.get(prod)!=null){ //if ReportData report1 is not null
			    	ReportData reportData=report1.get(prod); // ReportData reportData gets product
			    	if(currentData.quant<reportData.getMinQuantity().quant){ //set minimum quantity
			    		reportData.setMinQuantity(currentData);
			    	}else if(currentData.quant>reportData.getMaxQuantity().quant){//set maximum quantity
			    		reportData.setMaxQuantity(currentData);
			    	}	

		    	    reportData.setTotalSale(quant+reportData.getTotalSale());  //set total sale quantity
			        reportData.setSaleRecordNumber(reportData.getSaleRecordNumber()+1);	//	set total sale times        		        
			  			    	
			    }else{ //if ReportData report1 is null,create ReportData reportData and set corresponding args values
			    	ReportData reportData=new ReportData(currentData, currentData, quant, 1);
			    	report1.put(prod, reportData);		    	
			    }
			}
			
			//print the table for the first query 
			System.out.println("PRODUCT  MAX_Q  CUSTOMER  DATE        ST  MIN_Q  CUSTOMER  DATE        ST  AVG_Q");
			System.out.println("=======  =====  ========  ==========  ==  =====  ========  ==========  ==  ====="); 
		    
			//use relation between key and value to get products and reportData values
			Set<Map.Entry<String, ReportData>> sEntries=report1.entrySet();
		    Iterator<Map.Entry<String, ReportData>> iterator=sEntries.iterator();
		    while(iterator.hasNext()){
		    	Map.Entry<String, ReportData> mEntry=iterator.next();
		    	String prodKey=mEntry.getKey();
		    	ReportData prodValue=mEntry.getValue();
		    	
		    	// make the table formatted
		    	String maxProd=String.format("%-9s",prodValue.getMaxQuantity().prod);
		    	String maxQuant=String.format("%5s",prodValue.getMaxQuantity().quant);
		    	String maxCust=String.format("%-10s",prodValue.getMaxQuantity().cust);
		    	String maxDate=prodValue.getMaxQuantity().date;
		    	String maxState=prodValue.getMaxQuantity().state;
		    	String minQuant=String.format("%5s",prodValue.getMinQuantity().quant);
		    	String minCust=String.format("%-10s",prodValue.getMinQuantity().cust);
		    	String minDate=prodValue.getMinQuantity().date;
		    	String minState=prodValue.getMinQuantity().state;
		    	String average=String.format("%5s",prodValue.totalSale/prodValue.saleRecordNumber);
		    	
		    	//final print
		    	System.out.println(maxProd+maxQuant+"  "+maxCust+maxDate+"  "+maxState+"  "+minQuant+"  "+minCust+minDate+"  "+minState+"  "+average);
		    }
			
		}catch(SQLException e){
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}
	
	//use inner class to encapsulate ReportData and Setters, Getters and constructor functions
	private static class ReportData{
		SaleData minQuantity;
		SaleData maxQuantity;
		int totalSale;
		int saleRecordNumber;
		public SaleData getMinQuantity() {
			return minQuantity;
		}
		public void setMinQuantity(SaleData minQuantity) {
			this.minQuantity = minQuantity;
		}
		public SaleData getMaxQuantity() {
			return maxQuantity;
		}
		public void setMaxQuantity(SaleData maxQuantity) {
			this.maxQuantity = maxQuantity;
		}
		public int getTotalSale() {
			return totalSale;
		}
		public void setTotalSale(int totalSale) {
			this.totalSale = totalSale;
		}
		public int getSaleRecordNumber() {
			return saleRecordNumber;
		}
		public void setSaleRecordNumber(int saleRecordNumber) {
			this.saleRecordNumber = saleRecordNumber;
		}
		public ReportData(SaleData minQuantity, SaleData maxQuantity, int totalSale, int saleRecordNumber) {
			super();
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
			this.totalSale = totalSale;
			this.saleRecordNumber = saleRecordNumber;
		}		
	}
	
	//use inner class to encapsulate SaleData and Setters, Getters and constructor functions
	private static class SaleData {
		String cust,prod,date,state;
		int quant;
				
		public SaleData(String cust, String prod, String date, String state, int quant) {
			super();
			this.cust = cust;
			this.prod = prod;
			this.date = date;
			this.state = state;
			this.quant = quant;
		}
		public String getCust() {
			return cust;
		}
		public void setCust(String cust) {
			this.cust = cust;
		}
		public String getProd() {
			return prod;
		}
		public void setProd(String prod) {
			this.prod = prod;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public int getQuant() {
			return quant;
		}
		public void setQuant(int quant) {
			this.quant = quant;
		}
				
	}

}
