--Name: Bo Zhang
--CWID: 10411943

with S (prod,month,Savg,Smax) as
	(select prod,month, avg(quant), max(quant) 
	from sales 
	group by prod,month)

,tbefore(prod,month,before_Tot) as
        (select S.prod,S.month,count(sales.quant) 
	from S left join sales 
	on sales.prod=S.prod 
	and sales.month=S.month-1  
	and sales.quant between S.Savg and S.Smax
	group by S.prod,S.month)

,tafter(prod,month,after_Tot) as
	(select S.prod,S.month,count(sales.quant) 
	from S left join sales 
	on sales.prod=S.prod 
	and sales.month=S.month+1 
	and sales.quant between S.Savg and S.Smax
	group by S.prod,S.month)

select * 
from tbefore 
natural full outer join tafter 

order by prod,month