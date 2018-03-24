<?php
//-----------------------------------------------------------------------------Evaluate
//response by method

class Evaluate{
	
	function getAll(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"evaluate");
		//query data by method
		$getAll_sql = "SELECT * FROM evaluate";
		$getAll_result = mysqli_query($con,$getAll_sql);

		if(mysqli_num_rows($getAll_result) == 0) {
			return 'NULL';
		}
		else {
			$getAll_dataArray = mysqli_fetch_all($getAll_result,MYSQLI_ASSOC);
 			return $getAll_dataArray;
		}
	}
	/*function getById($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"evaluate");
		
		//query data by method
		$getById_sql = "SELECT * FROM evaluate WHERE id = '$id'";
		$getById_result = mysqli_query($con,$getById_sql);

		if(mysqli_num_rows($getById_result) == 0) {
			return 'NULL';
		}
		else {
			$getById_dataArray = mysqli_fetch_array($getById_result,MYSQLI_ASSOC);
			return $getById_dataArray;
		}
	}*/

	function add($input){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"evaluate");

		//$id = $input['id'];
		$uid = $input['uid'];
		$mmrc = $input['mmrc'];
		$cat1 = $input['cat1'];
		$cat2 = $input['cat2'];
		$cat3 = $input['cat3'];
		$cat4 = $input['cat4'];
		$cat5 = $input['cat5'];
		$cat6 = $input['cat6'];
		$cat7 = $input['cat7'];
		$cat8 = $input['cat8'];
		$datetime = $input['datetime'];

		$uid = strtolower($uid);

		if(!isset($uid)||empty($uid)||!isset($datetime)||!isset($mmrc)||!isset($cat1)||!isset($cat2)||!isset($cat3)||!isset($cat4)||!isset($cat5)||!isset($cat6)||!isset($cat7)||!isset($cat8)){
			return 'EMPTY';
		}
		else {
			$sql_insert = "INSERT INTO evaluate (uid,mmrc,cat1,cat2,cat3,cat4,cat5,cat6,cat7,cat8,datetime) VALUES ('$uid','$mmrc','$cat1','$cat2','$cat3','$cat4','$cat5','$cat6','$cat7','$cat8','$datetime')";
			$add_result = mysqli_query($con,$sql_insert);
			return 'ok';
		}
	}

	/*function delete($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"evaluate");

		$sql_check = "SELECT * FROM evaluate WHERE id = '$id'";
		$check_result = mysqli_query($con,$sql_check);
		if(mysqli_num_rows($check_result) == 0) {
			return 'NULL';
		}
		else {
			$sql_delete = "DELETE FROM evaluate WHERE id = '$id'";
			$del_result = mysqli_query($con,$sql_delete);	
			return 'ok';
		}
	}
	
	function update($input){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"evaluate");
		
		$id = $input['id'];
		$pwd = $input['pwd'];
		$fname = $input['fname'];
		$lname = $input['lname'];
		$age = $input['age'];
		$sex = $input['sex'];
		$bmi = $input['bmi'];
		$history = $input['history'];
		$drug = $input['drug'];
		$drug_other = $input['drug_other'];
		$history_other = $input['history_other'];		
		$env_id = $input['env_id'];
		$ble_id = $input['ble_id'];
		$watch_id = $input['watch_id'];

		// if(!isset($id)||empty($id)||!isset($pwd)||empty($pwd)||!isset($fname)||empty($fname)||!isset($lname)||empty($lname)||!isset($age)||empty($age)||!isset($sex)||!isset($bmi)||empty($bmi)||!isset($history)||empty($history)||!isset($drug)||empty($drug)||!isset($env_id)||empty($env_id)||!isset($ble_id)||empty($ble_id)||!isset($watch_id)||empty($watch_id)){
		if(!isset($id)||empty($id)||!isset($pwd)||empty($pwd)||!isset($fname)||empty($fname)||!isset($lname)||empty($lname)||!isset($age)||empty($age)||!isset($sex)||!isset($bmi)||empty($bmi)||!isset($history)||empty($history)||!isset($drug)||empty($drug)){
			return 'EMPTY';
		}
		else{
			$sql_check = "SELECT * FROM evaluate WHERE id = '$id'";
			$check_result = mysqli_query($con,$sql_check);

			if(mysqli_num_rows($check_result) == 0) {
				return 'NULL';
			}
			else {
				$sql_update ="UPDATE evaluate SET pwd='$pwd', fname='$fname', lname='$lname', age='$age', sex='$sex', bmi='$bmi', history='$history', drug='$drug', env_id='$env_id', ble_id='$ble_id', watch_id='$watch_id', drug_other='$drug_other', history_other='$history_other' WHERE id='$id'";
				$update_result = mysqli_query($con,$sql_update);	
				return 'ok';
			}
		}
	}*/
}
?>