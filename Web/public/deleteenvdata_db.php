<?php
// 設置資料類型 json
// 連結資料庫
require 'connect.php';
mysqli_select_db($con,"env");

if (!isset($_POST['ID']) || empty($_POST['ID']))
{
    echo json_encode(array('msg' => 'Type ID to delete data!!'));
    return;
}
else {
	$id = $_POST['ID'];
	$sql = "SELECT * FROM env WHERE id = '$id'";
	$result = mysqli_query($con,$sql);

	if(mysqli_num_rows($result) == 0) {
		echo json_encode(array('msg' => 'The data is not existence!'));
		return;
	}
	else {
		$sql_delete = "DELETE FROM env WHERE id = '$id'";
		$del_result = mysqli_query($con,$sql_delete);	
		// 刪除成功，返回姓名
		echo json_encode(array('ID' => $_POST['ID']));
	}
}

?>