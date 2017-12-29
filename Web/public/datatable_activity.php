<?php
require 'connect.php';
mysqli_select_db($con,"activity");
$sql = "SELECT * FROM activity";
$result = mysqli_query($con,$sql);

$dataArray = array();

while($row = mysqli_fetch_array($result)) {
    $dataArray[] = array($row["id"],$row["uid"],$row["step"],$row["start_time"],$row["end_time"]);
}
echo json_encode($dataArray);
?>