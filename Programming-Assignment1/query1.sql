select tmax.product, tmax.max_q, tmax.customer, tmax.date,tmax.st,
       tmin.min_q, tmin.customer,tmin.date,tmin.st,
       tavg.avg_q
        
From
( -- Get all the info of MAX value
	SELECT s.prod product, s.quant max_q, s.cust customer, CONCAT(
		lpad(month::text, 2, '0'), '/', 
		lpad(day::text, 2, '0'), '/', year) date, s.state st 
	FROM sales s
	WHERE (s.prod, s.quant) IN ( --condictions of MAX quant
		SELECT prod product, MAX(quant) q 
		FROM sales
		GROUP BY prod
	)
) tmax

  INNER JOIN

( -- Get all the info of MIN value
	SELECT s.prod product, s.quant min_q, s.cust customer, CONCAT(
		lpad(month::text, 2, '0'), '/', 
		lpad(day::text, 2, '0'), '/', year) date, s.state st 
	FROM sales s
	WHERE (s.prod, s.quant) IN ( --condictions of MIN quant
		SELECT prod p, MIN(quant) q 
		FROM sales
		GROUP BY prod
	)
) tmin

  ON tmax.product = tmin.product

  INNER JOIN
( -- Get all the info of AVERAGE value
	SELECT prod p, ROUND(AVG(quant),0) avg_q 
	FROM sales
	GROUP BY prod
) tavg

  ON tmax.product = tavg.p
  

