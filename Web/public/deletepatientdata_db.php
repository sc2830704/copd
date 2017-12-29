<?php
// 設置資料類型 json
// 連結資料庫
require 'connect.php';
mysqli_select_db($con,"user");

if (!isset($_POST['ID']) || empty($_POST['ID'])||
	!isset($_POST['FirstName']) || empty($_POST['FirstName']))
{
    echo json_encode(array('msg' => 'Type ID and FirstName to delete data!!'));
    return;
}
else {
	$id = $_POST['ID'];
	$fname = $_POST['FirstName'];
	$sql = "SELECT * FROM user WHERE id = '$id' and fname = '$fname'";
	$result = mysqli_query($con,$sql);

	if(mysqli_num_rows($result) == 0) {
		echo json_encode(array('msg' => 'The data is not existence!'));
		return;
	}
	else {
		$sql_delete = "DELETE FROM user WHERE id = '$id' and fname = '$fname'";
		$del_result = mysqli_query($con,$sql_delete);	
		// 刪除成功，返回姓名
		echo json_encode(array('FirstName' => $_POST['FirstName']));
	}
}

?>