<?php
//-------------------------------------------------------------------------------DailyHandler
require_once('connect.php');
require_once('SimpleRest.php');
require_once('Daily.php');
class DailyHandler extends SimpleRest{
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
					$daily_all = new Daily();
					$this ->setHttpHeaders('application/json', 200);
					echo $this->encodeJson($daily_all->getAll());
					break;
				}
				else if($this->action == 'getbyuser'){
					$daily_uid = new Daily();
					$this ->setHttpHeaders('application/json', 200);
					echo $this->encodeJson($daily_uid->getByUser($this->id));
					break;
				}
			case 'post':
				$daily_add = new Daily();
				$this ->setHttpHeaders('application/json', 200);
				echo $this->encodeJson($daily_add->add($this->input));
				//echo 'add success';
				break;
			case 'delete':
				$daily_delete = new Daily();
				$this ->setHttpHeaders('application/json', 200);
				echo $this->encodeJson($daily_delete->deletebyid($this->id));
				//echo 'delete success';
				break;
			
			
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