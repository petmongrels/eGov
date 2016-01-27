/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 *
 * Copyright (C) <2015> eGovernments Foundation
 *
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 *
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 *
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 *
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 *
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 *
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.stms.transactions.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.demand.model.EgDemand;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.stms.masters.entity.enums.PropertyType;

@Entity
@Table(name = "egswtax_connection_history")
@SequenceGenerator(name = SewerageConnectionHistory.SEQ_CONNECTION_HISTORY, sequenceName = SewerageConnectionHistory.SEQ_CONNECTION_HISTORY, allocationSize = 1)
public class SewerageConnectionHistory extends AbstractAuditable {

    private static final long serialVersionUID = -582076078407295165L;

    public static final String SEQ_CONNECTION_HISTORY = "SEQ_EGSWTAX_CONNECTION_HISTORY";

    @Id
    @GeneratedValue(generator = SEQ_CONNECTION_HISTORY, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private SewerageConnection connection;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Column(name = "noofclosets_residential")
    private Integer noOfClosetsResidential;

    @Column(name = "noofclosets_nonresidential")
    private Integer noOfClosetsNonResidential;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand")
    private EgDemand demand;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public SewerageConnection getConnection() {
        return connection;
    }

    public void setConnection(final SewerageConnection connection) {
        this.connection = connection;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getNoOfClosetsResidential() {
        return noOfClosetsResidential;
    }

    public void setNoOfClosetsResidential(final Integer noOfClosetsResidential) {
        this.noOfClosetsResidential = noOfClosetsResidential;
    }

    public Integer getNoOfClosetsNonResidential() {
        return noOfClosetsNonResidential;
    }

    public void setNoOfClosetsNonResidential(final Integer noOfClosetsNonResidential) {
        this.noOfClosetsNonResidential = noOfClosetsNonResidential;
    }

    public EgDemand getDemand() {
        return demand;
    }

    public void setDemand(final EgDemand demand) {
        this.demand = demand;
    }
}