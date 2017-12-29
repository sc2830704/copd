<?php
//----------------------------------------------------------------------------------Env
//response by method
class Env{
	function getAll(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"env");

		//query data by method
		$getAll_sql = "SELECT * FROM env";
		$getAll_result = mysqli_query($con,$getAll_sql);
		
		if(mysqli_num_rows($getAll_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$getAll_dataArray = mysqli_fetch_all($getAll_result,MYSQLI_ASSOC);
 			return $getAll_dataArray;
		}
	}
	function getByUser($userid){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"env");
		
		//query data by method
		$getByUser_sql = "SELECT * FROM env,user WHERE user.id = '$userid' && env_id = deviceid";
		$getByUser_result = mysqli_query($con,$getByUser_sql);
		
		if(mysqli_num_rows($getByUser_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$getByUser_dataArray = mysqli_fetch_all($getByUser_result,MYSQLI_ASSOC);
			return $getByUser_dataArray;
		}
	}
	function getById($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"env");
		
		//query data by method
		$getById_sql = "SELECT * FROM env WHERE deviceid = '$id'";
		$getById_result = mysqli_query($con,$getById_sql);

		if(mysqli_num_rows($getById_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$getById_dataArray = mysqli_fetch_all($getById_result,MYSQLI_ASSOC);
			return $getById_dataArray;
		}
	}
	function add($input){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"env");

		$deviceid = $input['deviceid'];
		$temperature = $input['temperature'];
		$humidity = $input['humidity'];
		$pm25 = $input['pm25'];
		$uv = $input['uv'];
		$datetime = $input['datetime'];

		if(!isset($deviceid)||empty($deviceid)||!isset($temperature)||empty($temperature)||!isset($humidity)||empty($humidity)||!isset($pm25)||empty($pm25)||!isset($uv)||empty($uv)||!isset($datetime)||empty($datetime)){
			return 'NULL Data Exist.';
		}
		else {
			$sql_insert = "INSERT INTO env (deviceid, temperature, humidity, pm25, uv, datetime) VALUES ('$deviceid','$temperature','$humidity','$pm25','$uv','$datetime')";
			$add_result = mysqli_query($con,$sql_insert);
			return 'ok';
		}
	}
}
?>

