<?php
if($_SERVER["HTTPS"] != "on")
{
    header("Location: https://" . $_SERVER["HTTP_HOST"] . $_SERVER["REQUEST_URI"]);
    exit();
}
session_start();
if(empty($_SESSION['account'])){
  header("Location: index.php"); 
}
else{
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
  <title>COPD Manage System</title>
  <!-- Bootstrap core CSS-->
  <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <!-- Custom fonts for this template-->
  <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
  <!-- Page level plugin CSS-->
  <!--   <link href="vendor/datatables/dataTables.bootstrap4.css" rel="stylesheet">   -->
  <!-- Custom styles for this template-->
  <link href="css/sb-admin.css" rel="stylesheet">

  <!-- Bootstrap core JavaScript-->
  <script src="vendor/jquery/jquery.min.js"></script>
  <script src="vendor/popper/popper.min.js"></script>
  <!-- Page level plugin JavaScript-->
  <script src="vendor/chart.js/Chart.min.js"></script>
  <!-- <script src="vendor/datatables/jquery.dataTables.js"></script>-->
  <script src="vendor/datatables/dataTables.bootstrap4.js"></script> 
  <!-- Custom scripts for this page-->
  <script src="js/sb-admin-datatables.min.js"></script>

  <script src="js/jquery-1.11.3.min.js"></script>
  <link rel="stylesheet" href="css\myStyle.css">
  <link rel="stylesheet" href="..\DataTables\DataTables-1.10.16\css\jquery.dataTables.min.css">
  <script type="text/JavaScript" src="..\DataTables\DataTables-1.10.16\js\jquery.dataTables.min.js"></script>

  <!-- DataTable for Mobile -->
  <script src="js/rowReorder.min.js"></script>
  <script src="js/responsive.min.js"></script>
  <link rel="stylesheet" href="css\responsive.dataTables.min.css">
  <link rel="stylesheet" href="css\rowReorder.dataTables.min.css">
</head>


<script type="text/JavaScript">
  $(document).ready(function(){
    var patientDataTable = $('#patientTable').DataTable({
      rowReorder: {
        selector: 'td:nth-child(2)'
      },
      responsive: true
    });
    getPatientData();
    
    $("#NewPatient").click(function(){
      clear_add_table();
    })
    $("#patient_close").click(function(){
      $("#PatientManage").hide();
    })
    $("#patient_save").click(function() {
      post_data('add');
    })
    $("#patient_update").click(function() {
      post_data('update');
    })
    $("#patient_delete").click(function() {
      delete_data();
    })
    $('#patientTable').on('click', 'tr', function(){
      var row = $(this).children('td:first-child').text();
      row==''? '':click_row(row);
    });
    /*$(document).on("click", "tr[class='odd'],tr[class='even']", function(){
      var row = $(this).children('td:first-child').text();
      click_row(row);
    })*/
  });

  function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  } 

  function getPatientData() {
    $.ajax({
      type : 'GET',
      url  : '../apiv1/user/getall',
      dataType: 'json',
      cache: false,
      success :  function(result) {
        console.log(result); 
        LoadPatientDataToTable(result);
      }
    });
  }

  function LoadPatientDataToTable(patientData) {
    var patientDataTable = $('#patientTable').DataTable();
    patientDataTable.clear().draw(false);
    for (var i in patientData){
      patientDataTable.row.add([
        patientData[i].id,
        patientData[i].fname,
        patientData[i].lname,
        patientData[i].age,
        patientData[i].sex==1?'男':(patientData[i].sex==0?'女':null),
        patientData[i].bmi,
        patientData[i].env_id,
        patientData[i].ble_id,
        patientData[i].watch_id
      ]).draw(false);
    }
    patientDataTable.columns.adjust().draw();
  }

  function delete_data() {
    $.ajax({
      type: "DELETE",
      url: "../apiv1/user/delete/" + $("#updateid").val(),
      dataType: "json",
      data: {                
      },
      success: function(data) {      
        getPatientData();         
        $("#PatientManage").hide(); 
      },
      error: function(jqXHR) {
        alert("發生錯誤: " + jqXHR.status + ' ' + jqXHR.statusText);
      }
    })
  }

  function post_data($action){
    get_medicine();
    get_case();
    $.ajax({
      type: "POST",
      url: "../apiv1/user/" + $action,
      dataType: "json",
      data: {
        id: $("#" + $action + "id").val(),
        pwd: $("#pwd").val(),
        fname: $("#fname").val(),
        lname: $("#lname").val(),
        age: $("#age").val(),
        sex: $("#sex").val(),
        bmi: $("#bmi").val(),
        history: $("#history").val(),
        drug: $("#drug").val(),
        env_id: $("#env_id").val(),
        ble_id: $("#ble_id").val(),
        watch_id: $("#watch_id").val(),
        drug_other: $("#drug_other").val(),
        history_other: $("#history_other").val()
      },
      success: function(data) { 
        getPatientData();
        $("#PatientManage").hide(); 
      },
      error: function(jqXHR) {
        console.log(jqXHR);
        alert("發生錯誤: " + jqXHR.status + ' ' + jqXHR.statusText);
      }
    })
  }

  function get_medicine(){
    //var medicine_other = [];
    var medicine_selected=[];

    /*var medicine_reg= $("#drug_other").val().replace(/,/g,"\",\"") + '';
    if(medicine_reg==''){
      document.getElementById('drug_other_post').value = '[""]';
    }
    else{
      medicine_other = ['["' + medicine_reg + '"]'];
      console.log(medicine_other);
      document.getElementById('drug_other_post').value = medicine_other;
    }*/

    $("[name=medicine]:checkbox:checked").each(function(){
      medicine_selected.push($(this).val());
    });
    medicine_selected = medicine_selected + '';
    if(medicine_selected==''){
    	document.getElementById('drug').value = '[]';
    }
    else{
      //console.log(medicine_selected);
	    var drug_arr = medicine_selected.split(',');
      //console.log(drug_arr);
      drug_arr = JSON.stringify(drug_arr);
      //console.log(drug_arr);
	    document.getElementById('drug').value = drug_arr ;
    }
  }
  function get_case(){
    //var case_other = [];
    var case_selected=[];

    /*var case_reg= $("#history_other").val().replace(/,/g,"\",\"") + '';
    if(case_reg==''){
      document.getElementById('history_other_post').value = '["None"]';
    }
    else{
      case_other = ['["' + case_reg + '"]'];
      console.log(case_other);
      document.getElementById('history_other_post').value = case_other;
    }*/

    $("[name=case]:checkbox:checked").each(function(){
      case_selected.push($(this).val());
    });
    case_selected = case_selected + '';
    if(case_selected==''){
     	document.getElementById('history').value = '[]';
    }
    else{
        var history_arr = case_selected.split(',');
        history_arr = JSON.stringify(history_arr);
     	document.getElementById('history').value = history_arr ;
    }
  }

  function click_row(row){
    //topFunction();
    $.ajax({
      type: "GET",
      url: "../apiv1/user/getbyid/" + row,
      dataType: "json",
      data: {
      },
      success: function(data) {
        document.getElementById('updateid').value = data.id;
        document.getElementById('pwd').value = data.pwd;
        document.getElementById('fname').value = data.fname;
        document.getElementById('lname').value = data.lname;
        document.getElementById('age').value = data.age;
        document.getElementById('sex').value = data.sex;
        document.getElementById('bmi').value = data.bmi;
        document.getElementById('env_id').value = data.env_id;
        document.getElementById('ble_id').value = data.ble_id;
        document.getElementById('watch_id').value = data.watch_id;
        data.history = JSON.parse(data.history);
        data.drug = JSON.parse(data.drug);
        //data.drug_other = JSON.parse(data.drug_other);
        //data.history_other = JSON.parse(data.history_other);
        document.getElementById('drug_other').value = data.drug_other;
        document.getElementById('history_other').value = data.history_other;
        
        $("input[name='medicine']").prop("checked", false);
        $("input[name='case']").prop("checked", false);
        for(var i in data.history){
          $("input[value='" + data.history[i] + "']").prop("checked", true);
        }
        for(var i in data.drug){
          $("input[value='" + data.drug[i] + "']").prop("checked", true);
        }
        
      },
      error: function(jqXHR) {
          alert("發生錯誤: " + jqXHR.status + ' ' + jqXHR.statusText);
      }
    })
    $("#patient_delete").show();
    $("#patient_update").show();
    $("#patient_save").hide();
    $("#patient_close").show();
    $("#newid_label").hide();
    $("#pid_label").show();
    $("#addid").hide();
    $("#updateid").show();
    $("#PatientManage").show();
  }
  

  function clear_add_table(){
    document.getElementById('updateid').value = '';
    document.getElementById('addid').value = '';
    document.getElementById('pwd').value = '';
    document.getElementById('fname').value = '';
    document.getElementById('lname').value = '';
    document.getElementById('age').value = '';
    document.getElementById('sex').value = '';
    document.getElementById('bmi').value = '';
    document.getElementById('env_id').value = '';
    document.getElementById('ble_id').value = '';
    document.getElementById('watch_id').value = '';
    document.getElementById('drug_other').value = '';
    document.getElementById('history_other').value = '';
    $("input[name='medicine']").prop("checked", false);
    $("input[name='case']").prop("checked", false);
    $("#patient_delete").hide();
    $("#patient_update").hide();
    $("#patient_save").show();
    $("#patient_close").show();
    $("#newid_label").show();
    $("#pid_label").hide();
    $("#addid").show();
    $("#updateid").hide();
    $("#PatientManage").show();
  }

</script>

<body class="fixed-nav sticky-footer bg-dark" id="page-top">
  <!-- Navigation-->
  <?php require('module.php'); ?>

  <!-- center -->
  <div class="content-wrapper">
    <div class="container-fluid">
      <div class="col-xs-12 col-md-12 col-lg-12" align="right">
            <button onclick="window.location.href='Download_Patient_PDF.php'">PDF</button>
            <button onclick="window.location.href='Download_Patient_Excel.php'">EXCEL</button>&nbsp;
            &nbsp;<button id="NewPatient">新增資料</button>
      </div>
      <br>
	    <!-- /.container-fluid-->
      <!-- PatientManage-->
      <div class="row" id="PatientManage" style="display: none; padding: 0px 10px 0px 10px;">
        <div class="col-xs-12 col-md-12 col-lg-4">
          <div class="card">
            <div class="card-header">
              <i class="fa fa-pencil-square-o"></i>&nbsp;&nbsp;病患資料表
            </div>
            <div class="card-body" align="right" style="margin: auto">
              <label for="fname">姓氏：</label>
              <input type="text" id="fname"> <br>

              <label for="lname">名字：</label>
              <input type="text" id="lname"> <br>

              <label for="age">年齡：</label>
              <input type="text" id="age"> <br>

              <label for="sex">性別：</label>
              <select id="sex" style="width: 72%;">
                <option value="1">男</option>
                <option value="0">女</option>
              </select> <br>

              <label for="updateid" id="pid_label">帳號：</label>
              <input type="text" id="updateid" disabled>

              <label for="addid" id="newid_label">帳號：</label>
              <input type="text" id="addid"> <br>

              <label for="pwd">密碼：</label>
              <input type="password" id="pwd"> <br>

              <label for="bmi">BMI：</label>
              <input type="text" id="bmi"> <br>

              <label for="env_id">環境ID：</label>
              <input type="text" id="env_id"> <br>
              
              <label for="ble_id">藍芽ID：</label>
              <input type="text" id="ble_id"> <br>

              <label for="watch_id">手錶ID：</label>
              <input type="text" id="watch_id"> <br><br>
          
              <h6 style="color: red;">[備註] 帳號若包含英文字母需小寫</h6>
              <h6 style="color: red;">     (輸入大寫會自動轉為小寫)</h6>
            </div>
            <div class="card-footer"></div>
          </div>
        </div>
        <div class="col-xs-12 col-md-12 col-lg-5">
          <div class="card">
            <div class="card-header">
              <i class="fa fa-list-ol"></i>&nbsp;&nbsp;藥物勾選清單
            </div>
            <div class="card-body" align="left" style="margin: auto">
              <h6>吸入型藥物：</h6>
              <input name="medicine" type="checkbox" value="Berotec"> 備勞喘噴霧劑：(Berotec, Fenoterol)<br>
              <input name="medicine" type="checkbox" value="BerodualN"> 備喘全噴霧劑：(Berodual N, Fenoterol + Ipratropium)<br>
              <input name="medicine" type="checkbox" value="Combivent"> 冠喘衛噴霧劑：(Combivent, Salbutamol + Ipratropium)<br>
              <input name="medicine" type="checkbox" value="Spiriva"> 適喘樂吸入劑：(Spiriva, Tiotropium)<br>
              <input name="medicine" type="checkbox" value="Atrovent"> 定喘樂吸入劑：(Atrovent Nebuliser Soln, Ipratropium)<br>
              <input name="medicine" type="checkbox" value="Seretide"> 使肺泰乾粉吸入劑：(Seretide, Fluticasone propionate + Salmeterol)<br>
              <br>
              <h6>口服型類固醇：</h6>
              <input name="medicine" type="checkbox" value="Prednisone"> 強的松/去氫可的松 (Prednisone)<br>
              <input name="medicine" type="checkbox" value="Donison"> 康速龍 (Donison, Prednisone)<br>
              <input name="medicine" type="checkbox" value="Methylprednisolone"> 甲基培尼皮質醇 (Methylprednisolone)<br>
              <input name="medicine" type="checkbox" value="Hydrocortisone"> 氫化可體松 (Hydrocortisone)<br>
              <input name="medicine" type="checkbox" value="Dexamethasone"> 地塞米松 (Dexamethasone)<br>
              <br>
              <h6>其它藥物：</h6>
              <textarea type="text" id="drug_other" rows="3" cols="25"></textarea>
            </div>
            <div class="card-footer"></div>
          </div>
        </div>
        <div class="col-xs-12 col-md-12 col-lg-3">
          <div class="card">
            <div class="card-header">
              <i class="fa fa-heartbeat"></i>&nbsp;&nbsp;疾病史
            </div>
            <div class="card-body" align="left" style="margin: auto">
              <input name="case" type="checkbox" value="HeartDisease"> 心臟病<br>
              <input name="case" type="checkbox" value="Hypertension"> 高血壓<br>
              <input name="case" type="checkbox" value="Diabetes"> 糖尿病<br>
              <input name="case" type="checkbox" value="Arrhythmia"> 心律不整<br>
              <input name="case" type="checkbox" value="HeartFailure"> 心衰竭<br>
              <input name="case" type="checkbox" value="Stroke"> 中風<br><br>
              <h6>其他疾病：</h6>
              <textarea type="text" id="history_other" rows="5" cols="25"></textarea>
              <textarea type="text" id="history" rows="3" cols="40" style="display: none;"></textarea>
              <textarea type="text" id="drug" rows="3" cols="40" style="display: none;"></textarea>
              <div align="right" >
                <h5 id="patient_Result" align="right" style="color:red"></h5>&nbsp;&nbsp;
                <button  class="button2" id="patient_save">儲存</button>
                <button  class="button2" id="patient_delete">刪除</button>
                <button  class="button2" id="patient_update">更新</button>
                <button  class="button2" id="patient_close">取消</button>
              </div>
            </div>
            <div class="card-footer"></div>
          </div>
        </div>
      </div>
      <br>
      <!-- PatientDataTable-->
      <div id="datatable_patient_visible" style="width: 98%; margin: auto;">
        <table id="patientTable" class="display" cellspacing="0" width="100%" >
          <thead>
            <tr>
              <th>帳號</th>
              <th>姓氏</th>
              <th>名字</th>
              <th>年齡</th>
              <th>性別</th>
              <th>BMI</th>
              <th>環境ID</th>
              <th>藍芽ID</th>
              <th>手錶ID</th>
            </tr>
          </thead>
        </table>
      </div>
      <!-- /.content-wrapper-->
      <!-- Logout Button + Footer -->
      <?php require('footer_and_logout.php'); ?>
    </div>
  </div>
  <!-- 左邊縮排需要的.js -->
  <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
  <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
  <script src="js/sb-admin.min.js"></script>
</body>

</html>
