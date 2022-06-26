<?php
//http://localhost/sts/sts_insert_polyline_into_polyline.php?polyline=123456

//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'polyline';
 $username = 'root';
 $password = '';
 $conn;
 
 $polyline = $_GET['polyline'];
 
//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "INSERT INTO `" . $table_name. "` (`id`, `polyline`) VALUES (NULL, '" . $polyline . "')";
	$result = mysqli_query($con,$sql);
	if($result){
		echo "Inserted";
	}
	else{
		echo "DataSet connection failed";
	}
}
?>