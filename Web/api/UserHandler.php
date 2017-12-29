<?php
//---------------------------------------------------------------------------------UserHandler
require_once('connect.php');
require_once('SimpleRest.php');
require_once('User.php');
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
					$this ->setHttpHeaders('application/json', 200);
					echo $this->encodeJson($user_all->getAll());
					break;
				}
				else if($this->action == 'getbyid'){
					$user_id = new User();
					$this ->setHttpHeaders('application/json', 200);
					echo $this->encodeJson($user_id->getById($this->id));
					break;
				}
			case 'post':
				if($this->action == 'add'){
					$user_add = new User();
					$this ->setHttpHeaders('application/json', 200);
					echo $this->encodeJson($user_add->add());
					//echo 'add success';
					break;
				}
				//------------------------UPDATE------------------------
				else if($this->action == 'update'){
					$user_update = new User();
					$this ->setHttpHeaders('application/json', 200);
					echo $this->encodeJson($user_update->update());
					//echo 'add success';
					break;
				}
			case 'delete':
				$user_delete = new User();
				$this ->setHttpHeaders('application/json', 200);
				echo $this->encodeJson($user_delete->delete($this->id));
				//echo 'delete success';
				break;
			/*
			case 'put':
				$user_update = new User();
				$this ->setHttpHeaders('application/json', 200);
				echo $this->encodeJson($user_update->update($this->input));
				//echo 'update success';
				break;
			*/
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