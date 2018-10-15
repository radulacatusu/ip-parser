# ip-parser

## To Run 

java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500 --accesslog=d:\_tests\access.log

java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=daily --threshold=250 --accesslog=d:\_tests\access.log

java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.15:00:00 --duration=hourly --threshold=200 --accesslog=d:\_tests\access.log

java -cp "parser.jar" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 --accesslog=d:\_tests\access.log