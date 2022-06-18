<?php
//http://localhost/sts/sts_get_all_location.php

//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'location';
 $username = 'root';
 $password = '';
 $conn;

//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "SELECT * FROM " . $table_name . "";
	$result = mysqli_query($con,$sql);
	if($result){
		header("Content-Type: JSON");
		$i = 0;
		while($row = mysqli_fetch_assoc($result)){
			$response[$i]['id'] = $row['id'];
			$response[$i]['longitude'] = $row['longitude'];
			$response[$i]['latitude'] = $row['latitude'];
			
			$i++;
			}
			echo json_encode($response,JSON_PRETTY_PRINT);
		}
			
	}
	else{
		echo "DataSet conection failed";
	}
?>