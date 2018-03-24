<?php
//---------------------------------------------------------------------------------UserHandler
require_once('connect.php');
require_once('SimpleRest.php');
require_once('User.php');
header('Content-Type: application/json; charset=UTF-8');
class UserHandler extends SimpleRest{
	public $method, $action, $id, $input;
	//constructor
	public function __construct($method,$params,$input){
		$this->method = strtolower($method);
		$this->action = strtolower($params[1]);
		if(isset($params[2]))
			$this->id = strtolower($params[2]);
		if(isset($input))
			$this->input = $input;
	}
	function response(){
		//parsing method 
		switch($this->method){
			case 'get':
				if($this->action == 'getall'){
					$user_all = new User();
					echo $this->set_status_code($user_all->getAll());
					break;
				}
				else if($this->action == 'getbyid'){
					$user_id = new User();
					echo $this->set_status_code($user_id->getById($this->id));
					break;
				}else if($this->action == 'checkid'){
					$user_id = new User();
					echo $this->set_status_code($user_id->checkId($this->id));
					break;
				}
			case 'post':
				if($this->action == 'add'){
					$user_add = new User();
					echo $this->set_status_code($user_add->add($this->input));
					break;
				}
				else if($this->action == 'update'){
					$user_update = new User();
					echo $this->set_status_code($user_update->update($this->input));
					break;
				}
				else if($this->action == 'login'){
					$user_login = new User();
					echo $this->set_status_code($user_login->login($this->input));
					break;
				}
			case 'delete':
				if($this->action == 'delete'){
					$user_delete = new User();
					echo $this->set_status_code($user_delete->delete($this->id));
					break;
				}
			default:
				$this ->setHttpHeaders('text/html', 404);
				echo 'URL Error!';
		}
		
	}

	public function set_status_code($responseData) {
		if($responseData == 'NULL') {
			$this ->setHttpHeaders('text/html', 601);
			//直接key URL錯誤時頁面顯示提醒
			return 'Error: No data avaliable.';
		}
		else if($responseData == 'EXIST') {
			$this ->setHttpHeaders('text/html', 602);
			//直接key URL錯誤時頁面顯示提醒
			return 'Error: This account is already existence.';
		}
		else if($responseData == 'EMPTY') {
			$this ->setHttpHeaders('text/html', 603);
			//直接key URL錯誤時頁面顯示提醒
			return 'Error: Data is empty.';
		} 
		else if($responseData =='LoginFailed'){
			$this ->setHttpHeaders('text/html', 604);
			//直接key URL錯誤時頁面顯示提醒
			return 'Error: Authentication fail.';
		}
		else {
			$this ->setHttpHeaders('application/json', 200);
			//Return 正確之資料
			return $this ->encodeJson($responseData);
		}
	}

	public function encodeJson($responseData) {
		$jsonResponse = json_encode($responseData);
		return $jsonResponse;		
	}
	
	public function encodeXml($responseData) {
		// 创建 SimpleXMLElement 对象
		$xml = new SimpleXMLElement('<?xml version="1.0"?><site></site>');
		foreach($responseData as $key=>$value) {
			$xml->addChild($key, $value);
		}
		return $xml->asXML();
	}
}

?>