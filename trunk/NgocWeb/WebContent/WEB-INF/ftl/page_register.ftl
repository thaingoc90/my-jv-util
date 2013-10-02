<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
    <meta name="description" content="description" />
    <meta name="keywords" content="contents" />
    <meta name="author" content="SPINNING" />
    <title>NHP - Register</title>
    <link rel="stylesheet" type="text/css" href="${staticResourceRoot}bootstrap2.3.2/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="${staticResourceRoot}css/fe-base.css"/>
</head>
<body style="background: #eee;">
	<form class="register-form">
		<header>Register Form</header>
		<div class="form-row">
			<label>Email : </label>
			<input type="text" maxlength="60" spellcheck="false" name="email" required placeholder="Email"/>
			<span class="required-label">R</span>
		</div>
		<div class="form-row">
			<label>Login Name : </label>
			<input type="text" maxlength="60" spellcheck="false" name="loginName" required placeholder="Login Name"/>
			<span class="required-label">R</span>
		</div>
		<div class="form-row">
			<label>Password : </label>
			<input type="password" maxlength="60" spellcheck="false" name="password" required placeholder="Password"/>
			<span class="required-label">R</span>
		</div>
		<div class="form-row">
			<label>Retype Password : </label>
			<input type="password" maxlength="60" spellcheck="false" name="confirmPassword" required placeholder="Retype Password"/>
			<span class="required-label">R</span>
		</div>
		<div class="form-row">
			<label>Display Name : </label>
			<input type="text" maxlength="60" spellcheck="false" name="displayName" required placeholder="Display Name"/>
			<span class="required-label">R</span>
		</div>
		<div class="form-row">
			<label>Birthday : </label>
			<select name="bday" id="field-dob-day">
				<option value="0">Day</option>
				<#list dobDays as day>
					<option value="${day}">${day}</option>
				</#list>
			</select>
			<select name="bmonth" id="field-dob-month">
				<option value="0">Month</option>
				<#list dobMonths?keys as month>
					<option value="${month}">${dobMonths[month]}</option>
				</#list>
			</select>
			<select name="byear" id="field-dob-year">
				<option value="0">Year</option>
				<#list dobYears as year>
					<option value="${year}">${year}</option>
				</#list>
			</select>
		</div>
		<div class="form-row">
			<label>Sex : </label>
			<select name="sex">
				<option value="1">Male</option>
				<option value="2">Female</option>
			</select>
		</div>
		<div class="form-row">
			<input type="submit" class="btn btn-success" value="Register"/>
		</div>
	</form>
</body>
</html>