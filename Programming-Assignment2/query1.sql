--Name:
--CWID: 

select cp.*, tavg.THE_AVG, pavg.OTHER_PROD_AVG, cavg.OTHER_CUST_AVG

from    (select cust as CUSTOMER,prod as PRODUCT
	from sales
	group by cust,prod)
	as cp
	
        left join 
        
	(select cust,prod ,round(avg(quant),0) as THE_AVG
	from sales
	group by cust,prod)
	as tavg
	
	on cp.CUSTOMER=tavg.cust and cp.PRODUCT=tavg.prod
	
	left join     

	(select s1.cust,s1.prod,round(avg(s2.quant),0) as OTHER_PROD_AVG
	from sales as s1
	join sales as s2
	on s1.prod<>s2.prod
	and s1.cust=s2.cust
	group by s1.cust,s1.prod)
	as pavg

	on cp.CUSTOMER=pavg.cust and cp.PRODUCT=pavg.prod
	
	left join 

	(select t1.cust,t1.prod,round(avg(t2.quant),0) as OTHER_CUST_AVG
	from sales as t1
	join sales as t2
	on t1.cust<>t2.cust
	and t1.prod=t2.prod
	group by t1.cust,t1.prod)
	as cavg

	on cp.CUSTOMER=cavg.cust and cp.PRODUCT=cavg.prod
	
	order by CUSTOMER
	
