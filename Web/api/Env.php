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
			return 'NULL';
		}
		else {
			$getAll_dataArray = mysqli_fetch_all($getAll_result,MYSQLI_ASSOC);
 			return $getAll_dataArray;
		}
	}
	function getByTime($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"env");

		if($id=='week'){
			//make time
			$starttime_week = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d'), date('Y'))) ;
			$endtime_week = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d')-7, date('Y'))) ;

			//query data by method
			$getbyweek_sql = "SELECT * FROM env WHERE datetime >='$endtime_week' AND datetime <='$starttime_week'";
			$getbyweek_result = mysqli_query($con,$getbyweek_sql);

			if(mysqli_num_rows($getbyweek_result) == 0) {
				return 'NULL';
			}
			else {
				$getbyweek_data = mysqli_fetch_all($getbyweek_result,MYSQLI_ASSOC);
				return $getbyweek_data;
			}
		}
		else if($id=='month'){
			//make time
			$starttime_month = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d'), date('Y'))) ;
			$endtime_month = date ("Y-m-d H:i:s" , mktime(0, 0, 0, date('m'), 1, date('Y'))) ;

			//query data by method
			$getbymonth_sql = "SELECT * FROM env WHERE datetime >='$endtime_month' AND datetime <='$starttime_month'";
			$getbymonth_result = mysqli_query($con,$getbymonth_sql);

			if(mysqli_num_rows($getbymonth_result) == 0) {
				return 'NULL';
			}
			else {
				$getbymonth_data = mysqli_fetch_all($getbymonth_result,MYSQLI_ASSOC);
				return $getbymonth_data;
			}
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
			return 'NULL';
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
		$getById_sql = "SELECT * FROM env WHERE deviceid = 'A002' OR deviceid = 'A003'";
		$getById_result = mysqli_query($con,$getById_sql);

		if(mysqli_num_rows($getById_result) == 0) {
			return 'NULL';
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

		if(!isset($deviceid)||!isset($temperature)||!isset($humidity)||!isset($pm25)||!isset($uv)||!isset($datetime)){
			return 'EMPTY';
		}
		else {
			$sql_insert = "INSERT INTO env (deviceid, temperature, humidity, pm25, uv, datetime) VALUES ('$deviceid','$temperature','$humidity','$pm25','$uv','$datetime')";
			$add_result = mysqli_query($con,$sql_insert);
			return 'ok';
		}
	}
}
?>

