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
require 'connect.php';
mysqli_select_db($con,"user");
 
// query資料庫的資料 
$sql_user="SELECT * FROM user";
$result = mysqli_query($con,$sql_user);

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
	if($row['sex']==0){
		$sex_chinese = '女';
	}
	else {
		$sex_chinese = '男';
	}
	$objPHPExcel->setActiveSheetIndex()
		->setCellValue('A'.$_cnt , $row['id'])
		->setCellValue('B'.$_cnt , $row['fname'])
		->setCellValue('C'.$_cnt , $row['lname'])
		->setCellValue('D'.$_cnt , $sex_chinese)
		->setCellValue('E'.$_cnt , $row['bmi'])
		->setCellValue('F'.$_cnt , $row['history'])
		->setCellValue('G'.$_cnt , $row['drug'])
		->setCellValue('H'.$_cnt , $row['env_id'])
		->setCellValue('I'.$_cnt , $row['ble_id'])
		->setCellValue('J'.$_cnt , $row['watch_id']);
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
            ->setCellValue('A1', '帳號')
            ->setCellValue('B1', '名字')
            ->setCellValue('C1', '姓氏')
            ->setCellValue('D1', '性別')
            ->setCellValue('E1', 'BMI')
            ->setCellValue('F1', '病例')
            ->setCellValue('G1', '藥物')
            ->setCellValue('H1', 'Env_ID')
            ->setCellValue('I1', 'BLE_ID')
            ->setCellValue('J1', 'Watch_ID');
			
$data_number = 0 ;

// Rename worksheet
$objPHPExcel->getActiveSheet()->setTitle('Patient');

// Set active sheet index to the first sheet, so Excel opens this as the first sheet
$objPHPExcel->setActiveSheetIndex(0);

// Redirect output to a client’s web browser (Excel2007)
header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');

//***************************file name***************************************
header('Content-Disposition: attachment;filename="Patient.xlsx"');
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
