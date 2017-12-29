<?php
session_start();
//-----------------------------------------------------------------------------Admin
//response by method
class Admin{
	function login(){
		//connet db
		require 'connect.php';
		mysqli_select_db($con,"admin");

		$account = $_POST['account'];
		$pwd = $_POST['pwd'];

		if(!isset($account)||empty($account)||!isset($pwd)||empty($pwd)) {
			return 'account or password is empty';
		}
		else {
			//query data by method
			$login_sql = "SELECT * FROM admin WHERE account = '$account' AND pwd = '$pwd'";
			$login_result = mysqli_query($con,$login_sql);

			if(mysqli_num_rows($login_result) == 0) {
				return 'error account or password';
			}
			else {
				//$login_dataArray = mysqli_fetch_all($login_result,MYSQLI_ASSOC);
				$_SESSION['account'] = $account;
				return 'login success';
			}
		}
	}
}

?>