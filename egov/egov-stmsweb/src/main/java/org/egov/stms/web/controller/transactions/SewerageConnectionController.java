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
package org.egov.stms.web.controller.transactions;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.PropertyTaxDetails;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.stms.masters.entity.SewerageApplicationType;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.entity.enums.SewerageConnectionStatus;
import org.egov.stms.masters.service.SewerageApplicationTypeService;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageConnection;
import org.egov.stms.transactions.service.SewerageApplicationDetailsService;
import org.egov.stms.transactions.service.SewerageConnectionService;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.egov.wtms.utils.PropertyExtnUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/transactions")
public class SewerageConnectionController extends GenericWorkFlowController {

    private static final Logger LOG = LoggerFactory.getLogger(SewerageConnectionController.class);

    private final SewerageTaxUtils sewerageTaxUtils;

    private final SewerageApplicationDetailsService sewerageApplicationDetailsService;

    @Autowired
    private SewerageConnectionService sewerageConnectionService;

    @Autowired
    private PropertyExtnUtils propertyExtnUtils;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    protected AssignmentService assignmentService;

    @Autowired
    protected BasicPropertyDAO basicPropertyDAO;

    @Autowired
    private PropertyExternalService propertyExternalService;

    @Autowired
    public SewerageConnectionController(final SewerageTaxUtils sewerageTaxUtils,
            final SewerageApplicationTypeService sewerageApplicationTypeService,
            final SewerageApplicationDetailsService sewerageApplicationDetailsService) {
        this.sewerageTaxUtils = sewerageTaxUtils;
        this.sewerageApplicationDetailsService = sewerageApplicationDetailsService;
    }

    @RequestMapping(value = "/newConnection-newform", method = RequestMethod.GET)
    public String showNewApplicationForm(@ModelAttribute final SewerageApplicationDetails sewerageApplicationDetails,
            final Model model, final HttpServletRequest request) {
        LOG.debug("Inside showNewApplicationForm method");
        SewerageApplicationType applicationType = new SewerageApplicationType();
        applicationType.setId(1L);
        sewerageApplicationDetails.setApplicationType(applicationType);
        sewerageApplicationDetails.setApplicationDate(new Date());
        final SewerageConnection connection = new SewerageConnection();
        connection.setConnectionStatus(SewerageConnectionStatus.INPROGRESS);
        sewerageApplicationDetails.setConnection(connection);

        model.addAttribute("allowIfPTDueExists", sewerageTaxUtils.isNewConnectionAllowedIfPTDuePresent());
        model.addAttribute("propertyTypes", PropertyType.values());

        return "newconnection-form";
    }

    @RequestMapping(value = "/newConnection-create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final SewerageApplicationDetails sewerageApplicationDetails,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Model model,
            final BindingResult errors) {

        validatePropertyID(sewerageApplicationDetails, resultBinder);
        if (sewerageApplicationDetails.getState() == null)
            sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                    SewerageTaxConstants.APPLICATION_STATUS_CREATED, SewerageTaxConstants.MODULETYPE));
        if (LOG.isDebugEnabled())
            LOG.error("Model Level Validation occurs = " + resultBinder);

        if (resultBinder.hasErrors()) {
            sewerageApplicationDetails.setApplicationDate(new Date());
            model.addAttribute("validateIfPTDueExists", sewerageTaxUtils.isNewConnectionAllowedIfPTDuePresent());
            model.addAttribute("propertyTypes", PropertyType.values());
            return "newconnection-form";
        }

        sewerageApplicationDetailsService.create(sewerageApplicationDetails);
        return "redirect:newconnectionack?pathVar=" + sewerageApplicationDetails.getApplicationNumber();
    }

    @RequestMapping(value = "/newconnectionack", method = RequestMethod.GET)
    public String newSewerageConnectionAck(final HttpServletRequest request, final Model model, final ModelMap modelMap) {
        final String applicationNumber = request.getParameter("pathVar");
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(applicationNumber);
        model.addAttribute("sewerageApplicationDetails", sewerageApplicationDetails);

        final Assignment currentUserAssignment = assignmentService.getPrimaryAssignmentForGivenRange(securityUtils
                .getCurrentUser().getId(), new Date(), new Date());
        model.addAttribute("currentUserDesgn", currentUserAssignment.getDesignation().getName());
        model.addAttribute("cityName", EgovThreadLocals.getCityName());

        setCommonDetails(sewerageApplicationDetails, modelMap);

        return "newSewerageConnection-ack";
    }

    private void setCommonDetails(final SewerageApplicationDetails sewerageApplicationDetails, final ModelMap modelMap) {
        String applicationNumber = sewerageApplicationDetails.getApplicationNumber();
        final BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(applicationNumber);
        modelMap.addAttribute("propertyAddress", basicProperty.getAddress().toString());

        final PropertyOwnerInfo propertyOwnerInfo = basicProperty.getPropertyOwnerInfo().get(0);
        modelMap.addAttribute("mobileNumber", propertyOwnerInfo.getOwner().getMobileNumber());
        modelMap.addAttribute("emailAddress", propertyOwnerInfo.getOwner().getEmailId());
        modelMap.addAttribute("applicantName", propertyOwnerInfo.getOwner().getName());
        modelMap.addAttribute("aadhaarNumber", propertyOwnerInfo.getOwner().getAadhaarNumber());

        final AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(applicationNumber,
                PropertyExternalService.FLAG_FULL_DETAILS);
        modelMap.addAttribute("locality", assessmentDetails.getBoundaryDetails().getLocalityName());
        modelMap.addAttribute("zoneWardBlockDetails", getZoneWardBlockDetails(assessmentDetails));

        final PropertyTaxDetails propertyTaxDetails = propertyExternalService.getPropertyTaxDetails(applicationNumber);
        modelMap.addAttribute("propertyTax", propertyTaxDetails.getTotalTaxAmt());

        final BigDecimal sewerageTaxDue = sewerageConnectionService.getTotalAmount(sewerageApplicationDetails.getConnection());
        modelMap.addAttribute("sewerageTaxDue", sewerageTaxDue);
    }

    private String getZoneWardBlockDetails(final AssessmentDetails assessmentDetails) {
        final StringBuffer sb = new StringBuffer();
        sb.append(assessmentDetails.getBoundaryDetails().getZoneName()).append("/");
        sb.append(assessmentDetails.getBoundaryDetails().getWardName()).append("/");
        sb.append(assessmentDetails.getBoundaryDetails().getBlockName());
        return sb.toString();
    }

    private void validatePropertyID(final SewerageApplicationDetails sewerageApplicationDetails, final BindingResult errors) {
        if (sewerageApplicationDetails.getConnection() != null
                && sewerageApplicationDetails.getConnection().getPropertyIdentifier() != null
                && !sewerageApplicationDetails.getConnection().getPropertyIdentifier().equals("")) {
            String errorMessage = sewerageApplicationDetailsService.checkValidPropertyAssessmentNumber(sewerageApplicationDetails
                    .getConnection().getPropertyIdentifier());
            if (errorMessage != null && !errorMessage.equals(""))
                errors.rejectValue("connection.propertyIdentifier", errorMessage, errorMessage);
            else {
                errorMessage = sewerageApplicationDetailsService.checkConnectionPresentForProperty(sewerageApplicationDetails
                        .getConnection().getPropertyIdentifier());
                if (errorMessage != null && !errorMessage.equals(""))
                    errors.rejectValue("connection.propertyIdentifier", errorMessage, errorMessage);
            }
        }
    }
}
