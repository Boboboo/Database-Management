
/*Name: 0
*CWID: 0
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
*==>>ArrayList: a data structure supports dynamic arrays that can grow as needed. Standard Java arrays are of a fixed length.
*==>>Chosen reason:Considering no special requirements, using ArrayList, a basic collection data structure.
*  		
*More detailed algorithm are clarified in comments.
*/

import java.sql.*;
import java.util.*;
public class Sd1 {
	public static void main(String[] args){
		String usr ="postgres";
		String pwd ="722722";
		String url ="jdbc:postgresql://localhost:5432/postgres";
			
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
			
			List<SaleData> report1=new ArrayList<>();               //to store each combination of customer and product
			
			HashMap<String, Integer> custQuant=new HashMap<>();     //to store total quantity of each customer
			HashMap<String, Integer> custCount=new HashMap<>();     //to store total sale count of each customer
			HashMap<String, Integer> prodQuant=new HashMap<>();     //to store total quantity each product
			HashMap<String, Integer> prodCount=new HashMap<>();     //to store total sale count of each product
						
			while (rs.next()){
				String customer = rs.getString("cust");             // extract useful information 
				String product = rs.getString("prod");  	    
				int quantity = rs.getInt("quant");
				
				boolean newReport=true;                             //to test if there is new sale information
				
				//if custQunat is null, add total quantity and total sale count of each customer, if not, update it.
				if(custQuant.containsKey(customer)){                
					custQuant.put(customer, custQuant.get(customer)+quantity);
					custCount.put(customer, custCount.get(customer)+1);
				}else{
					custQuant.put(customer, quantity);
					custCount.put(customer,1);
				}	
                
				//if prodQunat is null, add total quantity and total sale count of each product, if not, update it.
				if(prodQuant.containsKey(product)){                 
					prodQuant.put(product, prodQuant.get(product)+quantity);
					prodCount.put(product, prodCount.get(product)+1);
				}else{
					prodQuant.put(product, quantity);
					prodCount.put(product,1);
				}
				
				//to get total quantities and counts for all the combinations of customer and product
				Iterator<SaleData> iterator=report1.iterator();      
				while(iterator.hasNext()){
					SaleData temp=iterator.next();
					if(temp.cust.equals(customer) && temp.prod.equals(product)){
						newReport=false;                            //combination found, update its information
						temp.quant=temp.quant+quantity;
						temp.count++;
					}										
				}
				if(newReport){                                      //add new combination to ArrayList
					SaleData saleData=new SaleData(customer, product, quantity);
					report1.add(saleData);
				}													
			}
			
			//print the table for the first query
			System.out.println("");
			System.out.println("Report #1:");
			System.out.println("CUSTOMER PRODUCT  THE_AVG  OTHER_PROD_AVG  OTHER_CUST_AVG");
			System.out.println("======== =======  =======  ==============  =============="); 
			
			//for each sale information in report1 list, 
			//to get average quantities of other products were bought by the this customer 
			//and get average quantities other customers bought for the this product
			for(SaleData temp : report1){
				int THE_AVG=(temp.quant==0?0: temp.quant/temp.count);
				int OTHER_PROD_AVG=(custQuant.get(temp.cust)-temp.quant==0 ? 0: (custQuant.get(temp.cust)-temp.quant)/(custCount.get(temp.cust)-temp.count));
				int OTHER_CUST_AVG=(prodQuant.get(temp.prod)-temp.quant==0 ? 0: (prodQuant.get(temp.prod)-temp.quant)/(prodCount.get(temp.prod)-temp.count));  	    		
	    		
				//make the table formatted  
				String cust0=String.format("%-9s",temp.cust);
		    	String prod0=String.format("%-9s",temp.prod);
		    	String prodAvg=String.format("%7s",THE_AVG);
		    	String otherProdAvg=String.format("%16s",OTHER_PROD_AVG);
		    	String otherCustAvg=String.format("%16s",OTHER_CUST_AVG);
		    	
		    	//final print
		    	System.out.println(cust0 + prod0 + prodAvg + otherProdAvg + otherCustAvg);
		    }	    	    
		}

		catch(SQLException e){
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}
	
	//use inner class to encapsulate SaleData and Setters, Getters and constructor functions
	public static class SaleData {	   
		String cust;
		String prod;
		int quant;
		int count;
		
		public SaleData(String cust, String prod,int quant) {
			super();
			this.cust = cust;
			this.prod = prod;
			this.quant = quant;	
			count=1;
		}				
	}
}
