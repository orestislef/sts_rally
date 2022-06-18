<?php
//http://localhost/sts/sts_updatebt_id_polyline.php?id=1&polyline=123456

//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'polyline';
 $username = 'root';
 $password = '';
 $conn;
 
 $id = $_GET['id'];
 $polyline = $_GET['polyline'];
//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "UPDATE " . $table_name . " SET `id`='" . $id . "',`polyline`='" . $polyline . "' WHERE id = '" . $id . "'";
	echo $sql;
	$result = mysqli_query($con,$sql);
	if($result){
		echo "Updated";
	}
	else{
		echo "DataSet conection failed";
	}
}
?>