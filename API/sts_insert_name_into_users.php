<?php
//http://localhost/sts/sts_insert_name_into_users.php?name=orestis

//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'users';
 $username = 'root';
 $password = '';
 $conn;
 
 $name = $_GET['name'];
//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "INSERT INTO `" . $table_name. "` (`id`, `name`) VALUES (NULL, '". $name ."')";
	echo $sql;
	$result = mysqli_query($con,$sql);
	if($result){
		echo "Inserted";
	}
	else{
		echo "DataSet conection failed";
	}
}
?>