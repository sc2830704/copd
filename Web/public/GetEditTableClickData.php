<?php
// 設置資料類型 json
// 連結資料庫
require 'connect.php';
mysqli_select_db($con,"user");
$id=$_POST['ID'];
$sql = "SELECT * FROM user WHERE id = '$id'";
$result = mysqli_query($con,$sql);
$row = mysqli_fetch_array($result,MYSQLI_ASSOC);
// 返回資料
echo json_encode($row);

?>