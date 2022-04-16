# ip-filter-service

### How to test -

Run sample as follows with the text file as input.

1.Add Filter Rule - Sample request

````

http://localhost:8080/filterRules/
HTTP 1.1 POST
Request BODY
{
"sourceCidr" : "10.2.0.1/11",
"destinationCidr" : "192.169.0.1/2311",
"alloedDenyInd" : "1"
}
````

2.Remove Filter Rule - Sample request

````

http://localhost:8080/filterRules/1
HTTP 1.1 DELETE
````

3. Check Allow/Deny Rule - Sample request

````

http://localhost:8080/filterRules/allow
HTTP 1.1 GET
Request BODY 
{
"sourceIp" : "110.0.0.1",
"destinationIp" : "192.168.0.1"  
}
````
