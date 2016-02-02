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
package org.egov.stms.utils;

import org.egov.commons.EgwStatus;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infstr.services.PersistenceService;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.wtms.PropertyIntegrationService;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SewerageTaxUtils {

    @Autowired
    private AppConfigValueService appConfigValuesService;

    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;

    @Autowired
    @Qualifier("propertyIntegrationServiceImpl")
    private PropertyIntegrationService propertyIntegrationService;

    public Boolean isNewConnectionAllowedIfPTDuePresent() {
        AppConfigValues appConfigValue = null;
        if (appConfigValuesService.getConfigValuesByModuleAndKey(
                SewerageTaxConstants.MODULE_NAME, SewerageTaxConstants.NEWCONNECTIONALLOWEDIFPTDUE).size() == 0) {
            appConfigValue = new AppConfigValues();
            appConfigValue.setValue("NO");
        } else
            appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                    SewerageTaxConstants.MODULE_NAME, SewerageTaxConstants.NEWCONNECTIONALLOWEDIFPTDUE).get(0);

        return "YES".equalsIgnoreCase(appConfigValue.getValue());
    }

    public Boolean isMultipleNewConnectionAllowedForPID() {
        AppConfigValues appConfigValue = null;
        if (appConfigValuesService.getConfigValuesByModuleAndKey(
                SewerageTaxConstants.MODULE_NAME, SewerageTaxConstants.MULTIPLENEWCONNECTIONFORPID).size() == 0) {
            appConfigValue = new AppConfigValues();
            appConfigValue.setValue("NO");
        } else
            appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
                    SewerageTaxConstants.MODULE_NAME, SewerageTaxConstants.MULTIPLENEWCONNECTIONFORPID).get(0);
        return "YES".equalsIgnoreCase(appConfigValue.getValue());
    }

    public EgwStatus getStatusByCodeAndModuleType(final String code, final String moduleName) {
        return (EgwStatus) persistenceService.find("from EgwStatus where moduleType=? and code=?", moduleName, code);
    }

    public AssessmentDetails getAssessmentDetailsForFlag(final String asessmentNumber, final Integer flagDetail) {
        final AssessmentDetails assessmentDetails = propertyIntegrationService.getAssessmentDetailsForFlag(asessmentNumber,
                flagDetail);
        return assessmentDetails;
    }
}
