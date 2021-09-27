# SkyblockSketchpad API
[![](https://jitpack.io/v/Maxuss/SkyblockAPI.svg)](https://jitpack.io/#Maxuss/SkyblockAPI)

### How to use the API:

There are several endpoints in the API.
Each endpoint requires API key that you can get by doing /genkey on your SkyblockSketchpad server!

Example of request with key:
```http
GET http:<server ip>:8080/users?key=<api key>
Content-Type: application/json
```

There are several endpoints for the api.

* # GET `/`
Contains this page.
Does not require API key.

* # GET `/api`
Main endpoint, contains info about all other endpoints for the API.
Does not require API key.

* # GET `/api/users`
Contains all users that played on your server, 
their UUIDs and some more information about them.

* # GET `/api/users/{username}`
Replace `{username}` with username of user that played on the server already.
Provides skyblock-related information on the user.

* # GET `/api/slayers/{username}`
Replace `{username}` with username of user that played on the server already.
Provides slayer-related information on the user.

* # POST `/api/itemdata/`
Used to post hashed item data, to be converted into item later
Successful response contains UUID of the item stored.
Note that the item will be removed in 10 minutes after creation.

* # GET `/api/itemdata/{uuid}`
Used to get hashed item data from uuid.