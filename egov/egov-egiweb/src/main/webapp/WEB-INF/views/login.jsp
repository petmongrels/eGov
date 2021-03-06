
<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%
	String ipAddress = request.getRemoteAddr();
	String proxiedIPAddress = request.getHeader("X-Forwarded-For");
	if (StringUtils.isNotBlank(proxiedIPAddress)) {
		String [] ipAddresses = proxiedIPAddress.split(",");
		ipAddress = ipAddresses[ipAddresses.length-1].trim();
	}
	String userAgentInfo = request.getHeader("User-Agent");
%>
<!DOCTYPE html>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<meta name="description" content="eGov Urban Portal" />
		<meta name="author" content="eGovernments Foundation" />
        <spring:eval expression="@environment.getProperty('app.core.build.no')" scope="application" var="buildno"/>
		<title>eGov Urban Portal Login</title>
		<link rel="icon" href="/egi/resources/global/images/favicon.png" sizes="32x32">
		<link rel="stylesheet" href="/egi/resources/global/css/bootstrap/bootstrap.css">
		<link rel="stylesheet" href="/egi/resources/global/css/font-icons/entypo/css/entypo.css">
		<link rel="stylesheet" href="/egi/resources/global/css/font-icons/font-awesome-4.3.0/css/font-awesome.min.css">
		<link rel="stylesheet" href="/egi/resources/global/css/egov/custom.css?rnd=${applicationScope.buildno}">
		<script src="/egi/resources/global/js/jquery/jquery.js" type="text/javascript"></script>
		
		<!--[if lt IE 9]><script src="resources/js/ie8-responsive-file-warning.js"></script><![endif]-->
		
		<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
		<!--[if lt IE 9]>
			<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
			<script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
		<![endif]-->
	</head>
	<body class="page-body index">
		<div class="page-container">
			<header class="navbar navbar-fixed-top">
				<!-- set fixed position by adding class "navbar-fixed-top" -->
				<nav class="navbar navbar-default navbar-custom navbar-fixed-top">
					<div class="container-fluid">
						<div class="navbar-header col-md-10 col-xs-10">
							<a class="navbar-brand" href="javascript:void(0);"> <img src="<c:url value='${sessionScope.citylogo}' context='/egi'/>" height="60">
								<div>
									<span class="title2">${sessionScope.citymunicipalityname}</span>
								</div>
							</a>
						</div>
						<div class="nav-right-menu col-md-2 col-xs-2">
							<ul class="hr-menu text-right">
								<li class="ico-menu">
									<a href="http://www.egovernments.org" data-strwindname = "egovsite" class="open-popup">
									<img src="/egi/resources/global/images/egov_logo_tr_h.png" title="Powered by eGovernments" height="37" alt="">
									</a>
								</li>
							</ul>
						</div>
					</div>
				</nav>
			</header>
			<div class="main-content">
				<div class="row top-space">
				    <div class="text-center error-msg">
					<noscript>
					    	You don't have javascript enabled.  Make sure Javascript is enabled.
					</noscript>
					</div>
					<div class="col-md-6 side-space">
						<div class="col-md-12 community-card">
							<a href="/portal/citizen/register" target="_blank">
								<div class="rounded-circle"><i class="fa fa-user a"></i></div>&nbsp;
								<div class="label-font">
									<spring:message code="lbl.create.ac"/><br>
									<span class="label-subfont"><spring:message code="lbl.create.ac.desc"/></span>
								</div>
							</a>
						</div>
						<div class="col-md-12 community-card">
							<a href="/pgr/complaint/citizen/anonymous/show-reg-form" target="_blank">
								<div class="rounded-circle"><i class="fa fa-pencil b"></i></div>&nbsp;
								<div class="label-font">
									<spring:message code="lbl.reg.comp"/><br> 
									<span class="label-subfont"><spring:message code="lbl.reg.comp.desc"/></span>
								</div>
							</a>
						</div>
						<div class="col-md-12 community-card">
							<div class="rounded-circle">
								<i class="fa fa-search c"></i>
							</div>
							&nbsp;
							<div class="label-font" style="vertical-align: sub">
								<spring:message code="lbl.check.comp.status"/>&nbsp;<br>
								<input type="text" placeholder="Grievance number" id="compsearchtxt"
									style="padding: 2px 5px; height: 30px;font-size: 14px;border: 0;padding-left: 0;border-bottom: 1px solid #D0D2D7;outline: none;box-shadow: none;">
								<button class="btn-custom" id="compsearch"
									style="padding: 4px 5px;border-radius: 4px;font-size: 14px;vertical-align: bottom;"><spring:message code="btn.lbl.search"/></button>
							</div>
							<div class="error-msg search-error-msg display-hide" style="padding-left: 65px;">Grievance number is mandatory</div>
						</div>
						<c:if test="${not empty sessionScope.corpCallCenterNo}">
							<div class="col-md-12 community-card">
								<a href="tel:${sessionScope.corpCallCenterNo}">
									<div class="rounded-circle"><i class="fa fa-phone b"></i></div>&nbsp;
									<div class="label-font">
										<spring:message code="lbl.reg.via.grev.cell"/><br>
										<span class="label-subfont"><spring:message code="lbl.cal.desc.part1"/> ${sessionScope.corpCallCenterNo} <spring:message code="lbl.cal.desc.part2"/></span>
									</div>
								</a>
							</div>
						</c:if>
					</div>
					<div class="col-md-6 side-space">
	
						<div class="signin-formcontent signin-section">
							<form method="post" role="form" id="signform" action="${pageContext.request.contextPath}/j_security_check" autocomplete="off">
								<div class="form-group">
									<div class="signin-title"><spring:message code="lbl.login"/></div>
								</div>
								<input style="display:none" type="text">
								<input style="display:none" type="password" />
								<div class="form-group">
									<div class="input-group">
										<div class="input-group-addon style-label">
											<i class="entypo-user theme-color style-color"></i>
										</div>
										<input type="text" class="form-control style-form"
											name="j_username" id="j_username"
											placeholder="Username" autocomplete="off"
											required="required" autofocus="autofocus"/> <span class="mandatory set-mandatory"></span>
									</div>
								</div>
								<div class="form-group">
									<div class="input-group">
										<div class="input-group-addon style-label">
											<i class="entypo-key theme-color style-color"></i>
										</div>
										<input type="password" class="form-control style-form"
											name="j_password" id="j_password" placeholder="Password"
											autocomplete="new-password" required="required" /> <span
											class="mandatory set-mandatory"></span>
									</div>
								</div>
								<div class="form-group display-hide" id="counter-section">
									<div class="input-group">
										<div class="input-group-addon style-label">
											<i class="entypo-location theme-color style-color"></i>
										</div>
										<select class="form-control style-form" name="locationId" id="locationId"></select>
									</div>
								</div>
								<c:if test="${param.error}">
								<div class="form-group">
									<div class="text-center error-msg font-12">
										<c:choose>
										<c:when test="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message == 'Maximum sessions of {0} for this principal exceeded'}">
											<spring:message code="msg.multiple.login"/>
										</c:when>
										<c:when test="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message == 'User account has expired'}">
											<spring:message code="msg.cred.exprd1"/>
											<a href="javascript:void(0);" data-toggle="modal"
											   data-target="#fpassword" data-backdrop="static">
											<spring:message code="msg.cred.exprd2"/>
											</a> <spring:message code="msg.cred.exprd3"/>
										</c:when>
										<c:when test="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message == 'Inactive User'}">
		     							 	<div class="form-group signin-leftpadding">
												<a href="/portal/citizen/register?activation=true" class="btn btn-custom btn-block btn-login signin-submit">
												<spring:message code="msg.acc.not.activated"/>
												</a> 
											</div>
	     								</c:when>
										<c:otherwise>
											<spring:message code="msg.cred.invalid"/>
										</c:otherwise>
										</c:choose>
									</div>
								</div>
								</c:if>
								<c:if test="${not empty param.recovered}">
								<div class="form-group">
									<c:choose>
										<c:when test="${param.recovered}">
											<div class="text-center  font-green font-12"><spring:message code="msg.success.pwd.recov"/></div>
										</c:when>
										<c:otherwise>
											<div class="text-center  error-msg font-12"><spring:message code="msg.fail.pwd.recov"/></div>
										</c:otherwise>
									</c:choose>
								</div>
								</c:if>
								<c:if test="${not empty param.reset}">
								<div class="form-group">
									<c:choose>
										<c:when test="${param.reset}">
											<div class="text-center  font-green font-12"><spring:message code="msg.success.pwd.reset"/></div>
										</c:when>
										<c:otherwise>
											<div class="text-center  error-msg font-12"><spring:message code="msg.fail.pwd.reset"/></div>
										</c:otherwise>
									</c:choose>
								</div>
								</c:if>
								<div class="form-group signin-leftpadding">
									<button type="submit"
										class="btn btn-custom btn-block btn-login signin-submit" id="signin-action">
										<i class="entypo-login"></i><spring:message code="lbl.login"/>
									</button>
								</div>
								<div class="row">
									<div class="col-md-12 col-xs-12 text-right add-margin"
										style="font-size: 12px;">
										<a href="javascript:void(0);" data-toggle="modal"
											data-target="#fpassword" data-backdrop="static"><spring:message code="lbl.forgot.pwd"/></a>
									</div>
								</div>
								<input type="hidden" id="ipAddress" name="ipAddress" value="<%=ipAddress%>" /> 
								<input type="hidden" id="loginType" name="loginType" />
								<input type="hidden"  name="userAgentInfo" value="<%=userAgentInfo%>" /> 
							</form>
						</div>
					</div>
				</div>
				<div class="row text-center">
					<div class="col-md-12 feature-top text-center"></div>
					<div class="col-md-4 top-community-space">
						<a href="${sessionScope.corpGisLink}" target="_blank"><span><i
								class="fa fa-map-marker fa-2x"></i></span></a>
						<p>
							${sessionScope.corpAddress}
						</p>
						<a class="block-left-text" href="${sessionScope.corpGisLink}"
							target="_blank">Find us on google maps</a>
					</div>
					<div class="col-md-4 top-community-space">
						<a href="tel:${sessionScope.corpContactNo}"><span><i
								class="fa fa-phone fa-2x"></i></span></a>
						<p>${sessionScope.corpContactNo}</p>
						<a href="mailto:${sessionScope.corpContactEmail}"><p>${sessionScope.corpContactEmail}</p></a>
					</div>
					<div class="col-md-4 top-community-space">
						<a href="javascript:void(0)"><span><i
								class="fa fa-thumbs-o-up fa-2x"></i></span></a>
						<p>Follow us on</p>
						<a href="${sessionScope.corpTwitterLink}" target="_blank"><i
							class="fa fa-twitter fa-2x"></i></a> <a
							href="${sessionScope.corpFBLink}" target="_blank"><i
							class="fa fa-facebook fa-2x"></i></a>
					</div>
				</div>
			</div>
			<footer class="main">
				Powered by <a href="http://eGovernments.org" target="_blank">eGovernments Foundation</a>
			</footer>
		</div>
		<div class="modal fade" id="fpassword" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="myModalLabel"><spring:message code="lbl.recover.pwd"/></h4>
					</div>
					<div class="modal-body">
						<form method="post" role="form" id="forgotPasswordForm">
							<div class="form-group">
								<div class="input-group">
									<div class="input-group-addon style-label">
										<i class="entypo-user style-color"></i>
									</div>
									<input type="text" class="form-control style-form"
										name="identity" id="emailOrMobileNum"
										required="required" placeholder="Your Username"
										autocomplete="off" />
										<input type="hidden" name="originURL" id="originURL">
								</div>
								<div id="emailOrMobileNoReq" class="error-msg display-hide"><spring:message code="lbl.pwd.recover.un.req"/></div>
								<div id="" style="font-size: 12px;margin-left: 47px;color: #6b4f2c;"><spring:message code="lbl.pwd.reset.link"/></div>
							</div>
							<div class="form-group text-right">
								<button type="submit" id="recovrbtn" class="btn btn-primary"><spring:message code="btn.lbl.recover"/></button>
								<button type="button" class="btn btn-default"
									data-dismiss="modal"><spring:message code="lbl.close"/></button>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="cookieornoscript" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">Enable Cookies</h4>
					</div>
					<div class="modal-body">
						Your browser seems to have cookies disabled. Make sure cookies are enabled or try opening a new browser window.
					</div>
				</div>
			</div>
		</div>
		<script src="/egi/resources/global/js/bootstrap/bootstrap.js" type="text/javascript"></script>
		<script src="/egi/resources/global/js/egov/custom.js?rnd=${applicationScope.buildno}" type="text/javascript"></script>
		<script src="/egi/resources/global/js/jquery/plugins/jquery.validate.min.js"></script>
		<script src="/egi/resources/js/app/login.js?rnd=${applicationScope.buildno}" type="text/javascript"></script>
	</body>
</html>
<%session.invalidate();%>