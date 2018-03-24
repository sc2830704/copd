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

/************************************************************************COMMENT OR DEFINITION*********************************************************************

SetTitle($title)
SetFont($family, $style='', $size=null, $fontfile='', $subset='default', $out=true)
Write($h, $txt, $link='', $fill=false, $align='', $ln=false, $stretch=0, $firstline=false, $firstblock=false, $maxh=0, $wadj=0, $margin='')
writeHTML($html, $ln=true, $fill=false, $reseth=false, $cell=false, $align='')

example:
$pdf->SetTitle('台灣科技大學電子系'.$semester.'年度專題一覽表');
	// set font
	$pdf->SetFont('msungstdlight', '', 25);
	$txt = '台灣科技大學電子系'.$semester.'年度專題一覽表';
	$pdf->Write(0, $txt, '', 0, 'L', true, 0, false, false, 0);
	$pdf->SetFont('msungstdlight', '', 16);
	//html
	$txt = "<br/>";
	$pdf->writeHTML($txt, true, false, false, false, '');
	$pdf->SetFont('msungstdlight', '', 10);
******************************************************************************************************************************************************************/

// set title
$objPHPEXcel_PDF->SetTitle('每日資訊');
//$objPHPEXcel_PDF->SetFont('msungstdlight', '', 15); 可以顯示中文但會偏移
$objPHPEXcel_PDF->SetFont('cid0jp', '', 16); //可以顯示中韓日文且不會偏移
$objPHPEXcel_PDF->Write(0, '每日資訊', '', 0, 'C', true, 0, false, false, 0);

// 連結資料庫
require '../api/connect.php';
mysqli_select_db($con,"daily");

$download_date = $_GET['download_date'];
//$start_time = $download_date.' 00:00:00';
//$end_time = $download_date.' 23:59:59';
$name = $download_date;

// query資料庫的資料 
//$sql_env="SELECT * FROM env WHERE datetime >='$start_time' AND datetime <='$end_time'";
$sql_env = "SELECT * FROM daily WHERE date = '$download_date'";
$result = mysqli_query($con,$sql_env);

$_cnt = 0;
$table_data = "";
// 將資料庫的資料全部存在student這個陣列
while($row = mysqli_fetch_array($result)) {
	$_cnt++;
	$table_data = $table_data . "<tr>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['id']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['uid']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['step']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['distance']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['h_i_time']}</td>
     		<td border=\"1\" align=\"center\" cellpadding=\"3\">{$row['date']}</td>
	 	</tr>";
}

$table = "
<table  border=\"1\" align=\"center\" cellpadding=\"3\">

	<tr>
		<td>編號</td>
		<td>使用者</td>
		<td>步數</td>
		<td>距離(公尺)</td>
		<td>高強度運動時間</td>
		<td>日期</td>
	</tr>
	". $table_data .
	"
</table>
";

$objPHPEXcel_PDF->writeHTML('<br>', true, false, false, false, '');
$objPHPEXcel_PDF->writeHTML($table, true, false, false, false, '');

$objPHPEXcel_PDF->Output(dirname(__FILE__).'/download/Daily '.$name.'.pdf','F');
echo "success";
//exit;
