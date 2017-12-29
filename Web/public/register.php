<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">
  <title>SB Admin - Start Bootstrap Template</title>
  <!-- Bootstrap core CSS-->
  <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <!-- Custom fonts for this template-->
  <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
  <!-- Custom styles for this template-->
  <link href="css/sb-admin.css" rel="stylesheet">
</head>

<script type="text/JavaScript" src="jquery-1.11.3.min.js"></script>
<script type="text/JavaScript">
$(document).ready(function(){
    $("#register_button").click(function() {
        $.ajax({
            type: "POST",
            url: "register_db.php",
            dataType: "json",
            data: {
                accountName: $("#exampleAccount").val(),
                firstname: $("#exampleInputFirstName").val(),
                lastname: $("#exampleInputLastName").val(),
                password1: $("#exampleInputPassword").val(),
                password2: $("#exampleConfirmPassword").val()                  
            },
            success: function(data) {
                if (data.accountName) {
                    $("#acc_result").html(data.accountName + ' 儲存成功');
                } else {
                    $("#acc_result").html(data.msg);
                }                
            },
            error: function(jqXHR) {
                alert("發生錯誤: " + jqXHR.status);
            }
        })
    })
});
</script>

<body class="bg-dark">
  <div class="container">
    <div class="card card-register mx-auto mt-5">
      <div class="card-header">Register an Account
      <h5 id="acc_result" align="right" style="color:red;"></h5>
      </div>
      <div class="card-body">
        <form>
          <div class="form-group">
            <label for="exampleAccount">Account</label>
            <input class="form-control" id="exampleAccount" placeholder="Enter Account">
          </div>
          <div class="form-group">
            <label for="exampleInputPassword">Password</label>
            <input class="form-control" id="exampleInputPassword" type="password" placeholder="Password">
          </div>
          <div class="form-group">
            <label for="exampleConfirmPassword">Confirm password</label>
            <input class="form-control" id="exampleConfirmPassword" type="password" placeholder="Confirm password">
          </div>
          <a class="btn btn-primary btn-block"  id="register_button" style="color:white">Register</a>
        </form>
        <div class="text-center">
          <a class="d-block small mt-3" href="index.php">Login Page</a>
          <a class="d-block small" href="">Forgot Password?</a>
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
</body>

</html>
