<?php
//http://localhost/sts/sts_getby_id_users.php?id=3


//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'users';
 $username = 'root';
 $password = '';
 $conn;
 
 $id = $_GET['id'];
//DB Connect
$con = mysqli_connect($host,$username,$password,$db_name);
$response = array();
if($con){
	$sql = "SELECT * FROM " . $table_name . " where id like " . $id;
	$result = mysqli_query($con,$sql);
	if($result){
		header("Content-Type: JSON");
		$i = 0;
		while($row = mysqli_fetch_assoc($result)){
			$response[$i]['id'] = $row['id'];
			$response[$i]['name'] = $row['name'];
			
			$i++;
			}
			echo json_encode($response,JSON_PRETTY_PRINT);
		}
			
	}
	else{
		echo "DataSet conection failed";
	}
?>