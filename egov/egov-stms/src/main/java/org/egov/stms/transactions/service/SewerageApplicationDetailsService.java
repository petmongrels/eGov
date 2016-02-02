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

import org.egov.infra.utils.ApplicationNumberGenerator;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.stms.masters.entity.enums.SewerageConnectionStatus;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageConnection;
import org.egov.stms.transactions.repository.SewerageApplicationDetailsRepository;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.masters.service.DocumentNamesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SewerageApplicationDetailsService {

    @Autowired
    private DocumentNamesService documentNamesService;

    @Autowired
    private SewerageTaxUtils sewerageTaxUtils;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    protected SewerageApplicationDetailsRepository sewerageApplicationDetailsRepository;

    @Autowired
    private ApplicationNumberGenerator applicationNumberGenerator;

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
        if (sewerageApplicationDetails.getApplicationNumber() == null)
            sewerageApplicationDetails.setApplicationNumber(applicationNumberGenerator.generate());
        sewerageApplicationDetails.setApplicationDate(new Date());
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

    public SewerageApplicationDetails getPrimaryConnectionDetailsByPropertyIdentifier(final String propertyIdentifier) {
        return sewerageApplicationDetailsRepository.getPrimaryConnectionDetailsByPropertyID(propertyIdentifier);
    }

    public String checkValidPropertyAssessmentNumber(final String asessmentNumber) {
        String errorMessage = "";
        final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(asessmentNumber,
                PropertyExternalService.FLAG_FULL_DETAILS);
        errorMessage = validateProperty(assessmentDetails);
        if (errorMessage.isEmpty())
            errorMessage = validatePTDue(asessmentNumber, assessmentDetails);
        return errorMessage;
    }

    /**
     * @param assessmentDetails
     * @return ErrorMessage If PropertyId is Not Valid
     */
    private String validateProperty(final AssessmentDetails assessmentDetails) {
        String errorMessage = "";
        if (assessmentDetails.getErrorDetails() != null && assessmentDetails.getErrorDetails().getErrorCode() != null)
            errorMessage = assessmentDetails.getErrorDetails().getErrorMessage();
        return errorMessage;
    }

    private String validatePTDue(final String asessmentNumber, final AssessmentDetails assessmentDetails) {
        String errorMessage = "";
        if (assessmentDetails.getPropertyDetails() != null
                && assessmentDetails.getPropertyDetails().getTaxDue() != null
                && assessmentDetails.getPropertyDetails().getTaxDue().doubleValue() > 0)

            /**
             * If property tax due present and configuration value is 'NO' then restrict not to allow new water tap connection
             * application. If configuration value is 'YES' then new water tap connection can be created even though there is
             * Property Tax Due present.
             **/
            if (!sewerageTaxUtils.isNewConnectionAllowedIfPTDuePresent())
                errorMessage = messageSource.getMessage("err.validate.property.taxdue", new String[] {
                        assessmentDetails.getPropertyDetails().getTaxDue().toString(), asessmentNumber, "new" }, null);
        return errorMessage;
    }

    public String checkConnectionPresentForProperty(final String propertyID) {
        String validationMessage = "";
        /**
         * Validate only if configuration value is 'NO' for multiple new connection per property allowed or not. If configuration
         * value is 'YES' then multiple new connections are allowed. This will impact on the Additional connection feature.
         **/
        if (!sewerageTaxUtils.isMultipleNewConnectionAllowedForPID()) {
            final SewerageApplicationDetails sewerageApplicationDetails = getPrimaryConnectionDetailsByPropertyIdentifier(propertyID);
            if (sewerageApplicationDetails != null)
                if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                        .equalsIgnoreCase(ConnectionStatus.ACTIVE.toString()))
                    validationMessage = messageSource.getMessage("err.validate.newconnection.active", new String[] {
                            sewerageApplicationDetails.getConnection().getDhscNumber(), propertyID }, null);
                else if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                        .equalsIgnoreCase(ConnectionStatus.INPROGRESS.toString()))
                    validationMessage = messageSource.getMessage("err.validate.newconnection.application.inprocess",
                            new String[] { propertyID, sewerageApplicationDetails.getApplicationNumber() }, null);
                else if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                        .equalsIgnoreCase(ConnectionStatus.DISCONNECTED.toString()))
                    validationMessage = messageSource
                            .getMessage("err.validate.newconnection.disconnected", new String[] {
                                    sewerageApplicationDetails.getConnection().getDhscNumber(), propertyID }, null);
                else if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                        .equalsIgnoreCase(ConnectionStatus.CLOSED.toString()))
                    validationMessage = messageSource
                            .getMessage("err.validate.newconnection.closed", new String[] {
                                    sewerageApplicationDetails.getConnection().getDhscNumber(), propertyID }, null);
                else if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                        .equalsIgnoreCase(ConnectionStatus.HOLDING.toString()))
                    validationMessage = messageSource.getMessage("err.validate.newconnection.holding", new String[] {
                            sewerageApplicationDetails.getConnection().getDhscNumber(), propertyID }, null);
        }
        return validationMessage;
    }

}
