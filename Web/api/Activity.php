<?php
//-----------------------------------------------------------------------------Activity
//response by method
class Activity{
	function getAll(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");

		//query data by method
		$getAll_sql = "SELECT * FROM activity";
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
		mysqli_select_db($con,"activity");

		if($id=='week'){
			//make time
			$starttime_week = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d'), date('Y'))) ;
			$endtime_week = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d')-7, date('Y'))) ;

			//query data by method
			$getbyweek_sql = "SELECT * FROM activity WHERE start_time >='$endtime_week' AND start_time <='$starttime_week'";
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
			$getbymonth_sql = "SELECT * FROM activity WHERE start_time >='$endtime_month' AND start_time <='$starttime_month'";
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
	function getById($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");
		
		//query data by method
		$getById_sql = "SELECT activity.uid, activity.id, activity.step, activity.bp, activity.data, activity.start_time, activity.end_time, activity.distance, activity.h_i_time, user.id, user.lname, user.fname, user.age FROM activity, user WHERE activity.id = '$id' AND activity.uid = user.id";
		$getById_result = mysqli_query($con,$getById_sql);
		
		if(mysqli_num_rows($getById_result) == 0) {
			return 'NULL';
		}
		else {
			$getById_dataArray = mysqli_fetch_array($getById_result,MYSQLI_ASSOC);
			return $getById_dataArray;
		}
	}
	function add($input){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");

		$uid = $input['uid'];
		$step = $input['step'];
		$bp = json_encode($input['bp']);
		$data = json_encode($input['data']);
		$start_time = $input['start_time'];
		$end_time = $input['end_time'];
		$distance = $input['distance'];
		$h_i_time = $input['h_i_time'];
		
		if(!isset($uid)||empty($uid)||!isset($step)||!isset($bp)||empty($bp)||!isset($data)||empty($data)||!isset($start_time)||empty($start_time)||!isset($end_time)||empty($end_time)||!isset($distance)||!isset($h_i_time)){
			return 'EMPTY';
		}
		else {
			$sql_insert = "INSERT INTO activity (uid,step,bp,data,start_time,end_time,distance,h_i_time) VALUES ('$uid','$step','$bp','$data','$start_time','$end_time','$distance','$h_i_time')";
			$add_result = mysqli_query($con,$sql_insert);
			return 'ok';
		}
	}

	function delete($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");
		
		//query data by method
		$sql_check = "SELECT * FROM activity WHERE id = '$id'";
		$check_result = mysqli_query($con,$sql_check);
		if(mysqli_num_rows($check_result) == 0) {
			return 'NULL';
		}
		else {
			$sql_delete = "DELETE FROM activity WHERE id = '$id'";
			$del_result = mysqli_query($con,$sql_delete);	
			return 'ok';
		}
	}

	function updatebyid($input){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");
		
		$id = $input['id'];
		$step = $input['step'];
		$bp = json_encode($input['bp']);
		$data = json_encode($input['data']);
		$start_time = $input['start_time'];
		$end_time = $input['end_time'];
		$distance = $input['distance'];
		$h_i_time = $input['h_i_time'];


		if(!isset($uid)||empty($uid)||!isset($step)||!isset($bp)||empty($bp)||!isset($data)||empty($data)||!isset($start_time)||empty($start_time)||!isset($end_time)||empty($end_time)||!isset($distance)||!isset($h_i_time)){
			return 'EMPTY';
		}
		else{
			$sql_check = "SELECT * FROM activity WHERE id = '$id'";
			$check_result = mysqli_query($con,$sql_check);

			if(mysqli_num_rows($check_result) == 0) {
				return 'NULL';
			}
			else {
				$sql_update ="UPDATE activity SET step='$step', bp='$bp', data='$data', start_time='$start_time', end_time='$end_time', distance='$distance', h_i_time='$h_i_time' WHERE id='$id'";
				$update_result = mysqli_query($con,$sql_update);	
				return 'ok';
			}
		}
	}
}

?>