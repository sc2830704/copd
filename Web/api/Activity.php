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
			return 'No data avaliable.';
		}
		else {
			$getAll_dataArray = mysqli_fetch_all($getAll_result,MYSQLI_ASSOC);
			return $getAll_dataArray;
		}
	}
	function getAll_week(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");

		//make time
		$starttime = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d'), date('Y'))) ;
		$endtime = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d')-7, date('Y'))) ;

		//query data by method
		$getAll_week_sql = "SELECT * FROM activity WHERE start_time >='$endtime' AND start_time <='$starttime'";
		$getAll_week_result = mysqli_query($con,$getAll_week_sql);

		if(mysqli_num_rows($getAll_week_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			/*$getAll_week_dataArray = array();
			while($row = mysqli_fetch_array($getAll_week_result)) {
			    $getAll_week_dataArray[] = array($row["id"],$row["uid"],$row["step"],$row["start_time"],$row["end_time"],$row["distance"],$row["h_i_time"]);
			}
			return $getAll_week_dataArray;*/
			$getAll_week_dataArray = mysqli_fetch_all($getAll_week_result,MYSQLI_ASSOC);
			return $getAll_week_dataArray;
		}
	}
	function getAll_month(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");

		//make time
		$starttime = date ("Y-m-d H:i:s" , mktime(date('H')+7, date('i'), date('s'), date('m'), date('d'), date('Y'))) ;
		$endtime = date ("Y-m-d H:i:s" , mktime(0, 0, 0, date('m'), 1, date('Y'))) ;

		//query data by method
		$getAll_month_sql = "SELECT * FROM activity WHERE start_time >='$endtime' AND start_time <='$starttime'";
		$getAll_month_result = mysqli_query($con,$getAll_month_sql);

		if(mysqli_num_rows($getAll_month_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			/*$getAll_month_dataArray = array();
			while($row = mysqli_fetch_array($getAll_month_result)) {
			    $getAll_month_dataArray[] = array($row["id"],$row["uid"],$row["step"],$row["start_time"],$row["end_time"],$row["distance"],$row["h_i_time"]);
			}
			return $getAll_month_dataArray;*/
			$getAll_month_dataArray = mysqli_fetch_all($getAll_month_result,MYSQLI_ASSOC);
			return $getAll_month_dataArray;
		}
	}
	function getById($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");
		
		//query data by method
		$getById_sql = "SELECT activity.uid, activity.id, activity.step, activity.bp, activity.data, activity.start_time, activity.end_time, activity.distance, activity.h_i_time, user.id, user.lname, user.fname FROM activity, user WHERE activity.id = '$id' AND activity.uid = user.id";
		$getById_result = mysqli_query($con,$getById_sql);
		
		if(mysqli_num_rows($getById_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$getById_dataArray = mysqli_fetch_array($getById_result,MYSQLI_ASSOC);
			return $getById_dataArray;
		}
	}
	function getByIdBp($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");
		
		//query data by method
		$getByIdData_sql = "SELECT bp FROM activity WHERE id = '$id'";
		$getByIdData_result = mysqli_query($con,$getByIdData_sql);

		if(mysqli_num_rows($getByIdData_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$getByIdData_dataArray = array();
			while($row = mysqli_fetch_array($getByIdData_result)) {
				$personal_bp = json_decode($row[0]);
			    $getByIdData_dataArray[] = array($personal_bp->before->sbp,$personal_bp->before->dbp,$personal_bp->after->sbp,$personal_bp->after->dbp);
			}
			return $getByIdData_dataArray;
		}
	}
	function getByIdData($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");
		
		//query data by method
		$getByIdTable_sql = "SELECT activity.data FROM activity,user WHERE activity.id = '$id' AND activity.uid = user.id";
		$getByIdTable_result = mysqli_query($con,$getByIdTable_sql);

		if(mysqli_num_rows($getByIdTable_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$getByIdTable_dataArray = array();
			while($row = mysqli_fetch_array($getByIdTable_result)) {
				$personal_data = json_decode($row[0]);
			    $getByIdTable_dataArray[] = array($personal_data->spo2,$personal_data->hr,$personal_data->datetime);
			}
			return $getByIdTable_dataArray;
		}
	}
	function getByUser($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"activity");
		
		//query data by method
		$getByUserId_sql = "SELECT * FROM activity WHERE id = '$id'";
		$getByUserId_result = mysqli_query($con,$getByUserId_sql);
		
		if(mysqli_num_rows($getByUserId_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$getByUserId_dataArray = mysqli_fetch_all($getByUserId_result,MYSQLI_ASSOC);
			return $getByUserId_dataArray;
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
		if(!isset($uid)||empty($uid)||!isset($step)||!isset($bp)||empty($bp)||!isset($data)||empty($data)
			||!isset($start_time)||empty($start_time)||!isset($end_time)||empty($end_time)||!isset($distance)
			||!isset($h_i_time)){
			return 'NULL Data Exist.';
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
			return 'No data avaliable.';
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

		$sql_check = "SELECT * FROM activity WHERE id = '$id'";
		$check_result = mysqli_query($con,$sql_check);
		if(mysqli_num_rows($check_result) == 0) {
			return 'No data avaliable.';
		}
		else {
			$sql_update ="UPDATE activity SET step='$step', bp='$bp', data='$data', start_time='$start_time', end_time='$end_time', distance='$distance', h_i_time='$h_i_time' WHERE id='$id'";
			$update_result = mysqli_query($con,$sql_update);	
			return 'ok';
		}
	}
}

?>