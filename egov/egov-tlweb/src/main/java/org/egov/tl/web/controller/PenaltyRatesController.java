/* eGov suite of products aim to improve the internal efficiency,transparency,
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
package org.egov.tl.web.controller;

import java.util.List;

import org.egov.tl.entity.LicenseAppType;
import org.egov.tl.entity.PenaltyRates;
import org.egov.tl.service.PenaltyRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/penaltyRates")
public class PenaltyRatesController {
    @Autowired
    private PenaltyRatesService penaltyRatesService;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String penaltyRatesForm(@ModelAttribute final PenaltyForm penaltyForm, final Model model) {
        model.addAttribute("licenseAppTypes", penaltyRatesService.findAllLicenseAppType());
        return "penaltyRates-create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String penaltyRatesCreate(@ModelAttribute final PenaltyForm penaltyForm, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes, final Model model) {
        LicenseAppType licenseAppType = null;
        licenseAppType = penaltyRatesService.findByLicenseAppType(penaltyForm.getLicenseAppType().getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("licenseAppTypes", penaltyRatesService.findAllLicenseAppType());
            return "penaltyRates-create";
        }
        if (licenseAppType != null && penaltyForm.getPenaltyRatesList() != null
                && penaltyForm.getPenaltyRatesList().size() > 0)
            for (final PenaltyRates penaltyRates : penaltyForm.getPenaltyRatesList()) {
                penaltyRates.setLicenseAppType(licenseAppType);
                penaltyRates.setFromRange(penaltyRates.getFromRange());
                penaltyRates.setToRange(penaltyRates.getToRange());
                penaltyRates.setRate(penaltyRates.getRate());
                penaltyRatesService.create(penaltyRates);
            }
        redirectAttributes.addFlashAttribute("message", "msg.penaltyRate.created");
        return "penaltyRates-result";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@ModelAttribute final PenaltyForm penaltyForm, final BindingResult errors, final Model model) {
        if (errors.hasErrors())
            return "penaltyRates-result";
        final List<PenaltyRates> searchResultList = penaltyRatesService.search(penaltyForm.getLicenseAppType().getId());
        if (searchResultList.size() > 0)
            penaltyForm.setPenaltyRatesList(searchResultList);
        else {
            penaltyForm.getPenaltyRatesList().clear();
        }
        model.addAttribute("penaltyForm", penaltyForm);
        return "penaltyRates-result";
    }

}
