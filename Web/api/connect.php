<?php

//連結資料庫 (hostname,username,password,database name)
$con = mysqli_connect('localhost','root','mitlab','copd');
if (!$con) {
    die('Could not connect: ' . mysqli_error($con));
}

?>