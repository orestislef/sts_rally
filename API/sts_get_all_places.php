<?php
//http://localhost/sts/sts_get_all_places.php
//DB params
 $host = 'localhost';
 $db_name = 'api_sts';
 $table_name = 'place';
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
		$i = 0;
		while($row = mysqli_fetch_assoc($result)){
			$response[$i]['id'] = $row['id'];
			$response[$i]['name'] = $row['name'];
			$response[$i]['longitude'] = $row['longitude'];
			$response[$i]['latitude'] = $row['latitude'];
			
			$i++;
			}
			echo json_encode($response,JSON_PRETTY_PRINT);
		}
	}
	else{
		echo "DataSet connection failed";
	}
?>