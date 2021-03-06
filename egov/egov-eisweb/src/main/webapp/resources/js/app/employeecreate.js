$(document).ready(function(){
	
	// Instantiate the Bloodhound suggestion engine
	var designation = new Bloodhound({
		datumTokenizer: function (datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: {
			url: '/eis/employee/ajax/designations?designationName=%QUERY',
			filter: function (data) {
				// Map the remote source JSON array to a JavaScript object array
				return $.map(data, function (designation) {
					return {
						name: designation.name,
						value: designation.id
					};
				});
			}
		}
	});
	
	// Initialize the Bloodhound suggestion engine
	designation.initialize();
	// Instantiate the Typeahead UI
	var typeaheadobj =$('#designationName').typeahead({
		  hint: false,
		  highlight: false,
		  minLength: 1
		}, {
		displayKey: 'name',
		source: designation.ttAdapter()
	});
	typeaheadWithEventsHandling(typeaheadobj, '#designationId'); 
	
	
	function validateAssignment() {
		var deptId = $("#deptId").val();
		var desigId =$("#designationId").val();
		var fromDate = $("#fromDate").val();
		var toDate = $("#toDate").val();
		var posId = $("#positionId").val();
		var validate = true;
		if(null==deptId || ''==deptId){
			$('.departmenterror').html('Department is required').show();
			validate = false;
		}
		if(null==desigId || ''==desigId) {
			$('.designationerror').html('Designation is required').show();
			validate = false;
		}
		if(null==fromDate || ''==fromDate) {
			$('.fromdateerror').html('From Date is required').show();
			validate = false;
		}
		if(null==toDate || ''==toDate) {
			$('.todateerror').html('To Date is required').show();
			validate = false;
		}
		if(null==posId || ''==posId){
			$('.positionerror').html('Position is required').show();
			validate = false;
		}
		return validate;
	}
	
	$("#deptId").blur(function (){
		var deptId = $("#deptId").val();
		if(null!=deptId || ''!=deptId){
			$('.departmenterror').hide();
		}
	});
	
	$("#designationName").blur(function (){
		var desigId = $("#designationName").val();
		if(null!=desigId || ''!=desigId){
			$('.designationerror').hide();
		}
		else
			$("#designationId").val("");
	});
	
	$("#positionName").blur(function (){
		var posId = $("#positionName").val();
		if(null!=posId || ''!=posId){
			$('.positionerror').hide();
		}
		else
			$("#positionId").val("");
	});
	
	$("#fromDate").blur(function (){
		var fromDate = $("#fromDate").val();
		if(null!=fromDate || ''!=fromDate){
			$('.fromdateerror').hide();
		}
	});
	
	$("#toDate").blur(function (){
		var toDate = $("#toDate").val();
		if(null!=toDate || ''!=toDate){
			$('.todateerror').html('To Date is required').hide();
		}
		if(Date.parse($("#fromDate").val()) >= Date.parse($("#toDate").val()))
			$('.todateerror').html('To Date should be greater than from date').show();
	});
	
	//Position auto-complete
	
	var positions = new Bloodhound(
		{
			datumTokenizer : function(datum) {
				return Bloodhound.tokenizers
						.whitespace(datum.value);
			},
			queryTokenizer : Bloodhound.tokenizers.whitespace,
			remote : {
				url : '/eis/employee/ajax/positions',
				replace: function(uri, uriEncodedQuery) {
					return uri + '?positionName='+uriEncodedQuery+'&deptId='+ $("#deptId").val()+'&desigId='+$("#designationId").val()+'&fromDate='+$("#fromDate").val()+'&toDate='+$("#toDate").val()+'&primary='+$("#primary_yes").prop("checked");
					
				},
				filter : function(data) {
					// Map the remote source JSON array to a
					// JavaScript object array
					return $.map(data, function(position) {
						return {
							name: position.name,
							value: position.id
						};
					});
				}
			}
		});
		positions.initialize();
		
	
	var typeaheadobj = $('#positionName').typeahead({
		hint: false,
		highlight: false,
		minLength: 1
		}, {
		displayKey: 'name',
		source: positions.ttAdapter()
		});
	
	typeaheadWithEventsHandling(typeaheadobj, '#positionId'); 
	
	$("#positionName").focus(function() {
		validateAssignment();
		$('.positionerror').hide();
		positions.initialize();
	});
	
	var rowCount=0;
	var edit=false;
	var deleteRow="";
	var editedRowIndex="";
	
	$("#btn-add").click(function() {
		if(validateAssignment()) {
			if(!edit){
				rowCount = $("#assignmentTable tr").length-1;
				addRow(rowCount);
				rowCount++;
			}
			else{
				deleteRow.remove();
				addRow(editedRowIndex);		
				edit=false;
			}
			resetAssignmentValues();
		}	
	});
	
	function resetAssignmentValues() {
		if(!edit) {
			$("#primary_yes").prop("checked",true);
			$("#primary_no").prop("checked",false);
			$("#fromDate").val("");
			$("#toDate").val("");
			$("#deptId").val("");
			$("#designationName").val("");
			$("#designationId").val("");
			$("#positionId").val("");
			$("#positionName").val("");
			$("#fundId").val("");
			$("#functionId").val("");
			$("#functionaryId").val("");
			$("#gradeId").val("");
			$("#isHodYes").prop("checked",false);
			$("#isHodNo").prop("checked",true);
			$("#hodDeptId").find('option').attr('selected', false);
			$('#hodDeptDiv').hide();
		}	
	}
	
	
	function addRow(index) {
		var fund = (null!=$("#fundId").val() || 'undefined'!=$("#fundId").val())?$("#fundId").val():null;
		var ftn = (null!=$("#functionId").val() || 'undefined'!=$("#functionId").val())?$("#functionId").val():null;
		var functionary = (null!=$("#functionaryId").val() || 'undefined'!=$("#functionaryId").val())?$("#functionaryId").val():null;
		var grade = (null!=$("#gradeId").val() || 'undefined'!=$("#gradeId").val())?$("#gradeId").val():null;
		var hoddept = (null!=$("#hodDeptId").val() || 'undefined'!=$("#hodDeptId").val())?$("#hodDeptId").val():null;
		var hoddept = (null!=$("#hodDeptId").val() || 'undefined'!=$("#hodDeptId").val())?$("#hodDeptId").val():null;
		var hodInput="";
		if(null!=hoddept){
			for(var i=0;i<hoddept.length;i++) {
				hodInput = hodInput+'<input type="hidden" id="assignments['+index+'].deptSet['+i+'].hod" name="assignments['+index+'].deptSet['+i+'].hod" value="'+hoddept[i]+'"/>';
			}
			hodInput = hodInput+'<input type="hidden" id="hodIds'+index+'" value="'+hoddept+'"/>';
		}
		var del="";
		  del='<span class="add-padding"><i id="delete_row" class="fa fa-remove"></i></span>';
		var text = 
					'<tr>'+
						'<td>'+
							'<input type="hidden" id="assignments['+index+'].fromDate" name="assignments['+index+'].fromDate" '+
							'value="'+$("#fromDate").val()+'"/>'+
							'<input type="hidden" id="assignments['+index+'].toDate" name="assignments['+index+'].toDate" '+
							'value="'+$("#toDate").val()+'"/>'+
							'<input type="text" id="table_date_range'+index+'" class="form-control" readonly="readonly" style="text-align:center"/>'+
						'</td>'+	
						'<td>'+
							'<input type="hidden" class="isPrimary" id="assignments['+index+'].primary" name="assignments['+index+'].primary" '+ 
							'value="'+$("#primary_yes").prop("checked")+'"/>'+
							'<input type="text" id="table_primary'+index+'" class="form-control checkPrimary" readonly="readonly" style="text-align:center"/>'+ 
						'</td>'+	
						'<td>'+
							'<input type="hidden" id="assignments['+index+'].department" name="assignments['+index+'].department" '+
							'value="'+$("#deptId").val()+'"/>'+
							'<input type="text" id="table_department'+index+'" class="form-control" readonly="readonly" style="text-align:center"/>'+
						'</td>'+	
						'<td>'+
							'<input type="hidden" id="assignments['+index+'].designation" name="assignments['+index+'].designation" '+
							'value="'+$("#designationId").val()+'"/>'+
							'<input type="text" id="table_designation'+index+'" class="form-control" readonly="readonly" style="text-align:center"/>'+
						'</td>'+	
						'<td>'+	
							'<input type="hidden" id="assignments['+index+'].position" name="assignments['+index+'].position" '+
							'value="'+$("#positionId").val()+'"/>'+
							'<input type="text" id="table_position'+index+'" class="form-control" readonly="readonly" style="text-align:center"/>'+
							'<input type="hidden" id="assignments['+index+'].fund" name="assignments['+index+'].fund" '+
							'value="'+fund+'"/>'+
							'<input type="hidden" id="assignments['+index+'].function" name="assignments['+index+'].function" '+
							'value="'+ftn+'"/>'+
							'<input type="hidden" id="assignments['+index+'].functionary" name="assignments['+index+'].functionary" '+
							'value="'+functionary+'"/>'+
							'<input type="hidden" id="assignments['+index+'].grade" name="assignments['+index+'].grade" '+
							'value="'+grade+'"/>'+hodInput+
						'</td>'+	
						'<td>'+	
							'<span class="add-padding"><i id="edit_row" class="fa fa-edit" value="'+index+'"></i></span>'+del+
						'</td>'+	
					'</tr>';	
		
		$("#assignmentTable").append(text);
		$("#table_date_range"+index+"").val($("#fromDate").val() + " - "+$("#toDate").val());
		$("#table_primary"+index+"").val($("#primary_yes").prop("checked")?"Yes":"No");
		$("#table_department"+index+"").val($("#deptId").find('option:selected').text());
		$("#table_designation"+index+"").val($("#designationName").val());
		$("#table_position"+index+"").val($("#positionName").val());
	}
	
	$(document).on('click',"#delete_row",function (){
		if(!$("#removedassignIds").val()==""){
			$("#removedassignIds").val($("#removedassignIds").val()+",");
		}
		$("#removedassignIds").val($("#removedassignIds").val()+$("#table_assignid"+$(this).attr("value")+"").val());
		$(this).closest('tr').remove();
	});
	
	$(document).on('click',"#edit_row",function (){
		edit = true;
		deleteRow = $(this).closest('tr');
		editedRowIndex =$(this).attr("value");
		var primary = document.getElementById("assignments["+editedRowIndex+"].primary").value;
		var fromDate = document.getElementById("assignments["+editedRowIndex+"].fromDate").value;
		var toDate = document.getElementById("assignments["+editedRowIndex+"].toDate").value;
		var dept = document.getElementById("assignments["+editedRowIndex+"].department").value;
		var desig = document.getElementById("assignments["+editedRowIndex+"].designation").value;
		var desigName = document.getElementById("table_designation"+editedRowIndex).value;
		var pos = document.getElementById("assignments["+editedRowIndex+"].position").value;
		var posName = document.getElementById("table_position"+editedRowIndex).value;
		var fund = document.getElementById("assignments["+editedRowIndex+"].fund").value;
		var ftn = document.getElementById("assignments["+editedRowIndex+"].function").value;
		var functionary = document.getElementById("assignments["+editedRowIndex+"].functionary").value;
		var grade = document.getElementById("assignments["+editedRowIndex+"].grade").value;
		
		if(primary=="true"){
			$("#primary_yes").prop("checked",true);
			$("#primary_no").prop("checked",false);
		}
		if(primary=="false"){
			$("#primary_yes").prop("checked",false);
			$("#primary_no").prop("checked",true);
		}		
		$("#fromDate").val(fromDate);
		$("#toDate").val(toDate);
		$("#deptId").val(dept);
		$("#designationId").val(desig);
		$("#designationName").val(desigName);
		$("#positionId").val(pos);
		$("#positionName").val(posName);
		$("#fundId").val(fund);
		$("#functionId").val(ftn);
		$("#functionaryId").val(functionary);
		$("#grade").val(grade);
		if(null!=document.getElementById("hodIds"+editedRowIndex)) {
			var hodIds = document.getElementById("hodIds").value;
			if(null!=hodIds){
				var dataArray = hodIds.split(","); 
				$("#hodDeptId").val(dataArray);
				$("#isHodYes").prop("checked",true);
				$("#isHodNo").prop("checked",false);
				$('#hodDeptDiv').show();
			}	
		}	
	});
	
	$("#isHodYes").click(function () {
		$('#hodDeptDiv').show();
	});
	
	$("#isHodNo").click(function () {
		$('#hodDeptDiv').hide();
		$("#hodDeptId").find('option').attr('selected', false);
	});
	
	$("#primary_yes").click(function () {
		resetAssignmentValues();
	});
	
	$("#primary_no").click(function () {
		resetAssignmentValues();
		$("#primary_no").prop("checked",true);
		$("#primary_yes").prop("checked",false);
	});
	
	$("#submit").click(function () {
		var count = 0;
        jQuery(".table-bordered tr").find('input').each(function(){
                if(jQuery(this).val() == "true"){
                        count++;
                }
        });
/*		if($("#assignmentTable tr").length==1 || count == 0 ){
			$('.assignmentserror').html('At least one primary assignment should be entered ').show().fadeOut(5000);
			$('.fromdateerror').hide();
			$('.departmenterror').hide();
			$('.designationerror').hide();
			$('.positionerror').hide();
			$('.todateerror').hide();
			$('.fromdateerror').focus();
			return false;
		}*/
        
       /* if(count == 0){
        	$('.assignmentserror').html('At least one primary assignment should be entered ').show().fadeOut(25000);
			$('.fromdateerror').hide();
			$('.departmenterror').hide();
			$('.designationerror').hide();
			$('.positionerror').hide();
			$('.todateerror').hide();
			$('.fromdateerror').focus();
			return false;
        }	*/
	});

	
	function validateJurisdiction() {
		$('.boundaryTypeerror').hide();
		$('.boundaryerror').hide();
		$('.duplicatejurisdictionerror').hide();
		if(($("#jurisdictionTable tr").length-1)>=1){
			for(var i=0;i<$("#jurisdictionTable tr").length-1;i++) {
				if(($('#table_boundaryType'+i).val()).localeCompare($("#boundaryTypeId").find('option:selected').text())==0){
					if(($('#table_boundary'+i).val()).localeCompare($("#boundarySelect").find('option:selected').text())==0){
						$('.duplicatejurisdictionerror').html('Already this jurisdiction combination exist').show();
						return false;
					}
				}
			}
		}
		$('.boundaryTypeerror').hide();
		$('.boundaryerror').hide();
		var boundaryTypeId = $("#boundaryTypeId").val();
		var boundaryId =$("#boundarySelect").val();
		var validate = true;
		if(null==boundaryTypeId || ''==boundaryTypeId){
			$('.boundaryTypeerror').html('Boundary Type is required').show();
			validate = false;
		}
		if(null==boundaryId || ''==boundaryId) {
			$('.boundaryerror').html('Boundary is required').show();
			validate = false;
		}
		return validate;
	}
	var jurdctnrowCount=0;
	var jurdctnedit=false;
	var jurdctndeleteRow="";
	var jurdctneditedRowIndex="";
	$("#btn-addJurdctn").click(function() {
		if(validateJurisdiction()) {
			if(!jurdctnedit){
				jurdctnrowCount = $("#jurisdictionTable tr").length-1;
				jurdctnaddRow(jurdctnrowCount);
				jurdctnrowCount++;
			}
			else{
				jurdctndeleteRow.remove();
				jurdctnaddRow(jurdctneditedRowIndex);		
				jurdctnedit=false;
			}
			resetJurisdictionValues();
		}
	});
	function resetJurisdictionValues() {
		if(!jurdctnedit) {
			$("#boundaryTypeId").val("");
			$("#boundarySelect").val("");
		}	
	}
	function jurdctnaddRow(index) {
		var del="";
			del='<span class="add-padding"><i id="jurdctndelete_row" class="fa fa-remove"></i></span>';
		var text = 
					'<tr>'+
						'<td>'+
							'<input type="hidden" id="jurisdictions['+index+'].boundaryType" name="jurisdictions['+index+'].boundaryType" '+
							'value="'+$("#boundaryTypeId").val()+'"/>'+
							'<input type="text" id="table_boundaryType'+index+'" class="form-control" readonly="readonly" style="text-align:center"/>'+
						'</td>'+	
						'<td>'+
							'<input type="hidden" id="jurisdictions['+index+'].boundary" name="jurisdictions['+index+'].boundary" '+
							'value="'+$("#boundarySelect").val()+'"/>'+
							'<input type="text" id="table_boundary'+index+'" class="form-control" readonly="readonly" style="text-align:center"/>'+
						'</td>'+	
						'<td>'+	
							'<span class="add-padding"><i id="jurdctnedit_row" class="fa fa-edit" value="'+index+'"></i></span>'+del+
						'</td>'+	
					'</tr>';	
		$("#jurisdictionTable").append(text);
		$("#table_boundaryType"+index+"").val($("#boundaryTypeId").find('option:selected').text());
		$("#table_boundary"+index+"").val($("#boundarySelect").find('option:selected').text());
	}

	$(document).on('click',"#jurdctndelete_row",function (){
		if(!$("#removedJurisdictionIds").val()==""){
			$("#removedJurisdictionIds").val($("#removedJurisdictionIds").val()+",");
		}
		$("#removedJurisdictionIds").val($("#removedJurisdictionIds").val()+$("#table_jurisdictionid"+$(this).attr("value")+"").val());
		$(this).closest('tr').remove();
	});

	$(document).on('click',"#jurdctnedit_row",function (){
		jurdctnedit = true;
		jurdctndeleteRow = $(this).closest('tr');
		jurdctneditedRowIndex =$(this).attr("value");
		var boundaryType = document.getElementById("jurisdictions["+jurdctneditedRowIndex+"].boundaryType").value;
		var boundary = document.getElementById("jurisdictions["+jurdctneditedRowIndex+"].boundary").value;
		var boundaryTypeName = document.getElementById("table_boundaryType"+jurdctneditedRowIndex).value;
		var boundaryName = document.getElementById("table_boundary"+jurdctneditedRowIndex).value;
		populateboundarySelect({
			boundaryTypeId : boundaryType
		});
		$("#boundaryTypeId").val(boundaryType);
		$("#boundarySelect").val(boundary);
	});

});

function populateBoundary(dropdown) {
	populateboundarySelect({
		boundaryTypeId : dropdown.value
	});
}
