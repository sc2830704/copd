<?php
//------------------------------------------------------------------------------------EnvHandler
require_once('connect.php');
require_once('SimpleRest.php');
require_once('Evaluate.php');
class EvaluateHandler extends SimpleRest{
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
					$evaluate_all = new Evaluate();
					echo $this->set_status_code($evaluate_all->getAll());
					break;
				}
				else if($this->action == 'getbyid'){
					$evaluate_id = new Evaluate();
					echo $this->set_status_code($evaluate_id->getById($this->id));
					break;
				}
			case 'post':
				if($this->action == 'add'){
					$evaluate_add = new Evaluate();
					echo $this->set_status_code($evaluate_add->add($this->input));
					break;
				}
			default:
				$this ->setHttpHeaders('text/html', 404);
				echo 'URL Error!';
		}
	}

	public function encodeJson($responseData) {
		$jsonResponse = json_encode($responseData);
		return $jsonResponse;		
	}
	
	public function set_status_code($responseData) {
		if($responseData == 'NULL') {
			$this ->setHttpHeaders('text/html', 601);
			return 'Error: No data avaliable.';
		}
		else if($responseData == 'EXIST') {
			$this ->setHttpHeaders('text/html', 602);
			return 'Error: This account is already existence.';
		}
		else if($responseData == 'EMPTY') {
			$this ->setHttpHeaders('text/html', 603);
			return 'Error: Data is empty.';
		}
		else {
			$this ->setHttpHeaders('application/json', 200);
			return $this ->encodeJson($responseData);
		}
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