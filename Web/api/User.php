<?php
//-----------------------------------------------------------------------------User
//response by method
class User{
	function getAll(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"user");
		//query data by method
		$getAll_sql = "SELECT * FROM user";
		$getAll_result = mysqli_query($con,$getAll_sql);

		if(mysqli_num_rows($getAll_result) == 0) {
			return 'NULL';
		}
		else {
			$getAll_dataArray = mysqli_fetch_all($getAll_result,MYSQLI_ASSOC);
 			return $getAll_dataArray;
		}
	}
	function getById($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"user");
		
		//query data by method
		$getById_sql = "SELECT * FROM user WHERE id = '$id'";
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
		mysqli_select_db($con,"user");

		$id = $input['ID'];
		$pwd = $input['Password'];
		$fname = $input['FirstName'];
		$lname = $input['LastName'];
		$age = $input['Age'];
		$sex = $input['Sex'];
		$bmi = $input['BMI'];
		$history = $input['History'];
		$drug = $input['Drug'];
		$env_id = $input['ENV_ID'];
		$ble_id = $input['BLE_ID'];
		$watch_id = $input['Watch_ID'];

		if(!isset($id)||empty($id)||!isset($pwd)||empty($pwd)||!isset($fname)||empty($fname)||!isset($lname)||empty($lname)||!isset($age)||empty($age)||!isset($sex)||!isset($bmi)||empty($bmi)||!isset($history)||empty($history)||!isset($drug)||empty($drug)||!isset($env_id)||empty($env_id)||!isset($ble_id)||empty($ble_id)||!isset($watch_id)||empty($watch_id)){
			return 'EMPTY';
		}
		else {
			$sql_check = "SELECT * FROM user WHERE id = '$id'";
			$check_result = mysqli_query($con,$sql_check);
			if(mysqli_num_rows($check_result) == 0) {
				$sql_insert = "INSERT INTO user (id,pwd,fname,lname,age,sex,bmi,history,drug,env_id,ble_id,watch_id) VALUES ('$id','$pwd','$fname','$lname','$age','$sex','$bmi','$history','$drug','$env_id','$ble_id','$watch_id')";
				$add_result = mysqli_query($con,$sql_insert);
				return 'ok';
			}
			else {
				return 'EXIST';
			}
		}
	}

	function delete($id){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"user");

		$sql_check = "SELECT * FROM user WHERE id = '$id'";
		$check_result = mysqli_query($con,$sql_check);
		if(mysqli_num_rows($check_result) == 0) {
			return 'NULL';
		}
		else {
			$sql_delete = "DELETE FROM user WHERE id = '$id'";
			$del_result = mysqli_query($con,$sql_delete);	
			return 'ok';
		}
	}
	
	function update($input){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"user");
		
		$id = $input['ID'];
		$pwd = $input['Password'];
		$fname = $input['FirstName'];
		$lname = $input['LastName'];
		$age = $input['Age'];
		$sex = $input['Sex'];
		$bmi = $input['BMI'];
		$history = $input['History'];
		$drug = $input['Drug'];
		$env_id = $input['ENV_ID'];
		$ble_id = $input['BLE_ID'];
		$watch_id = $input['Watch_ID'];


		if(!isset($id)||empty($id)||!isset($pwd)||empty($pwd)||!isset($fname)||empty($fname)||!isset($lname)||empty($lname)||!isset($age)||empty($age)||!isset($sex)||!isset($bmi)||empty($bmi)||!isset($history)||empty($history)||!isset($drug)||empty($drug)||!isset($env_id)||empty($env_id)||!isset($ble_id)||empty($ble_id)||!isset($watch_id)||empty($watch_id)){
			return 'EMPTY';
		}
		else{
			$sql_check = "SELECT * FROM user WHERE id = '$id'";
			$check_result = mysqli_query($con,$sql_check);

			if(mysqli_num_rows($check_result) == 0) {
				return 'NULL';
			}
			else {
				$sql_update ="UPDATE user SET pwd='$pwd', fname='$fname', lname='$lname', age='$age', sex='$sex', bmi='$bmi', history='$history', drug='$drug', env_id='$env_id', ble_id='$ble_id', watch_id='$watch_id' WHERE id='$id'";
				$update_result = mysqli_query($con,$sql_update);	
				return 'ok';
			}
		}
	}
}

?>