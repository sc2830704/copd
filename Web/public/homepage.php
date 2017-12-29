<?php
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
            <i class="fa fa-fw fa-dashboard"></i>
            <span class="nav-link-text">COPD首頁</span>
          </a>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Patient">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseComponents" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">病患資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseComponents">
            <li>
              <a href="PatientPage.php">Table</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Environment">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseExamplePages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">環境資料</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseExamplePages">
            <li>
              <a href="EnvironmentPage.php">Table</a>
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
              <a href="DailyPage.php">Table</a>
            </li>
          </ul>
        </li>
        <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Activity">
          <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseActivityPages" data-parent="#exampleAccordion">
            <i class="fa fa-fw fa-table"></i>
            <span class="nav-link-text" id="test">活動紀錄</span>
          </a>
          <ul class="sidenav-second-level collapse" id="collapseActivityPages">
            <li>
              <a href="ActivityPage.php">Table</a>
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
	    <!-- /.container-fluid-->
	    <!-- Download Page -->
	    <div class="edit_table" id="DownloadTable" style="display:none">
	    	<h2 align="center">Download Area</h2><br>
	    	<table cellspacing="0" width="80%" align="center" style="border:1px solid;text-align: center;">
	            <tr class="tr2">
	                <th style="text-align: center;border:1px solid">ID</th>
	                <th style="text-align: center;border:1px solid">Name</th>
	                <th style="text-align: center;border:1px solid">Button</th>
	            </tr>
	            <tr class="tr2">
	            	<td class="td2">1</td>
	            	<td class="td2">Environment Excel</td>
	            	<td class="td2"><a href="Download_Environment_Excel.php">Download</a></td>
	            </tr>
	            <tr class="tr2">
	            	<td class="td2">2</td>
	            	<td class="td2">Environment PDF</td>
	            	<td class="td2"><a href="Download_Environment_PDF.php">Download</a></td>
	            </tr>
	            <tr class="tr2">
	            	<td class="td2">3</td>
	            	<td class="td2">Patient Excel</td>
	            	<td class="td2"><a href="Download_Patient_Excel.php">Download</a></td>
	            </tr>
	            <tr class="tr2">
	            	<td class="td2">4</td>
	            	<td class="td2">Patient PDF</td>
	            	<td class="td2"><a href="Download_Patient_PDF.php">Download</a></td>
	            </tr>
	    	</table>
	    	<br>
	    </div>
	    <!-- PatientManage-->
	    <div class="edit_table" id="PatientManage" style="display:none">
	      <h2 align="center">Edit The Patient Data</h2>
	      <p></p>
	        <div class="row">
	          <div class="column cleft" align="right">
	            <label for="pid">User's ID：</label>
	            <input type="text" id="pid"><br>

	            <label for="fname">FirstName：</label>
	            <input type="text" id="fname"><br>

	            <label for="lname">LastName：</label>
	            <input type="text" id="lname"><br>

	            <label for="sex">Sex：</label>
	            <input type="text" id="sex"><br>

	            <label for="bmi">BMI：</label>
	            <input type="text" id="bmi"><br>
	          </div>

	          <div class="column cright" align="right">
	            <label for="history">History：</label>
	            <textarea type="text" id="history" rows="3" cols="60"></textarea><br>
	            <label for="drug">Drug：</label>
	            <textarea type="text" id="drug" rows="3" cols="60"></textarea><br>

	            <button  class="button2" id="patient_save">Save</button>
	            <button  class="button2" id="patient_delete">Delete</button>
	            <button  class="button2" id="patient_update">Update</button>
	            <button  class="button2" id="patient_cancel">Cancel</button>
	          </div>
	          
	        </div>
	        <p>
	        <h5 id="patient_Result" align="right" style="color:red"></h5>
	    </div>
	    <br>
	    <!-- /.content-wrapper-->
	    <footer class="sticky-footer">
	      <div class="container">
	        <div class="text-center">
	          <small>Copyright © Your Website 2017</small>
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
	    <script src="js/sb-admin-charts.min.js"></script>
    </div>
</body>

</html>
