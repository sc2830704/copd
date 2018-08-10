<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" id="mainNav">
  <a class="navbar-brand" href="homepage.php">COPD Manage System</a>
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
      <li class="nav-item" data-toggle="tooltip" data-placement="right" title="Evaluate">
        <a class="nav-link nav-link-collapse collapsed" data-toggle="collapse" href="#collapseEvaluatePages" data-parent="#exampleAccordion">
          <i class="fa fa-fw fa-tasks"></i>
          <span class="nav-link-text" id="test">衛教量表</span>
        </a>
        <ul class="sidenav-second-level collapse" id="collapseEvaluatePages">
          <li>
            <a href="EvaluatePage.php">Evaluate</a>
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
     <text style="margin: auto 0 auto 0; color:yellow; padding-right: 10px" id="login_msg">Hello! <?php echo $_SESSION['account'] ?></text>
      <li class="nav-item">
        <a class="nav-link" data-toggle="modal" data-target="#exampleModal">
          <i class="fa fa-fw fa-sign-out"></i>Logout</a>
      </li>
    </ul>
  </div>
</nav>