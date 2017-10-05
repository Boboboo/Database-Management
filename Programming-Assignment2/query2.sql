--Name: Bo Zhang
--CWID: 10411943
select cpm.*, tbefore.BEFORE_AVG, tafter.AFTER_AVG

from    (select cust as CUSTOMER, prod as PRODUCT, month
	from  sales
	group by cust,prod, month)
	as cpm     

        left join 

	(select s1.cust,s1.prod,s1.month,round(avg(s2.quant),0)as BEFORE_AVG
	from sales as s1 left join sales as s2
	on s2.month=(s1.month)-1
	and s1.cust=s2.cust 
	and s1.prod=s2.prod
	group by s1.cust,s1.prod,s1.month)
	as tbefore

	on cpm.CUSTOMER=tbefore.cust 
	and cpm.PRODUCT=tbefore.prod 
	and cpm.month=tbefore.month
	left join

	(select t1.cust,t1.prod,t1.month,round(avg(t2.quant),0) as AFTER_AVG
	from sales as t1 left join sales as t2
	on t2.month=(t1.month)+1
	and t1.cust=t2.cust
	and t1.prod=t2.prod
	group by t1.cust,t1.prod,t1.month)
	as tafter

	on cpm.CUSTOMER=tafter.cust 
	and cpm.PRODUCT=tafter.prod 
	and cpm.month=tafter.month

	order by CUSTOMER