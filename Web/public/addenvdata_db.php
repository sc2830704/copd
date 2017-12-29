<?php
// 設置資料類型 json
// 連結資料庫
require 'connect.php';
mysqli_select_db($con,"env");

if (!isset($_POST['ID']) || empty($_POST['ID']) ||
	!isset($_POST['Temperature']) || empty($_POST['Temperature']) ||
    !isset($_POST['Humidity']) || empty($_POST['Humidity']) ||
	!isset($_POST['PM25']) || empty($_POST['PM25']) ||
	!isset($_POST['UV']) || empty($_POST['UV'])) 
	{
    	echo json_encode(array('msg' => 'The data is not complete!!'));
        return;
    }
else {
	$id = $_POST['ID'];
	$temperature = $_POST['Temperature'];
	$humidity = $_POST['Humidity'];
	$pm25 = $_POST['PM25'];
	$uv = $_POST['UV'];
	$sql_insert = "INSERT INTO env (id, temperature, humidity, pm25, uv) VALUES ('$id','$temperature','$humidity','$pm25','$uv')";
	$result = mysqli_query($con,$sql_insert);	
	
	}

// 儲存成功，返回姓名
echo json_encode(array('ID' => $_POST['ID']));

?>