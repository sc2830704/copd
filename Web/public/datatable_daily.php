<?php
require 'connect.php';
mysqli_select_db($con,"daily");
$sql = "SELECT * FROM daily";
$result = mysqli_query($con,$sql);

$dataArray = array();

while($row = mysqli_fetch_array($result)) {
    $dataArray[] = array($row["id"],$row["uid"],$row["step"],$row["date"]);
}
echo json_encode($dataArray);
?>