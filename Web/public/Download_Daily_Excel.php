<?php
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

$_cnt = 1;

/** Error reporting */
error_reporting(E_ALL);
ini_set('display_errors', TRUE);
ini_set('display_startup_errors', TRUE);
date_default_timezone_set('Europe/London');

if (PHP_SAPI == 'cli')
	die('This example should only be run from a Web Browser');

/** Include PHPExcel */
require_once ('../PHPExcel-1.8/Classes/PHPExcel.php');

// Create new PHPExcel object
$objPHPExcel = new PHPExcel();

// 將資料庫的資料全部存在student這個陣列
while($row = mysqli_fetch_array($result)) {
	$_cnt++;
	$objPHPExcel->setActiveSheetIndex()
		->setCellValue('A'.$_cnt , $row['id'])
		->setCellValue('B'.$_cnt , $row['uid'])
		->setCellValue('C'.$_cnt , $row['step'])
		->setCellValue('D'.$_cnt , $row['date'])
		->setCellValue('E'.$_cnt , $row['distance'])
		->setCellValue('F'.$_cnt , $row['h_i_time'])
		->setCellValue('G'.$_cnt , $row['updatetime']);
}

// Set document properties
$objPHPExcel->getProperties()->setCreator("Maarten Balliauw")
							 ->setLastModifiedBy("Maarten Balliauw")
							 ->setTitle("Office 2007 XLSX Test Document")
							 ->setSubject("Office 2007 XLSX Test Document")
							 ->setDescription("Test document for Office 2007 XLSX, generated using PHP classes.")
							 ->setKeywords("office 2007 openxml php")
							 ->setCategory("Test result file");

// Add some data
$objPHPExcel->setActiveSheetIndex(0)
            ->setCellValue('A1', '編號')
            ->setCellValue('B1', '使用者')
            ->setCellValue('C1', '步數')
            ->setCellValue('D1', '日期')
            ->setCellValue('E1', '距離(公尺)')
            ->setCellValue('F1', '高強度運動時間')
            ->setCellValue('G1', '更新時間');
			
// Rename worksheet
$objPHPExcel->getActiveSheet()->setTitle('Environment');

// Set active sheet index to the first sheet, so Excel opens this as the first sheet
$objPHPExcel->setActiveSheetIndex(0);

// Redirect output to a client’s web browser (Excel2007)
//header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');

header('Content-Type:text/html');
$objWriter = PHPExcel_IOFactory::createWriter($objPHPExcel, 'Excel2007');
$objWriter->save(dirname(__FILE__).'/download/Daily '.$name.'.xlsx');
echo "success";
exit;

//***************************file name***************************************
//header('Content-Disposition: attachment;filename="Environment '.$name.'.xlsx"');
//header('Cache-Control: max-age=0');

// If you're serving to IE 9, then the following may be needed
//header('Cache-Control: max-age=1');

// If you're serving to IE over SSL, then the following may be needed
/*
header ('Expires: Mon, 26 Jul 1997 05:00:00 GMT'); // Date in the past
header ('Last-Modified: '.gmdate('D, d M Y H:i:s').' GMT'); // always modified
header ('Cache-Control: cache, must-revalidate'); // HTTP/1.1
header ('Pragma: public'); // HTTP/1.0
*/

//$objWriter->save('php://output');
