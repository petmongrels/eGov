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
package org.egov.ptis.web.controller.masters.usage;

import javax.validation.Valid;

import org.egov.infra.security.utils.SecurityUtils;
import org.egov.ptis.domain.entity.property.PropertyUsage;
import org.egov.ptis.master.service.PropertyUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller to Create a new Usage Master
 * 
 * @author nayeem
 *
 */

@Controller
@RequestMapping(value = "/usage/create")
public class CreateUsageController {

    private final PropertyUsageService propertyUsageService;
    
    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    public CreateUsageController(final PropertyUsageService propertyUsageService) {
        this.propertyUsageService = propertyUsageService;
    }

    @ModelAttribute
    public PropertyUsage propertyUsageModel() {
        return new PropertyUsage();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String newForm(final Model model) {
        String roleName = propertyUsageService.getRolesForUserId(securityUtils.getCurrentUser().getId());
        model.addAttribute("roleName",roleName);
        return "usage-form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final PropertyUsage propertyUsage, final BindingResult errors,
            final RedirectAttributes redirectAttributes) {

        if (errors.hasErrors())
            return "usage-form";
        
        propertyUsageService.create(propertyUsage);
        redirectAttributes.addFlashAttribute("message", "msg.usage.create.success");

        return "redirect:/usage/create";
    }
}
