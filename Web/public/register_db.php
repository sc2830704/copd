<?php
// 連結資料庫
require 'connect.php';
mysqli_select_db($con,"admin");

    if (!isset($_POST['accountName']) || empty($_POST['accountName']) ||
        !isset($_POST['password1']) || empty($_POST['password1']) ||
        !isset($_POST['password2']) || empty($_POST['password2'])) 
    {
      	echo json_encode(array('msg' => '資料填寫未完全'));
        return;
    }
	else {
		$password1 = $_POST['password1'];
		$password2 = $_POST['password2'];
		if(!($password1==$password2)){
			echo json_encode(array('msg' => '兩組密碼不相同'));
			return;
		}
		else {
			$account = $_POST['accountName'];
			$pwd = $_POST['password1'];
				


			$sql_check = "SELECT * FROM admin WHERE account = '$account'";
			$check_result = mysqli_query($con,$sql_check);
			if(mysqli_num_rows($check_result) == 0) {
				$sql_insert = "INSERT INTO admin (account, pwd) VALUES ('$account','$pwd')";
				$result = mysqli_query($con,$sql_insert);
				// 儲存成功，返回帳戶名
		    	echo json_encode(array('accountName' => $_POST['accountName']));
			}
			else {
				echo json_encode(array('msg' => '這個帳號已存在'));
			}
		}
	}
   	
?>