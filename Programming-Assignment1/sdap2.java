
/*Name: Bo Zhang
*CWID: 10411943
*
*General instructions:
*==>> Open the program in eclipse or other IDE for JAVA
*==>> Start a new project and copy my code in a new class file.
*==>> Run the program, and results should be in the console window
*==>> There should be one table, it is the answer for second query.
*	
*Data Structures: 
*==>>HashMap: a data structure used to implement an associative array, a structure that can map keys to values.
*==>>Chosen reason: Considering the mapping between product and corresponding values,using HashMap can easily find relations.
*
*Description of Algorithm:
*==>>Pseudo code: 
*  	 initiate classes,HahsMap and arguments for different kinds of information;
*  	 while(has next row from database) {
*  		get information of this new row;
*       get values from outer value; 
*  		if(outer value is null) {add new outer value;}
*       get values from inner value; 
*  		if(inner value is null) {add new inner value;}	
*  	    if(state is CT){ 
*  			if(2000<=year<=2005 ){
*               if(new quant > record Max)
*                   update CT_MAX information;
*           } 
*  		}else if(state is NY){ 
*           if(new quant < record Min)
*  				update NY_MIN information; 
*       }else if(state is NJ)
*  			if(new quant < record Min) 
*  				update NY_MIN information; 
*  		}
*       get inner HashMap;
*       get outer HashMap;		
*  	}
*   print table; 
*  		
*More detailed algorithm are clarified in comments.
*/
import java.sql.*;
import java.util.*;

public class sdap2 {
	public static void main(String[] args){
		String usr ="postgres"; 
		String pwd ="722722";    
		String url ="jdbc:postgresql://localhost:5432/postgres";
		
		String cust = null,prod = null,date,state;	//customer.product,date and state for this combination	
		int month,day,year,quant;		            //month,day,year and quantity for this combination
		//create a nested HashMap to store all information to be reported,outer HashMap key is product and inner HashMap key is customer
		HashMap<String,HashMap<String, ReportData>> report2= new HashMap<>();  
		  		
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
			    
			    //use a outer value(inner HashMap) to store corresponding information getting from outer key
			    HashMap<String, ReportData> customerMap=report2.get(prod);
			    
			        //if outer value is null,create new outer value
			    	if(customerMap==null){
			    		customerMap= new HashMap<>();
			    	}
			        
			    	//get values from inner value
			    	ReportData reportData= customerMap.get(cust);
			    	
			    	//if inner value is null,add new inner value
			    	if(reportData==null){
			    		reportData=new ReportData(cust);
			    	}
			    	
			    	//according to requirements to set maximum quantity for CT,and minimum quantity for NY and NJ
			    	if(currentData.getState().equals("CT")){  //update maximum quantity for CT
			    		if(year<=2005&&year>=2000){           //for CT, to limit dates between 2000 and 2005
				    		if(reportData.getMaxCTQuantity()==null || currentData.quant>reportData.getMaxCTQuantity().quant){
				    			reportData.setMaxCTQuantity(currentData);
				    		}
			    		}
			    	} else if(currentData.getState().equals("NY")){  //update minimum quantity for NY
			    		if(reportData.getMinNYQuantity()==null || currentData.quant<reportData.getMinNYQuantity().quant){
			    			reportData.setMinNYQuantity(currentData);
			    		}
			    	} else if(currentData.getState().equals("NJ")){  //update minimum quantity for NJ
			    		if(reportData.getMinNJQuantity()==null || currentData.quant<reportData.getMinNJQuantity().quant){
			    			reportData.setMinNJQuantity(currentData);
			    		}
			    	}
			    	
			    	customerMap.put(cust, reportData);
			    	report2.put(prod, customerMap);			    
			}
			
			//print the table for the second query
			System.out.println("CUSTOMER  PRODUCT  CT_MAX  DATE        NY_MIN  DATE        NJ_MIN  DATE      ");
			System.out.println("========  =======  ======  ==========  ======  ==========  ======  =========="); 
		    
			//use relation between outer key and value to get products and reportData values
			Set<Map.Entry<String, HashMap<String, ReportData>>> sEntries=report2.entrySet();
		    Iterator<Map.Entry<String, HashMap<String, ReportData>>> iterator=sEntries.iterator();
		    //use nested while loop to get outer and inner key and value
		    while(iterator.hasNext()){
		    	Map.Entry<String, HashMap<String, ReportData>> mEntry=iterator.next();
		    	String prodKey=mEntry.getKey();
		    	HashMap<String, ReportData> custValue= mEntry.getValue();
		    	
		    	Set<String> kSet=custValue.keySet();
		    	Iterator<String> iteratorSub=kSet.iterator();
		    	while(iteratorSub.hasNext()){		
		    		String keySub=iteratorSub.next();
		    		ReportData valueSub=custValue.get(keySub);
		    		
		    		// considering null value condition and make the table formatted  
		    		String cust0=String.format("%-10s",valueSub.custName);
			    	String prod0=String.format("%-9s",mEntry.getKey());
			    	String maxCTQuant=String.format("%6s","NULL");
			    	String maxCTDate=String.format("%10s","NULL");;
			    	if(valueSub.getMaxCTQuantity()!=null){
			    	  maxCTQuant=String.format("%6s",valueSub.getMaxCTQuantity().quant);	
			    	  maxCTDate=valueSub.getMaxCTQuantity().date;
			    	}
			    	
			    	String minNYQuant=String.format("%8s","NULL");
			    	String minNYDate=String.format("%10s","NULL");
			    	if(valueSub.getMinNYQuantity()!=null){
			    		 minNYQuant=String.format("%8s",valueSub.getMinNYQuantity().quant);	    	
				    	 minNYDate=valueSub.getMinNYQuantity().date;
			    	}
			    	
			    	String minNJQuant=String.format("%8s","NULL");
			    	String minNJDate=String.format("%10s","NULL");
			    	if(valueSub.getMinNJQuantity()!=null){
			    	 minNJQuant=String.format("%8s",valueSub.getMinNJQuantity().quant);	    	
			    	 minNJDate=valueSub.getMinNJQuantity().date;
			    	}
			    	
			    	//final print
			    	System.out.println(cust0+prod0+maxCTQuant+"  "+maxCTDate+minNYQuant+"  "+minNYDate+minNJQuant+"  "+minNJDate);
		    	}	    		    	
		    }
			
		}catch(SQLException e){
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}
	
	//use inner class to encapsulate ReportData and Setters, Getters and constructor functions
	private static class ReportData{
		
		String custName;
		SaleData maxCTQuantity;
		SaleData minNYQuantity;
		SaleData minNJQuantity;
		
		public ReportData(String custName) {
			super();
			this.custName = custName;
		}
		public ReportData(String custName, SaleData maxCTQuantity, SaleData minNYQuantity, SaleData minNJQuantity) {
			super();
			this.custName = custName;
			this.maxCTQuantity = maxCTQuantity;
			this.minNYQuantity = minNYQuantity;
			this.minNJQuantity = minNJQuantity;
		}
		public String getcustName() {
			return custName;
		}
		public void setcustName(String custName) {
			this.custName = custName;
		}
		public SaleData getMaxCTQuantity() {
			return maxCTQuantity;
		}
		public void setMaxCTQuantity(SaleData maxCTQuantity) {
			this.maxCTQuantity = maxCTQuantity;
		}
		public SaleData getMinNYQuantity() {
			return minNYQuantity;
		}
		public void setMinNYQuantity(SaleData minNYQuantity) {
			this.minNYQuantity = minNYQuantity;
		}
	
		public SaleData getMinNJQuantity() {
			return minNJQuantity;
		}
		public void setMinNJQuantity(SaleData minNJQuantity) {
			this.minNJQuantity = minNJQuantity;
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

