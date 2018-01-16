<?php
/** Error reporting */
error_reporting(E_ALL);
ini_set('display_errors', TRUE);
ini_set('display_startup_errors', TRUE);
date_default_timezone_set('Europe/London');

require_once('../TCPDF-master/tcpdf.php');

if (PHP_SAPI == 'cli')
	die('This example should only be run from a Web Browser');

/** Include PHPExcel */
require_once ('../PHPExcel-1.8/Classes/PHPExcel.php');

// Create new PHPExcel object
// Search TCPDF/config/tcpdf-config.php => PDF_PAGE_ORIENTATION = L
$objPHPEXcel_PDF = new TCPDF(PDF_PAGE_ORIENTATION, PDF_UNIT, PDF_PAGE_FORMAT, true, 'UTF-8', false);

// set document information
$objPHPEXcel_PDF->SetCreator(PDF_CREATOR);
$objPHPEXcel_PDF->SetPrintHeader(false);

// set margins
$objPHPEXcel_PDF->SetMargins(PDF_MARGIN_LEFT, PDF_MARGIN_TOP, PDF_MARGIN_RIGHT);
$objPHPEXcel_PDF->SetHeaderMargin(PDF_MARGIN_HEADER);
$objPHPEXcel_PDF->SetFooterMargin(PDF_MARGIN_FOOTER);

// set auto page breaks
$objPHPEXcel_PDF->SetAutoPageBreak(TRUE, PDF_MARGIN_BOTTOM);

// set image scale factor
$objPHPEXcel_PDF->setImageScale(PDF_IMAGE_SCALE_RATIO);

// add a page
$objPHPEXcel_PDF->AddPage();

// add some datas here 

// set title
$objPHPEXcel_PDF->SetTitle('運動資訊');
//$objPHPEXcel_PDF->SetFont('msungstdlight', '', 15); 可以顯示中文但會偏移
$objPHPEXcel_PDF->SetFont('cid0jp', '', 14); //可以顯示中韓日文且不會偏移
$objPHPEXcel_PDF->Write(0, '運動資訊', '', 0, 'C', true, 0, false, false, 0);

// 連結資料庫
require '../api/connect.php';
mysqli_select_db($con,"activity");
 
// query資料庫的資料 
$sql_user="SELECT * FROM activity";
$result = mysqli_query($con,$sql_user);

$_cnt = 0;
$table_data = "";
// 將資料庫的資料全部存在陣列
while($row = mysqli_fetch_array($result)) {
	$_cnt++;
	$row['start_time'] = str_replace("-", "/", $row['start_time']);
	$row['end_time'] = str_replace("-", "/", $row['end_time']);

	$table_data = $table_data . "<tr>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['id']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['uid']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['step']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['bp']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['data']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['distance']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['h_i_time']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['start_time']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['end_time']}</td>
     	</tr>";
}

$table = "
<table  border=\"1\" align=\"center\" cellpadding=\"3\">
	<tr>
		<td>編號</td>
		<td>使用者編號</td>
		<td>步數</td>
		<td>血壓</td>
		<td>血氧及心率</td>
		<td>運動距離</td>
		<td>高強度運動時間</td>
		<td>開始時間</td>
		<td>結束時間</td>
	</tr>
	". $table_data .
	"
</table>
";

$objPHPEXcel_PDF->writeHTML('<br>', true, false, false, false, '');
$objPHPEXcel_PDF->writeHTML($table, true, false, false, false, '');

$file_name = 'Activity.pdf';
$objPHPEXcel_PDF->Output($file_name,'I');

?>
