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
package org.egov.demand.interfaces;

import java.math.BigDecimal;
import java.util.Date;

public interface LatePayPenaltyCalculator {
	public enum LPPenaltyCalcType {
		SIMPLE, COMPOUND
	};

	BigDecimal getLPPPercentage();

	LPPenaltyCalcType getLPPenaltyCalcType();

	void setPenaltyCalcType(LPPenaltyCalcType penaltyType);

	/**
	 * Calculates the Late pay penalty for given amount b/w given period with
	 * interest rate (interest rate will be taken from getPercentage() API)
	 * 
	 * @param fromDate - from date to calculate interest
	 * @param toDate - to date to calculate interest
	 * @param amount - amount for which interest to be calculated
	 * @return Interest amount.
	 */
	BigDecimal calculateLPPenaltyForPeriod(Date fromDate, Date toDate,
			BigDecimal amount);

	/**
	 * Calculates the Late pay penalty for given amount from given date to Till date with
	 * interest rate (interest rate will be taken from getPercentage() API)
	 * 
	 * @param fromDate - from date to calculate interest
	 * @param amount - amount for which interest to be calculated
	 * @return Interest amount.
	 */
	BigDecimal calculatePenalty(Date latestCollectedRcptDate, Date fromDate, BigDecimal amount);

}
