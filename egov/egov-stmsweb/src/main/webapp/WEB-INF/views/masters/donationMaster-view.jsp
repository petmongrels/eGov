<!--
	eGov suite of products aim to improve the internal efficiency,transparency, 
    accountability and the service delivery of the government  organizations.
 
    Copyright (C) <2015>  eGovernments Foundation
 
	The updated version of eGov suite of products as by eGovernments Foundation 
    is available at http://www.egovernments.org
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or 
    http://www.gnu.org/licenses/gpl.html .
 
    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:
 
 	1) All versions of this program, verbatim or modified must carry this 
 	   Legal Notice.
 
 	2) Any misrepresentation of the origin of the material is prohibited. It 
 	   is required that all modified versions of this material be marked in 
 	   reasonable ways as different from the original version.
 
 	3) This license does not grant any rights to any user of the program 
 	   with regards to rights under trademark law for use of the trade names 
 	   or trademarks of eGovernments Foundation.
 
   	In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
-->
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<body>
<div class="row" id="page-content">
	<div class="col-md-12">
		<div class="row">
			<div class="col-md-12">
				<div class="panel-group">
					<div class="panel panel-primary">
						<div class="panel-heading slide-history-menu">
							<div class="panel-title"></div>
							<div class="history-icon"></div>
						</div>
						<div class="panel-body history-slide">
							<form:errors cssClass="add-margin error-msg" />
							<div class="form-group">
								<div class="form-group col-sm-6 col-sm-offset-3">
								<table class="table table-bordered "  >
										<thead>
											<tr>
												<th style="width:10%"><label><spring:message code="lbl.slno" /></label></th>
												<th style="width:30%"><label><spring:message
															code="lbl.propertytype" /></label></th>
												<th style="width:10%"><label><spring:message
															code="lbl.noofclosets" /></label></th>
												<th style="width:20%"><label><spring:message
															code="lbl.effective.fromdate" /></label></th>
												<th style="width:10%"><label><spring:message
															code="lbl.donation.amount" /></label></th>
												<th style="width:10%"><label><spring:message
															code="lbl.active"></spring:message> </label></th>
											</tr>
										</thead>
										<tbody id="tblBody" class="table table-bordered">
										<c:forEach var="donationMasterList"
											items="${donationMasterList}" varStatus="index">
											<tr>
												<td ><c:out value="${index.index + 1}" /></td>
												<td id="propertyType"><c:out
														value="${donationMasterList.propertyType}" /></td>
												<td  class="text-right"><c:out
														value="${donationMasterList.noOfClosets}" /></td>
												<td ><fmt:formatDate pattern="dd/MM/yyyy"
														value="${donationMasterList.fromDate}" /></td>
													<td class="text-right"><fmt:formatNumber type="number"
															minFractionDigits="2" maxFractionDigits="2"
															value="${donationMasterList.amount}" /></td>
													<td  id="active" class="text-right"><c:out
														value="${donationMasterList.active}" /></td>
												</tr>
										</c:forEach>
									</tbody>
								</table>
								<div class="row">
									<div class="text-center">
										<a href="javascript:void(0)" class="btn btn-default"
											onclick="self.close()"><spring:message code="lbl.close" /></a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</div>
<script
	src="<c:url value='/resources/js/masters/view.js?rnd=${app_release_no}'/>"></script>


