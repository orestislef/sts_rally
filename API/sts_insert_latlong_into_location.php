<?php
//http://localhost/sts/sts_insert_latlong_into_location.php?id=1&longitude=123456&latitude=321654

//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'location';
 $username = 'root';
 $password = '';
 $conn;
 
 $id = $_GET['id'];
 $longitude = $_GET['longitude'];
 $latitude = $_GET['latitude'];
 
//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "INSERT INTO `" . $table_name. "` (`id`, `longitude`, `latitude`) VALUES ('" . $id ."', '" . $longitude . "', '" . $latitude . "')";
	$result = mysqli_query($con,$sql);
	if($result){
		echo "Inserted";
	}
	else{
		$sql = "UPDATE `" . $table_name. "` SET `longitude` = '" . $longitude ."', `latitude` = '" . $latitude . "' WHERE `location`.`id` = " .$id  ;
		$result = mysqli_query($con,$sql);
		if($result){
			echo "Updated";
		}else{
			echo "DataSet connection failed";
		}
	}
}
?>