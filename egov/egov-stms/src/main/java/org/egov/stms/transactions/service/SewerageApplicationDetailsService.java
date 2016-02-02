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
package org.egov.stms.transactions.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.egov.stms.masters.entity.enums.SewerageConnectionStatus;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageConnection;
import org.egov.stms.transactions.repository.SewerageApplicationDetailsRepository;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SewerageApplicationDetailsService {

    protected SewerageApplicationDetailsRepository sewerageApplicationDetailsRepository;
    private static final Logger LOG = LoggerFactory.getLogger(SewerageApplicationDetailsService.class);

    @Autowired
    public SewerageApplicationDetailsService(final SewerageApplicationDetailsRepository sewerageApplicationDetailsRepository) {
        this.sewerageApplicationDetailsRepository = sewerageApplicationDetailsRepository;
    }

    public SewerageApplicationDetails findBy(final Long id) {
        return sewerageApplicationDetailsRepository.findOne(id);
    }

    public List<SewerageApplicationDetails> findAll() {
        return sewerageApplicationDetailsRepository
                .findAll(new Sort(Sort.Direction.ASC, SewerageTaxConstants.APPLICATION_NUMBER));
    }

    public SewerageApplicationDetails findByApplicationNumber(final String applicationNumber) {
        return sewerageApplicationDetailsRepository.findByApplicationNumber(applicationNumber);
    }

    public SewerageApplicationDetails load(final Long id) {
        return sewerageApplicationDetailsRepository.getOne(id);
    }

    public Page<SewerageApplicationDetails> getListSewerageApplicationDetails(final Integer pageNumber, final Integer pageSize) {
        final Pageable pageable = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC,
                SewerageTaxConstants.APPLICATION_NUMBER);
        return sewerageApplicationDetailsRepository.findAll(pageable);
    }

    @Transactional
    public SewerageApplicationDetails create(final SewerageApplicationDetails sewerageApplicationDetails) {
        final SewerageApplicationDetails savedSewerageApplicationDetails = sewerageApplicationDetailsRepository
                .save(sewerageApplicationDetails);
        return savedSewerageApplicationDetails;
    }

    @Transactional
    public SewerageApplicationDetails update(final SewerageApplicationDetails sewerageApplicationDetails) {
        final SewerageApplicationDetails updatedSewerageApplicationDetails = sewerageApplicationDetailsRepository
                .save(sewerageApplicationDetails);
        return updatedSewerageApplicationDetails;
    }

    @Transactional
    public void save(final SewerageApplicationDetails detail) {
        sewerageApplicationDetailsRepository.save(detail);
    }

    public SewerageApplicationDetails findByDhscNumberAndConnectionStatus(final String dhscNumber,
            final SewerageConnectionStatus connectionStatus) {
        return sewerageApplicationDetailsRepository.findByConnection_DhscNumberAndConnection_ConnectionStatus(dhscNumber,
                connectionStatus);
    }

    public SewerageApplicationDetails findByConnection(final SewerageConnection sewerageConnection) {
        return sewerageApplicationDetailsRepository.findByConnection(sewerageConnection);
    }

    public SewerageApplicationDetails getActiveApplicationDetailsByConnection(final SewerageConnection sewerageConnection) {
        return sewerageApplicationDetailsRepository.findByConnectionAndConnection_ConnectionStatus(sewerageConnection,
                SewerageConnectionStatus.ACTIVE);
    }

    public Date getDisposalDate(final SewerageApplicationDetails sewerageApplicationDetails, final Integer appProcessTime) {
        final Calendar c = Calendar.getInstance();
        c.setTime(sewerageApplicationDetails.getApplicationDate());
        c.add(Calendar.DATE, appProcessTime);
        return c.getTime();
    }

    public SewerageApplicationDetails getSewerageConnectionByAssessmentNumberAndConnectionStatus(final String propertyIdentifier,
            final SewerageConnectionStatus connectionStatus) {
        return sewerageApplicationDetailsRepository
                .findByConnection_PropertyIdentifierAndConnection_ConnectionStatus(propertyIdentifier, connectionStatus);
    }

}
