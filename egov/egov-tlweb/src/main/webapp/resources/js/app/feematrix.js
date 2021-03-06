
$(document).ready(function()
{

$('#unitOfMeasurement').attr("disabled", true); 
	
$('#feeType').click(function(event){
	if($('#subCategory').val()==""){
		bootbox.alert("Please Choose Sub Category");
		return false;
	}
});

$('#unitOfMeasurement').click(function(event){
	if($('#feeType').val()==""){
		bootbox.alert("Please Choose Fee Type");
		return false;
	}
});


$('#subCategory').change(function(){ 
	console.log("came onchange of "+$('#subCategory').val());
	$('#unitOfMeasurement').empty();
	$('#unitOfMeasurement').append($("<option value=''>Select</option>"));
	$('#rateType').val("");
	$.ajax({
		url: "/tl/domain/commonAjax-ajaxPopulateFeeType.action?subCategoryId="+$('#subCategory').val()+"",
		type: "GET",
		dataType: "json",
		success: function (response) {
			console.log("success"+JSON.stringify(response) );
			$('#feeType').empty();
			
			$('#feeType').append($("<option value=''>Select</option>"));
			$.each(response.Result, function(index, value) {
				
			     $('#feeType').append("<option value="+value.Value+">"+value.Text+"</option>");
			});
			console.log("completed"+response);
			
		}, 
		error: function (response) {
			console.log("failed");
		}
	});
});

$('#feeType').change(function(){
	console.log("came onchange of "+$('#feeType').val());
	var subCategoryValue=$('#subCategory').val();
	$.ajax({
		url: "/tl/domain/commonAjax-ajaxPopulateUom.action?feeTypeId="+$('#feeType').val()+"&subCategoryId="+$('#subCategory').val()+"",
		type: "GET",
		dataType: "json",
		success: function (response) {
			console.log("success"+JSON.stringify(response) );
			$('#unitOfMeasurement').empty();
			$('#unitOfMeasurement').append($("<option value=''>Select</option>"));
			$.each(response.Result, function(index, value) {
			     $('#unitOfMeasurement').append("<option value="+value.uomId+">"+value.uomName+"</option>");
			     $('#rateType').val(value.rateType);
			});
			console.log("completed"+response);
			$("#unitOfMeasurement").prop("selectedIndex",1);
		}, 
		error: function (response) {
			console.log("failed"+response);
		} 
	});
});
	
$('#licenseCategory').change(function(){
		console.log("came onchange of "+$('#licenseCategory').val());
		$('#feeType').empty();
		$('#feeType').append($("<option value=''>Select</option>"));
		$('#unitOfMeasurement').empty();
		$('#unitOfMeasurement').append($("<option value=''>Select</option>"));
		$('#rateType').val("");
		$.ajax({
			url: "/tl/domain/commonAjax-ajaxPopulateSubCategory.action?categoryId="+$('#licenseCategory').val()+"",
			type: "GET",
			dataType: "json",
			success: function (response) {
				console.log("success"+JSON.stringify(response) );
				$('#subCategory').empty();
				
				$('#subCategory').append($("<option value=''>Select</option>"));
				$.each(response.Result, function(index, value) {
					
				     $('#subCategory').append("<option value="+value.Value+">"+value.Text+"</option>");
				});
				console.log("completed"+response);
				
			}, 
			error: function (response) {
				console.log("failed");
			}
		});
	});

});

/*$( "#add-row" ).click(function( event ) {
	bootbox.alert( "add-row event called." );
	  $(this).closest("tr").remove(); // remove row
	    return false;
});*/

$( "#add-row" ).click(function( event ) {
	var rowCount = $('#result tr').length;
	if(!checkforNonEmptyPrevRow())      
		return false;
	var prevUOMFromVal=getPrevUOMFromData();
	var content= $('#resultrow0').html();
	resultContent=   content.replace(/0/g,rowCount-1);   
	$(resultContent).find("input").val("");
	$('#result > tbody:last').append("<tr>"+resultContent+"</tr>"); 
	$('#result tr:last').find("input").val("");   
	intiUOMFromData(prevUOMFromVal);  
	patternvalidation(); 
});    

$( "#save" ).click(function( event ) {
if(!validateDetailsBeforeSubmit()){
	return false;
} 
var natureOfBusinessDisabled=$('#natureOfBusiness').is(':disabled');
var licenseAppTypeDisabled=$('#licenseAppType').is(':disabled');
var unitOfMeasurementDisabled=$('#unitOfMeasurement').is(':disabled');
	$('#natureOfBusiness').removeAttr("disabled");
	$('#licenseAppType').removeAttr("disabled");
	$('#unitOfMeasurement').removeAttr("disabled");
	var fd=$('#feematrix-new').serialize();
	
	  $.ajax({
			url: "/tl/feematrix/create",
			type: "POST",
			data: fd,
			//dataType: "text",
			success: function (response) {
				console.log("success"+response );
				 $('#resultdiv').html(response);
				 if(natureOfBusinessDisabled)
					 $('#natureOfBusiness').attr("disabled", true); 
				 if(licenseAppTypeDisabled)
					 $('#licenseAppType').attr("disabled", true); 
				 if(unitOfMeasurementDisabled)
					 $('#unitOfMeasurement').attr("disabled", true); 
				 bootbox.alert("Details saved Successfully");
			}, 
			error: function (response) {
				console.log("failed");
				if(natureOfBusinessDisabled)
					$('#natureOfBusiness').attr("disabled", true); 
				if(licenseAppTypeDisabled)
					$('#licenseAppType').attr("disabled", true);
				if(unitOfMeasurementDisabled)
					 $('#unitOfMeasurement').attr("disabled", true); 
				bootbox.alert("Failed to Save Details");
			}
		});
});
