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
</head>

<script src="js/jquery-1.11.3.min.js"></script>
<link rel="stylesheet" href="css\myStyle.css">
<link rel="stylesheet" href="..\DataTables\DataTables-1.10.16\css\jquery.dataTables.min.css">
<script type="text/JavaScript" src="..\DataTables\DataTables-1.10.16\js\jquery.dataTables.min.js"></script>

<script type="text/JavaScript">

  $(document).ready(function(){
    var patientDataTable = $('#patientTable').DataTable();
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
    $(document).on("click", "tr[class='odd'],tr[class='even']", function(){
      var row = $(this).children('td:first-child').text();
      click_row(row);
    })
  });

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
        //patientData[i].drug,
        //patientData[i].history,
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
        drug_other: $("#drug_other_post").val(),
        history_other: $("#history_other_post").val()
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
    var medicine_other = [];
    var medicine_selected=[];

    var medicine_reg= $("#drug_other").val().replace(/,/g,"\",\"") + '';
    if(medicine_reg==''){
      document.getElementById('drug_other_post').value = '["None"]';
    }
    else{
      medicine_other = ['["' + medicine_reg + '"]'];
      console.log(medicine_other);
      document.getElementById('drug_other_post').value = medicine_other;
    }

    $("[name=medicine]:checkbox:checked").each(function(){
      medicine_selected.push($(this).val());
    });
    medicine_selected = medicine_selected + '';
    if(medicine_selected==''){
    	document.getElementById('drug').value = '["None"]';
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
    var case_other = [];
    var case_selected=[];

    var case_reg= $("#history_other").val().replace(/,/g,"\",\"") + '';
    if(case_reg==''){
      document.getElementById('history_other_post').value = '["None"]';
    }
    else{
      case_other = ['["' + case_reg + '"]'];
      console.log(case_other);
      document.getElementById('history_other_post').value = case_other;
    }

    $("[name=case]:checkbox:checked").each(function(){
      case_selected.push($(this).val());
    });
    case_selected = case_selected + '';
    if(case_selected==''){
     	document.getElementById('history').value = '["None"]';
    }
    else{
        var history_arr = case_selected.split(',');
        history_arr = JSON.stringify(history_arr);
     	document.getElementById('history').value = history_arr ;
    }
  }

  function click_row(row){
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
        data.drug_other = JSON.parse(data.drug_other);
        data.history_other = JSON.parse(data.history_other);
        document.getElementById('drug_other').value = data.drug_other;
        document.getElementById('history_other').value = data.history_other;
        //var history_arr = data.history.split(',');
        //var drug_arr = data.drug.split(',');
        
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
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" id="mainNav">
    <a class="navbar-brand" href="homepage.php">Welcome COPD Manage System</a>
    <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarResponsive">
      <ul class="navbar-nav navbar-sidenav" id="exampleAccordion">
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="HomePage">
          <a class="nav-link" href="homepage.php">
            <i class="fa fa-fw fa-windows"></i>
            <span class="nav-link-text">COPD首頁</span>
          </a>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Patient">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseComponents" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-child"></i>
            <span class="nav-link-text" id="test">病患資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseComponents">
            <li>
              <a href="PatientPage.php">Patient</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Environment">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseExamplePages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-bank"></i>
            <span class="nav-link-text" id="test">環境資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseExamplePages">
            <li>
              <a href="EnvironmentPage.php">Environment</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Daily">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseDailyPages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">每日統計</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseDailyPages">
            <li>
              <a href="DailyPage.php">Daily</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Activity">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseActivityPages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-bar-chart-o"></i>
            <span class="nav-link-text" id="test">活動紀錄</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseActivityPages">
            <li>
              <a href="ActivityPage.php">Activity</a>
            </li>
          </ul>
        </li>
      </ul>
      <ul class="navbar-nav sidenav-toggler">
        <li class="nav-item">
          <a class="nav-link text-center" id="sidenavToggler">
            <i class="fa fa-fw fa-angle-left"></i>
          </a>
        </li>
      </ul>
      <ul class="navbar-nav ml-auto">
       <text align="text-center" style="margin: auto; color:yellow; padding-right: 10px" id="login_msg">Hello! <?php echo $_SESSION['account'] ?></text>
        <li class="nav-item">
          <a class="nav-link" data-toggle="modal" data-target="#exampleModal">
            <i class="fa fa-fw fa-sign-out"></i>Logout</a>
        </li>
      </ul>
    </div>
  </nav>


<!-- center   -->

  <div class="content-wrapper" style="padding-left: 5px">
    <div class="container-fluid">
        <div class="download_table" align="right">
              <button onclick="window.location.href='Download_Patient_PDF.php'">PDF 下載</button>
              <button onclick="window.location.href='Download_Patient_Excel.php'">EXCEL 下載</button>&nbsp;
              &nbsp;<button id="NewPatient">新增資料</button>
        </div>
	    <!-- /.container-fluid-->
	    <!-- Download Page -->
	    <!-- PatientManage-->
      <div class="edit_table" id="PatientManage" style="display:none; width: 100%">
        <div class="row">
          <div class="column" align="right" style="padding: 0px 10px 0px 5px; width: 25%;"><br>
              <h5 align="left" style="font-weight: bold; padding-left: 20px;">病患資料表</h5>
              <label for="fname">姓氏：</label>
              <input type="text" id="fname"> <br>

              <label for="lname">名字：</label>
              <input type="text" id="lname"> <br>

              <label for="age">年齡：</label>
              <input type="text" id="age"> <br>

              <label for="sex">性別：</label>
              <select id="sex" style="width: 173px;">
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
          <div class="column" align="left" style="padding: 0px 10px 0px 5px; width: 50%">
          <br>
            <div style="padding: 0px 10px 0px 40px;">
              <h5 style="font-weight: bold;">藥物勾選清單</h5>

              <h6>吸入型藥物：</h6>
              <input name="medicine" type="checkbox" value="Berotec"> 備勞喘噴霧劑：(Berotec, Fenoterol)<br>
              <input name="medicine" type="checkbox" value="BerodualN"> 備喘全噴霧劑：(Berodual N, Fenoterol + Ipratropium)<br>
              <input name="medicine" type="checkbox" value="Combivent"> 冠喘衛噴霧劑：(Combivent, Salbutamol + Ipratropium)<br>
              <input name="medicine" type="checkbox" value="Seretide"> 使肺泰乾粉吸入劑：(Seretide, Fluticasone propionate + Salmeterol)<br>
              <input name="medicine" type="checkbox" value="Spiriva"> 適喘樂吸入劑：(Spiriva, Tiotropium)<br>
              <input name="medicine" type="checkbox" value="Atrovent"> 定喘樂吸入劑：(Atrovent Nebuliser Soln, Ipratropium)<br>
              <br>
              <h6>口服型類固醇：</h6>
              <input name="medicine" type="checkbox" value="Prednisone"> 強的松/去氫可的松 (Prednisone)<br>
              <input name="medicine" type="checkbox" value="Donison"> 康速龍 (Donison, Prednisone)<br>
              <input name="medicine" type="checkbox" value="Methylprednisolone"> 甲基培尼皮質醇 (Methylprednisolone)<br>
              <input name="medicine" type="checkbox" value="Hydrocortisone"> 氫化可體松 (Hydrocortisone)<br>
              <input name="medicine" type="checkbox" value="Dexamethasone"> 地塞米松 (Dexamethasone)<br>
              <br>
              <h6>其它藥物：（不同藥物請用,分開）</h6>
              <textarea type="text" id="drug_other" rows="3" cols="40"></textarea>
              <br>
            </div>
          </div>
          <div class="column" align="right" style="padding: 0px 10px 0px 5px; width: 25%;">
          <br>
            <div style="padding: 0px 10px 0px 40px;" align="left">
              <h5 style="font-weight: bold;">疾病史</h5>
            
              <input name="case" type="checkbox" value="HeartDisease"> 心臟病<br>
              <input name="case" type="checkbox" value="Hypertension"> 高血壓<br>
              <input name="case" type="checkbox" value="Diabetes"> 糖尿病<br>
              <input name="case" type="checkbox" value="Arrhythmia"> 心律不整<br>
              <input name="case" type="checkbox" value="HeartFailure"> 心衰竭<br>
              <input name="case" type="checkbox" value="Stroke"> 中風<br><br>
              <h6>其他疾病：（不同疾病請用,分開）</h6>
              <textarea type="text" id="history_other" rows="7" cols="25"></textarea>
            </div>
            <br><br>
            <div align="right" >
              <button  class="button2" id="patient_save">儲存</button>
              <button  class="button2" id="patient_delete">刪除</button>
              <button  class="button2" id="patient_update">更新</button>
              <button  class="button2" id="patient_close">取消</button>
            </div>
          </div>
        </div>
        <div class="row" align="right">
          <textarea type="text" id="history" rows="3" cols="40" style="display: none;"></textarea>
          <textarea type="text" id="drug" rows="3" cols="40" style="display: none;"></textarea>
          <textarea type="text" id="history_other_post" rows="3" cols="40" style="display: none;"></textarea>
          <textarea type="text" id="drug_other_post" rows="3" cols="40" style="display: none;"></textarea>
        </div>
        <p>
        <h5 id="patient_Result" align="right" style="color:red"></h5>
      </div>
      <br>
	    <!-- PatientDataTable-->
	    <div id="datatable_patient_visible">
	      <table id="patientTable" class="display" cellspacing="0" width="100%" >
            
	          <thead>
	              <tr>
	                  <th>帳號</th>
	                  <th>名字</th>
	                  <th>姓氏</th>
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
	    <footer class="sticky-footer">
	      <div class="container">
	        <div class="text-center">
	          <small>COPD Walk © 2018</small>
	        </div>
	      </div>
	    </footer>
	    <!-- Scroll to Top Button-->
	    <a class="scroll-to-top rounded" href="#page-top">
	      <i class="fa fa-angle-up"></i>
	    </a>
	    <!-- Logout Modal-->
	    <div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	      <div class="modal-dialog" role="document">
	        <div class="modal-content">
	          <div class="modal-header">
	            <h5 class="modal-title" id="exampleModalLabel">Ready to Leave?</h5>
	            <button class="close" type="button" data-dismiss="modal" aria-label="Close" >
	              <span aria-hidden="true">×</span>
	            </button>
	          </div>
	          <div class="modal-body">Select "Logout" below if you are ready to end your current session.</div>
	          <div class="modal-footer">
	            <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
	            <a class="btn btn-primary" href="index.php">Logout</a>
	          </div>
	        </div>
	      </div>
	    </div>
	    <!-- Bootstrap core JavaScript-->
	    <script src="vendor/jquery/jquery.min.js"></script>
	    <script src="vendor/popper/popper.min.js"></script>
	    <script src="vendor/bootstrap/js/bootstrap.min.js"></script>
	    <!-- Core plugin JavaScript-->
	    <script src="vendor/jquery-easing/jquery.easing.min.js"></script>
	    <!-- Page level plugin JavaScript-->
	    <script src="vendor/chart.js/Chart.min.js"></script>
	    <script src="vendor/datatables/jquery.dataTables.js"></script>
	    <!-- <script src="vendor/datatables/dataTables.bootstrap4.js"></script> -->
	    <!-- Custom scripts for all pages-->
	    <script src="js/sb-admin.min.js"></script>
	    <!-- Custom scripts for this page-->
	    <script src="js/sb-admin-datatables.min.js"></script>
    </div>
</body>

</html>
