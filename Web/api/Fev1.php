<?php
class Fev1{
    function getAll(){
        //connet db
        require 'connect.php';
        mysqli_select_db($con,"fev1");
        //query data by method
        $getAll_sql = "SELECT fev1.index, fev1.id, user.fname, user.lname, fev1.fev1, fev1.testing_time FROM fev1, user WHERE fev1.id = user.id ";
//        $getAll_sql = "SELECT * FROM fev1";
        $getAll_result = mysqli_query($con,$getAll_sql);

        $getAll_dataArray = array();

        if(mysqli_num_rows($getAll_result) == 0) {
            return 'NULL';
        }
        else {
            while ($row = mysqli_fetch_array($getAll_result,MYSQLI_ASSOC)) {
                $getAll_dataArray[] = $row;
            }
            return $getAll_dataArray;
        }
    }
    function getById($id){
        // connet db
        require 'connect.php';
        mysqli_select_db($con,"fev1");

        //query data by method
        $getById_sql = "SELECT fev1.index, fev1.id, fev1.fev1, fev1.testing_time, user.fname, user.lname FROM fev1,user WHERE fev1.id = '$id' AND user.id = '$id'";
        $getById_result = mysqli_query($con,$getById_sql);

        if(mysqli_num_rows($getById_result) == 0) {
            return 'NULL';
        }
        else {
            $getById_dataArray = mysqli_fetch_array($getById_result,MYSQLI_ASSOC);
            return $getById_dataArray;
        }
    }

    function add($input){
        //connet db
        require 'connect.php';
        mysqli_select_db($con,"fev1");

        $id = $input['id'];
        $fev1 = $input['fev1'];

        if(!isset($id)||empty($id)||!isset($fev1)){
            return 'EMPTY';
        }
        else {
            $sql_insert = "INSERT INTO fev1 (id,fev1) VALUES ('$id','$fev1')";
            mysqli_query($con,$sql_insert);
            return 'ok';
        }
    }
}
?>