/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
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
 */
package org.egov.bpa.models.extd;

import org.egov.bpa.models.extd.masters.CheckListDetailsExtn;

public class DocketViolations {

	private Docket docket;
	protected Long id;
	private String value;
	private String remarks;
	private CheckListDetailsExtn checkListDetails;
	private String required;
	private String provided;
	private String extentOfViolation;
	private String percentageOfViolation;
	
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	public String getProvided() {
		return provided;
	}
	public void setProvided(String provided) {
		this.provided = provided;
	}
	public String getExtentOfViolation() {
		return extentOfViolation;
	}
	public void setExtentOfViolation(String extentOfViolation) {
		this.extentOfViolation = extentOfViolation;
	}
	public String getPercentageOfViolation() {
		return percentageOfViolation;
	}
	public void setPercentageOfViolation(String percentageOfViolation) {
		this.percentageOfViolation = percentageOfViolation;
	}
	public Docket getDocket() {
		return docket;
	}
	public void setDocket(Docket docket) {
		this.docket = docket;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public CheckListDetailsExtn getCheckListDetails() {
		return checkListDetails;
	}
	public void setCheckListDetails(CheckListDetailsExtn checkListDetails) {
		this.checkListDetails = checkListDetails;
	}
	
	
}
