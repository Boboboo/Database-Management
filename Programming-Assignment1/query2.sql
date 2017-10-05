
SELECT  CCP.cust,CCP.prod, CT.CT_MAX, CT.date, NY.NY_MIN, NY.date, NJ.NJ_MIN, NJ.date
FROM (SELECT cust,prod 
      FROM sales 
      GROUP BY cust,prod) AS CCP --all combinations of customers and products

LEFT JOIN

(SELECT cust,prod, quant AS CT_MAX, CONCAT(   --conditions of CT
		lpad(month::text, 2, '0'), '/', 
		lpad(day::text, 2, '0'), '/', year) date -- format date
 FROM sales 
 WHERE(cust,prod,quant) in 
       (SELECT cust,prod,MAX(quant) AS CT_MAX 
        FROM (SELECT cust,prod,quant 
              FROM sales 
              WHERE state='CT' 
              AND year between 2000 AND 2005) AS ct 
        GROUP BY cust, prod) ) AS CT

ON CT.cust=CCP.cust AND CT.prod=CCP.prod

LEFT JOIN

(SELECT cust,prod,quant AS NY_MIN, CONCAT(   --conditions of NY
		lpad(month::text, 2, '0'), '/', 
		lpad(day::text, 2, '0'), '/', year) date --format date
 FROM sales 
 WHERE(cust,prod,quant) in 
       (SELECT cust,prod,MIN(quant) AS NY_MIN 
        FROM (SELECT cust, prod, quant 
              FROM sales 
              WHERE state='NY') AS ny 
        GROUP BY cust,prod) ) AS NY

ON NY.cust=CCP.cust AND NY.prod=CCP.prod

LEFT JOIN

(SELECT cust,prod,quant AS NJ_MIN, CONCAT(   --conditiosn of NJ
		lpad(month::text, 2, '0'), '/', 
		lpad(day::text, 2, '0'), '/', year) date --format date
 FROM sales 
 WHERE(cust,prod,quant) in 
       (SELECT cust,prod,MIN(quant) AS NJ_MIN 
        FROM (SELECT cust, prod, quant 
              FROM sales 
              WHERE state='NJ') AS nj 
        GROUP BY cust,prod) ) AS NJ
 
ON NJ.cust=CCP.cust AND NJ.prod=CCP.prod 

ORDER BY cust,prod --show well organized results 
