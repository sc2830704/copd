<?php
//require handler
require_once('UserHandler.php');
require_once('EnvHandler.php');
require_once('ActivityHandler.php');
require_once('DailyHandler.php');
require_once('AdminHandler.php');
require_once('EvaluateHandler.php');
$method = $_SERVER['REQUEST_METHOD'];
$params = explode('/', trim($_SERVER['PATH_INFO'],'/'));
$controller = $params[0];
$input=$_REQUEST;
if(empty($input)){
	$input = json_decode(file_get_contents('php://input'),true);
}
$queryStr = $_SERVER['QUERY_STRING'];

 switch ($controller){
	 // route[/api/user]
	 case 'user':
		//to-do handler
	 	$userHandler = new UserHandler($method,$params,$input);
	 	$userHandler->response();
		break;
	// route[/api/task]
	case 'env':
	 	//to-do handler
		$envHandler = new EnvHandler($method,$params,$input);
	 	$envHandler->response();
		break;
	case 'activity':
		//to-do handler
		$activityHandler = new ActivityHandler($method,$params,$input);
	 	$activityHandler->response();
		break;
	case 'daily':
		//to-do handler
		$dailyHandler = new DailyHandler($method,$params,$input);
	 	$dailyHandler->response();
		break;
	case 'admin':
		$adminHandler = new AdminHandler($method,$params,$input);
		$adminHandler->response();
		break;
	case 'evaluate':
		$evaluateHandler = new EvaluateHandler($method,$params,$input);
		$evaluateHandler->response();
		break;
	default:
		header("http/ 404");
		echo 'URL Error!';
 }

?>