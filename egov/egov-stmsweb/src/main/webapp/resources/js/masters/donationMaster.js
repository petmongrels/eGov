/*#-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#-------------------------------------------------------------------------------*/
$(document).ready(function() {
	document.getElementById('amount').value = "";
	$('#propertyType option').each(function() {              // remove  propety type mixed option and underscore
		var $this = $(this);
		$this.text($this.text().replace(/_/g, ' '));
		if ($this.text() == "MIXED")
			$(this).remove();
	});
	
	$("#submitform").click(function() {
		if ($("#donationMaster").valid())
			 if(!validateEffectiveDate())
				{
				return false;
				}
			  else{
				  if($('#effectiveDate').val() !=undefined)
					  donationMasterCombination();
			  }
	});
	
	function donationMasterCombination() {
		$.ajax({
			url : '/stms/masters/ajaxexistingdonationvalidate',
				type : "GET",
				data : {
					propertyType : $('#propertyType').val(),
					noOfClosets : $('#noOfClosets').val(),
					fromDate : $('#effectiveDate').val(),
					monthlyRate : $('#amount').val()
					},
					dataType : 'json',
					success : function(response) {
					console.log("success"+ response);
					if (response > 0) {
					if (!overwriteDonationMasterRate(response))
						return false;
					} else {
					document.forms[0].submit();
					return true;
					}
				},
				error : function(response) {
					console.log("failed");
				}
			});
	}
	
	function overwriteDonationMasterRate(res) {
		var r = confirm($("#err-validate-donationoverwritevalidate").text().replace('{0}',res))
		if (r == true) {
			document.forms[0].submit();
		} else {
			return false;
		}
	}
	
	function validateEffectiveDate() {
		var fromdate = $('#effectiveDate').val();
		var todaysDate = getTodayDate();
		if (compareDate(fromdate, todaysDate) == 1) {
			bootbox.alert($("#err-validate-effective-date").text());
			$(this).val("");
			return false;
		} else {
			return true;
		}
	}
	
	function clearField() {
		input.value = "";
	};
	function getTodayDate() {
		if (!validateAmmount())
			return false;
		var date;
		var d = new Date();
		var curr_date = d.getDate();
		var curr_month = d.getMonth();
		curr_month++;
		var curr_year = d.getFullYear();
		date = curr_date + "/" + curr_month + "/" + curr_year;
		return date;
	}

	function compareDate(dt1, dt2) {
		var d1, m1, y1, d2, m2, y2, ret;
		dt1 = dt1.split('/');
		dt2 = dt2.split('/');
		ret = (eval(dt2[2]) > eval(dt1[2])) ? 1
				: (eval(dt2[2]) < eval(dt1[2])) ? -1
						: (eval(dt2[1]) > eval(dt1[1])) ? 1
								: (eval(dt2[1]) < eval(dt1[1])) ? -1															// decimal points
										: (eval(dt2[0]) > eval(dt1[0])) ? 1
												: (eval(dt2[0]) < eval(dt1[0])) ? -1
														: 0;
		return ret;
	}
	function validateAmmount() {
		var val = $('#amount').val();
		if (val < 1) {
			bootbox.alert($("#err-validate-amount").text());
			return false;
		} else
			return true;
	}
	
	
	$('#amount').keyup(function(e) {       // validate two decimal points
		var regex = /^\d+(\.\d{0,2})?$/g;
		if (!regex.test(this.value)) {
			$(this).val($(this).getNum());
		}
	});

	jQuery.fn.getNum = function() {
		var val = $.trim($(this).val());
		if (val.indexOf(',') > -1) {
			val = val.replace(',', '.');
		}
		var num = parseFloat(val);
		var num = num.toFixed(2);
		if (isNaN(num)) {
			num = '';
		}
		return num;
	}
	$("#view" ).click(function() {                               // url to view button          
		 window.location = "/stms/donationmaster/view";
	});
});