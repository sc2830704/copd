<?php
require 'connect.php';
mysqli_select_db($con,"user");
$sql = "SELECT * FROM user";
$result = mysqli_query($con,$sql);

$dataArray = array();

while($row = mysqli_fetch_array($result)) {
	if($row["sex"]=='1')
    	$dataArray[] = array($row["id"],$row["fname"],$row["lname"],'男',$row["bmi"],$row['history'],$row['drug'],$row["env_id"],$row["ble_id"],$row["watch_id"]);
    else
    	$dataArray[] = array($row["id"],$row["fname"],$row["lname"],'女',$row["bmi"],$row['history'],$row['drug'],$row["env_id"],$row["ble_id"],$row["watch_id"]);
}
echo json_encode($dataArray);
?>