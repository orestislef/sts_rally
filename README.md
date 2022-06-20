# Street Thugs Salonica Rally APP

## This is an API and Android APP witch created to pilot rally enthusiast into a 'follow the leader' way!

### Android client app 
1. Sends Users location at database
    location report fileter:
      min time in ms 5000(5sec). 
      min distance 30 Meters. 
2. Gets the polyline(aka route) from api
3. Shows Start and Finish icons on Map
4. Gets the Start and Finish points from api.  (NOT WET IMPLEMENTED)
5. Gets the leader position(from api) and adds a moving marker on map.  (NOT WET IMPLEMENTED)

### API
#### Get all as jSon
sts_get_all_location.php,
sts_get_all_polylines.php,
sts_get_all_users.php
#### Get one by ID as jSon
sts_getby_id_location.php,
sts_getby_id_polyline.php,
sts_getby_id_users.php
#### insert new using _GET
sts_insert_latlong_into_location.php,
sts_insert_name_into_users.php,
sts_insert_polyline_into_polyline.php
#### Update value with ID using _GET
sts_updatebt_id_polyline.php,
sts_updateby_id_location.php,
sts_updateby_id_users.php.
