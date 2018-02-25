<?php
//-------------------------------------------------------------------------------Daily
class Daily{
	function getAll(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"daily");

		//query data by method
		$getAll_sql = "SELECT * FROM daily";
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
		mysqli_select_db($con,"daily");

		if($id=='week'){
			//make time
			$starttime_week = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d'), date('Y'))) ;
			$endtime_week = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d')-7, date('Y'))) ;

			//query data by method
			$getbyweek_sql = "SELECT * FROM daily WHERE date >='$endtime_week' AND date <='$starttime_week'";
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
			$getbymonth_sql = "SELECT * FROM daily WHERE date >='$endtime_month' AND date <='$starttime_month'";
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

	function getByUser($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"daily");

		//query data by method
		$getById_sql = "SELECT * FROM daily WHERE uid = '$id'";
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
		mysqli_select_db($con,"daily");

		//query data by method
		$uid = $input['uid'];
		$step = $input['step'];
		$date = $input['date'];
		$distance = $input['distance']; 
		$h_i_time = $input['h_i_time'];
		$updatetime = $input['updatetime'];

		if(!isset($uid)||empty($uid)||!isset($step)||!isset($date)||empty($date)||!isset($distance)||!isset($h_i_time)){
			return 'EMPTY';
		}
		else {
			$sql_insert = "INSERT INTO daily (uid, step, date, distance, h_i_time, updatetime) VALUES ('$uid','$step','$date', '$distance', '$h_i_time', '$updatetime')";
			$add_result = mysqli_query($con,$sql_insert);
			return 'ok';
		}
	}
	
	function deletebyid($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"daily");

		//query data by method
		$sql_check = "SELECT * FROM daily WHERE id = '$id'";
		$check_result = mysqli_query($con,$sql_check);
		if(mysqli_num_rows($check_result) == 0) {
			return 'NULL';
		}
		else {
			$sql_delete = "DELETE FROM daily WHERE id = '$id'";
			$del_result = mysqli_query($con,$sql_delete);	
			return 'ok';
		}
	}
}

?>