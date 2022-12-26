# jwtAuthorization
Getting data from endpoints using jwt authorization

# API DOCUMENTATION

API Endpoint
------------
base-url: /v1

The list of endpoints related to encoding and decoding urls-

1. POST: host-url/base-url/authenticate [Takes the payload and generates a jwt token]
   - Content-Type: application/json
   - Example Request-Body: 
   {
    "name": "Alvi Ibne Alam",
    "id": "001",
    "validated": "false"
   }
2. GET: host-url/base-url/get [prints a hard coded string Book in the response]
   - Authorization Header: Bearer "jwt token"
   - Example Response-Body: 
        -"Book"
3. GET: host-url/base-url/getAll [prints a hard coded string array Pen, Book in the response]
   - Authorization Header: Bearer "jwt token"
   - Example Response-Body: 
        -["Pen","Book"]
        
Username to match for authentication can be specified in the application.properties and password is ahrd coded.
