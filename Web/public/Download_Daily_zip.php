<?php

$start_time = $_GET['start_time'];
$end_time = $_GET['end_time'];
$type = $_GET['type'];

require '../api/connect.php';
mysqli_select_db($con,"daily");
//算出區間總共有多少天 

$day_starttime = strtotime($start_time);
$day_endtime = strtotime($end_time);
$day = round(($day_endtime-$day_starttime)/3600/24)+1; 

for($count=0;$count<$day;$count++){
	$download_date[$count] = $end_time;
	//天數-1
	$end_time = strtotime($end_time);
	$end_time = date('Y-m-d', strtotime('-1 days', $end_time));
}

//echo implode("", $download_date);
if ($type=='PDF'){
	$endpoint = "localhost/copd/public/Download_Daily_PDF.php?";
}
else {
	$endpoint = "localhost/copd/public/Download_Daily_Excel.php?";
}
//$endpoint = "Download_Environment_Excel.php?";
$ch = curl_init();
$empty_check = 0;
foreach($download_date as $value){
	//echo $value."<br>";
	$check_sql = "SELECT * FROM daily WHERE date = '$value'";
	$check_result = mysqli_query($con,$check_sql);
	//echo mysqli_num_rows($check_result)."<br>";
	if(mysqli_num_rows($check_result) == 0) {
		$empty_check = $empty_check + 1;
		if($empty_check==$day){
			header("Location: Download_Daily_Empty.php");
		} 
	}
	else{
		$url = $endpoint."download_date=".$value;
		curl_setopt($ch,CURLOPT_URL,$url);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		$data = curl_exec($ch);
	}
}
curl_close($ch);

//打包壓縮檔
if ($type=='PDF'){
	$files = array();
	foreach($download_date as $value){
		array_push($files,'download/Daily '.$value.'.pdf');
	}
	$zipname = 'Daily_PDF.zip';
}
else {
	$files = array();
	foreach($download_date as $value){
		array_push($files,'download/Daily '.$value.'.xlsx');
	}
	$zipname = 'Daily_Excel.zip';
}

$zip = new ZipArchive;
$zip->open($zipname, ZipArchive::CREATE);
foreach ($files as $file) {
  $zip->addFile($file);
}
$zip->close();

header('Content-Type: application/zip');
header('Content-disposition: attachment; filename='.$zipname);
header('Content-Length: ' . filesize($zipname));
readfile($zipname);

foreach ($files as $file) {
  unlink($file);
}
unlink($zipname);

?>