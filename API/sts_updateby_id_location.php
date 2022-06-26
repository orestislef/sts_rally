<?php
//http://localhost/sts/sts_updateby_id_location.php?id=2&latitude=23.456&longitude=65.321

//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'location';
 $username = 'root';
 $password = '';
 $conn;
 
 $id = $_GET['id'];
 $latitude = $_GET['latitude'];
 $longitude = $_GET['longitude'];
//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "UPDATE " . $table_name. " SET `id`='" . $id . "',`latitude`='" . $latitude . "',`longitude`='" . $longitude ."' WHERE id = '" . $id . "'";
	$result = mysqli_query($con,$sql);
	if($result){
		echo "Updated";
	}
	else{
		echo "DataSet connection failed";
	}
}
?>