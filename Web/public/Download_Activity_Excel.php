<?php
/**
 * PHPExcel
 *
 * Copyright (c) 2006 - 2015 PHPExcel
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * @category   PHPExcel
 * @package    PHPExcel
 * @copyright  Copyright (c) 2006 - 2015 PHPExcel (http://www.codeplex.com/PHPExcel)
 * @license    http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt	LGPL
 * @version    ##VERSION##, ##DATE##
 */

// 連結資料庫
require '../api/connect.php';
mysqli_select_db($con,"activity");
 
// query資料庫的資料 
$sql_activity="SELECT * FROM activity";
$result = mysqli_query($con,$sql_activity);

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
	$row['start_time'] = str_replace("-", "/", $row['start_time']);
	$row['end_time'] = str_replace("-", "/", $row['end_time']);
	//$objPHPExcel->getActiveSheet()->getStyle('E'.$_cnt)->getAlignment()->setWrapText(true); 
	$objPHPExcel->setActiveSheetIndex()
		->setCellValue('A'.$_cnt , $row['id'])
		->setCellValue('B'.$_cnt , $row['uid'])
		->setCellValue('C'.$_cnt , $row['step'])
		->setCellValue('D'.$_cnt , $row['bp'])
		->setCellValue('E'.$_cnt , $row['data'])
		->setCellValue('F'.$_cnt , $row['distance'])
		->setCellValue('G'.$_cnt , $row['h_i_time'])
		->setCellValue('H'.$_cnt , $row['start_time'])
		->setCellValue('I'.$_cnt , $row['end_time']);
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
            ->setCellValue('B1', '使用者編號')
            ->setCellValue('C1', '步數')
            ->setCellValue('D1', '血壓')
            ->setCellValue('E1', '血氧及心率')
            ->setCellValue('F1', '運動距離')
            ->setCellValue('G1', '高強度運動時間')
            ->setCellValue('H1', '開始時間')
            ->setCellValue('I1', '結束時間');
			
$data_number = 0 ;

// Rename worksheet
$objPHPExcel->getActiveSheet()->setTitle('Activity');

// Set active sheet index to the first sheet, so Excel opens this as the first sheet
$objPHPExcel->setActiveSheetIndex(0);

// Redirect output to a client’s web browser (Excel2007)
header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');

//***************************file name***************************************
header('Content-Disposition: attachment;filename="Activity.xlsx"');
header('Cache-Control: max-age=0');
// If you're serving to IE 9, then the following may be needed
header('Cache-Control: max-age=1');

// If you're serving to IE over SSL, then the following may be needed
header ('Expires: Mon, 26 Jul 1997 05:00:00 GMT'); // Date in the past
header ('Last-Modified: '.gmdate('D, d M Y H:i:s').' GMT'); // always modified
header ('Cache-Control: cache, must-revalidate'); // HTTP/1.1
header ('Pragma: public'); // HTTP/1.0

$objWriter = PHPExcel_IOFactory::createWriter($objPHPExcel, 'Excel2007');
$objWriter->save('php://output');
exit;
