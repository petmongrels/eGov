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
package org.egov.works.lineestimate.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "EGW_LINEESTIMATE_DETAILS")
@Unique(id = "id", tableName = "EGW_LINEESTIMATE_DETAILS", columnName = { "estimatenumber" }, fields = {
        "estimateNumber" }, enableDfltMsg = true)
@SequenceGenerator(name = LineEstimateDetails.SEQ_EGW_LINEESTIMATE_DETAILS, sequenceName = LineEstimateDetails.SEQ_EGW_LINEESTIMATE_DETAILS, allocationSize = 1)
public class LineEstimateDetails extends AbstractAuditable {

    private static final long serialVersionUID = -788818018131193299L;

    public static final String SEQ_EGW_LINEESTIMATE_DETAILS = "SEQ_EGW_LINEESTIMATE_DETAILS";

    @Id
    @GeneratedValue(generator = SEQ_EGW_LINEESTIMATE_DETAILS, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineestimate", nullable = false)
    private LineEstimate lineEstimate;

    @NotNull
    @SafeHtml
    @Length(max = 1024)
    private String nameOfWork;

    @NotNull
    private BigDecimal estimateAmount;

    @NotNull
    @SafeHtml
    @Length(max = 50)
    @Column(unique = true)
    private String estimateNumber;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public LineEstimate getLineEstimate() {
        return lineEstimate;
    }

    public void setLineEstimate(final LineEstimate lineEstimate) {
        this.lineEstimate = lineEstimate;
    }

    public String getNameOfWork() {
        return nameOfWork;
    }

    public void setNameOfWork(final String nameOfWork) {
        this.nameOfWork = nameOfWork;
    }

    public BigDecimal getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(final BigDecimal estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public String getEstimateNumber() {
        return estimateNumber;
    }

    public void setEstimateNumber(final String estimateNumber) {
        this.estimateNumber = estimateNumber;
    }

}
