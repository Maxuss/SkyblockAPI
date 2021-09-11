# SkyblockSketchpad API
[![](https://jitpack.io/v/Maxuss/SkyblockAPI.svg)](https://jitpack.io/#Maxuss/SkyblockAPI)

### How to use the API:

There are several endpoints in the API.
Each endpoint requires API key that you can get by doing /genkey on your SkyblockSketchpad server!

Example of request with key:
```http
GET http:<server ip>:8080/users?key=<api key>
```

There are several endpoints for the api.

* # `/`
Contains this page.
Does not require API key.

* # `/api`
Main endpoint, contains info about all other endpoints for the API.
Does not require API key.

* # `/api/users`
Contains all users that played on your server, 
their UUIDs and some more information about them.

* # `/api/users/{username}`
Replace `{username}` with username of user that played on the server already.
Provides skyblock-related information on the user.

* # `/api/slayers/{username}`
Replace `{username}` with username of user that played on the server already.
Provides slayer-related information on the user.