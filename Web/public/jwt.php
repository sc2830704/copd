<?php
	$header = [
		"alg" => "HS256",
		"typ" => "JWT"
	];
	$header = base64url_encode(json_encode($header));
	//echo $header;

	$payload = [
		"iss" => "andyyou.github.io",
	  	"exp" => 1465700328092,
	  	"name" => "andyyou",
	  	"admin" => true
	];
	$payload = base64url_encode(json_encode($payload));
	//echo $payload;

	$key = 'secret';
	$signature = base64url_encode(hash_hmac('sha256', $header.'.'.$payload, $key, true));
	//echo $signature."<br>";
	//echo $header.'.'.$payload."<br>";

	$token = $header.'.'.$payload.'.'.$signature;
	echo $token;

	function base64url_encode($data) { 
  		return rtrim(strtr(base64_encode($data), '+/', '-_'), '='); 
	} 
	function base64url_decode($data) { 
	  	return base64_decode(str_pad(strtr($data, '-_', '+/'), strlen($data) % 4, '=', STR_PAD_RIGHT)); 
	} 
?>