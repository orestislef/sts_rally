<?php
//http://localhost/sts/sts_updateby_id_users.php?id=3&name=nikos

//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'users';
 $username = 'root';
 $password = '';
 $conn;
 
 $id = $_GET['id'];
 $name = $_GET['name'];
//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "UPDATE " . $table_name . " SET `id`='" . $id . "',`name`='" . $name . "' WHERE id = '" . $id . "'";
	$result = mysqli_query($con,$sql);
	if($result){
		echo "Updated";
	}
	else{
		echo "DataSet connection failed";
	}
}
?>