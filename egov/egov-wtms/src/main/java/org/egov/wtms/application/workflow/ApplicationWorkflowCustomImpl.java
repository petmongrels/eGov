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
package org.egov.wtms.application.workflow;

import static org.egov.wtms.utils.constants.WaterTaxConstants.WFLOW_ACTION_STEP_REJECT;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WFLOW_ACTION_STEP_THIRDPARTY_CREATED;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WF_STATE_REJECTED;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.eis.service.PositionMasterService;
import org.egov.infra.admin.master.entity.Role;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.workflow.WorkFlowMatrix;
import org.egov.pims.commons.Position;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.enums.BasicPropertyStatus;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.application.service.WaterConnectionSmsAndEmailService;
import org.egov.wtms.elasticSearch.service.ConsumerIndexService;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.utils.PropertyExtnUtils;
import org.egov.wtms.utils.WaterTaxUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.elasticsearch.common.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class ApplicationCommonWorkflow.
 */
public abstract class ApplicationWorkflowCustomImpl implements ApplicationWorkflowCustom {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationWorkflowCustomImpl.class);

    @Autowired
    private WaterConnectionDetailsRepository waterConnectionDetailsRepository;

    @Autowired
    private PropertyExtnUtils propertyExtnUtils;

    @Autowired
    private EisCommonService eisCommonService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private PositionMasterService positionMasterService;

    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    private ConsumerIndexService consumerIndexService;

    @Autowired
    private UserService userService;

    @Autowired
    private WaterConnectionSmsAndEmailService waterConnectionSmsAndEmailService;

    @Autowired
    private SimpleWorkflowService<WaterConnectionDetails> waterConnectionWorkflowService;

    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;

    @Autowired
    public ApplicationWorkflowCustomImpl() {

    }

    @Override
    public void createCommonWorkflowTransition(final WaterConnectionDetails waterConnectionDetails,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction) {
        if (LOG.isDebugEnabled())
            LOG.debug(" Create WorkFlow Transition Started  ...");
        final User user = securityUtils.getCurrentUser();
        final DateTime currentDate = new DateTime();
        User currentUser = null;
        final Assignment userAssignment = assignmentService.getPrimaryAssignmentForUser(user.getId());
        Position pos = null;
        Assignment wfInitiator = null;
        final Boolean recordCreatedBYNonEmployee = waterTaxUtils.getCurrentUserRole(waterConnectionDetails
                .getCreatedBy());
        String currState = "";
        final String natureOfwork = getNatureOfTask(waterConnectionDetails);
        if (recordCreatedBYNonEmployee) {
            currState = WFLOW_ACTION_STEP_THIRDPARTY_CREATED;
            if (!waterConnectionDetails.getStateHistory().isEmpty())
                wfInitiator = assignmentService.getPrimaryAssignmentForPositon(waterConnectionDetails.getStateHistory()
                        .get(0).getOwnerPosition().getId());
        } else if (null != waterConnectionDetails.getId()) {
            currentUser = userService.getUserById(waterConnectionDetails
                    .getCreatedBy().getId());
            if (currentUser != null && waterConnectionDetails.getLegacy().equals(true)) {
                for (final Role userrole : currentUser.getRoles())
                    if (userrole.getName().equals(WaterTaxConstants.ROLE_SUPERUSER)) {
                        final Position positionuser = waterTaxUtils.getZonalLevelClerkForLoggedInUser(waterConnectionDetails
                                .getConnection().getPropertyIdentifier());
                        if (positionuser != null)
                            wfInitiator = assignmentService.getPrimaryAssignmentForPositionAndDate(positionuser.getId(),
                                    new Date());
                        break;
                    }
            } else
                wfInitiator = assignmentService.getPrimaryAssignmentForUser(waterConnectionDetails.getCreatedBy().getId());
        }
        if (WFLOW_ACTION_STEP_REJECT.equalsIgnoreCase(workFlowAction)) {
            if (wfInitiator.equals(userAssignment)) {
                waterConnectionDetails.setConnectionStatus(ConnectionStatus.INACTIVE);
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_CANCELLED, WaterTaxConstants.MODULETYPE));
                waterConnectionDetails.transition(true).end().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent).withDateInfo(currentDate.toDate()).withNatureOfTask(natureOfwork);
                waterConnectionSmsAndEmailService.sendSmsAndEmailOnRejection(waterConnectionDetails, approvalComent);
                waterConnectionDetailsService.updateIndexes(waterConnectionDetails, null);
            } else {
                final String stateValue = WF_STATE_REJECTED;
                waterConnectionDetails.transition(true).withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent)
                        .withStateValue(stateValue).withDateInfo(currentDate.toDate())
                        .withOwner(wfInitiator.getPosition()).withNextAction("Application Rejected")
                        .withNatureOfTask(natureOfwork);
            }
        } else {
            if (null != approvalPosition && approvalPosition != -1 && !approvalPosition.equals(Long.valueOf(0)))
                pos = positionMasterService.getPositionById(approvalPosition);
            WorkFlowMatrix wfmatrix = null;
            if (null == waterConnectionDetails.getState()) {
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                        null, additionalRule, currState, null);
                waterConnectionDetails.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent)
                        .withStateValue(wfmatrix.getNextState()).withDateInfo(new Date()).withOwner(pos)
                        .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(natureOfwork);
            } else if (WaterTaxConstants.WF_STATE_TAP_EXECUTION_DATE_BUTTON.equalsIgnoreCase(workFlowAction)) {
                if (null != workFlowAction
                        && !workFlowAction.isEmpty()
                        && workFlowAction.equalsIgnoreCase(WaterTaxConstants.WF_STATE_TAP_EXECUTION_DATE_BUTTON)
                        && waterConnectionDetails.getApplicationType().getCode()
                                .equalsIgnoreCase(WaterTaxConstants.CHANGEOFUSE)) {
                    final WaterConnectionDetails connectionToBeDeactivated = waterConnectionDetailsRepository
                            .findByConnection_ConsumerCodeAndConnectionStatus(waterConnectionDetails.getConnection()
                                    .getConsumerCode(), ConnectionStatus.ACTIVE);
                    connectionToBeDeactivated.setConnectionStatus(ConnectionStatus.INACTIVE);
                    connectionToBeDeactivated.setIsHistory(true);
                    waterConnectionDetailsRepository.saveAndFlush(connectionToBeDeactivated);
                    // waterConnectionDetailsService.updateIndexes(connectionToBeDeactivated);
                }
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                        null, additionalRule, waterConnectionDetails.getCurrentState().getValue(), null);
                final AssessmentDetails assessmentDetailsFullFlag = propertyExtnUtils.getAssessmentDetailsForFlag(
                        waterConnectionDetails.getConnection().getPropertyIdentifier(),
                        PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
                waterConnectionDetails.setStatus(waterTaxUtils.getStatusByCodeAndModuleType(
                        WaterTaxConstants.APPLICATION_STATUS_SANCTIONED, WaterTaxConstants.MODULETYPE));
                waterConnectionDetailsService.updateIndexes(waterConnectionDetails, null);
                if (waterConnectionDetails.getApplicationType().getCode()
                        .equalsIgnoreCase(WaterTaxConstants.CHANGEOFUSE)) {
                    final BigDecimal amountTodisplayInIndex = waterConnectionDetailsService
                            .getTotalAmount(waterConnectionDetails);
                    waterConnectionDetails.setConnectionStatus(ConnectionStatus.ACTIVE);
                    consumerIndexService.createConsumerIndex(waterConnectionDetails, assessmentDetailsFullFlag,
                            amountTodisplayInIndex);
                }
                if (wfmatrix.getNextAction().equalsIgnoreCase("END"))
                    waterConnectionDetails.transition(true).end().withSenderName(user.getUsername() + "::" + user.getName())
                            .withComments(approvalComent).withDateInfo(currentDate.toDate()).withNatureOfTask(natureOfwork);
            } else if (null != approvalComent && "Receipt Cancelled".equalsIgnoreCase(approvalComent)) {
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                        null, additionalRule, "Asst engg approved", null);
                waterConnectionDetails.transition(true).withSenderName(user.getUsername() + "::" + user.getName())
                        .withComments(approvalComent)
                        .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate()).withOwner(pos)
                        .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(natureOfwork);
            } else if ((additionalRule.equals(WaterTaxConstants.WORKFLOW_CLOSUREADDITIONALRULE) || additionalRule
                    .equals(WaterTaxConstants.RECONNECTIONCONNECTION))
                    && (waterConnectionDetails.getCurrentState().getValue().equals("Closed") ||
                    waterConnectionDetails.getCurrentState().getValue().equals("END"))) {
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                        null, additionalRule, null, null);
                if (wfmatrix != null && !wfmatrix.getNextAction().equalsIgnoreCase("END"))
                    waterConnectionDetails.transition(true).withSenderName(user.getUsername() + "::" + user.getName())
                            .withComments(approvalComent)
                            .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate()).withOwner(pos)
                            .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(natureOfwork);
            }
            else {
                wfmatrix = waterConnectionWorkflowService.getWfMatrix(waterConnectionDetails.getStateType(), null,
                        null, additionalRule, waterConnectionDetails.getCurrentState().getValue(), null);
                if ((additionalRule.equals(WaterTaxConstants.WORKFLOW_CLOSUREADDITIONALRULE) || additionalRule
                        .equals(WaterTaxConstants.RECONNECTIONCONNECTION))
                        && wfmatrix != null && wfmatrix.getNextAction().equalsIgnoreCase("END"))
                    waterConnectionDetails.transition(true).end().withSenderName(user.getUsername() + "::" + user.getName())
                            .withComments(approvalComent).withDateInfo(currentDate.toDate()).withNatureOfTask(natureOfwork);
                else
                    waterConnectionDetails.transition(true).withSenderName(user.getUsername() + "::" + user.getName())
                            .withComments(approvalComent)
                            .withStateValue(wfmatrix.getNextState()).withDateInfo(currentDate.toDate()).withOwner(pos)
                            .withNextAction(wfmatrix.getNextAction()).withNatureOfTask(natureOfwork);
            }

        }
        if (LOG.isDebugEnabled())
            LOG.debug(" WorkFlow Transition Completed  ...");
    }

    public String getNatureOfTask(final WaterConnectionDetails waterConnectionDetails) {
        final String wfTypeDisplayNmae = "Water Tap Connection";
        if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINPROGRESS)
                || waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERINITIATED)
                || waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERDIGSIGNPENDING)
                || waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERAPRROVED)
                || waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED))
            return wfTypeDisplayNmae + "::" + "Closure Connection";
        else if (waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.WORKFLOW_RECONNCTIONINITIATED)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONINPROGRESS)
                || waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONAPPROVED)
                || waterConnectionDetails.getStatus().getCode().equals(WaterTaxConstants.APPLICATION_STATUS_RECONNDIGSIGNPENDING)
                || waterConnectionDetails.getStatus().getCode()
                        .equals(WaterTaxConstants.APPLICATION_STATUS__RECONNCTIONSANCTIONED))
            return wfTypeDisplayNmae + "::" + "Reconnection";
        else
            return wfTypeDisplayNmae + "::" + waterConnectionDetails.getApplicationType().getName();
    }

}
