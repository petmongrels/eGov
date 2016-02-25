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
package org.egov.stms.web.controller.masters;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.egov.stms.masters.entity.SewerageRatesMaster;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.service.SewerageRatesMasterService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/masters")
public class SewerageRateMasterController {

    private final SewerageRatesMasterService sewerageRatesMasterService;

    @Autowired
    public SewerageRateMasterController(final SewerageRatesMasterService sewerageRatesMasterService) {
        this.sewerageRatesMasterService = sewerageRatesMasterService;
    }

    @RequestMapping(value = "/seweragerates", method = RequestMethod.GET)
    public String showForm(
            @ModelAttribute SewerageRatesMaster sewerageRatesMaster,
            final Model model, final HttpServletRequest request) {
        sewerageRatesMaster = new SewerageRatesMaster();
        model.addAttribute("propertyTypes", PropertyType.values());

        return "sewerageRates-master";
    }

    @RequestMapping(value = "/seweragerates", method = RequestMethod.POST)
    public String creates(
            @Valid @ModelAttribute final SewerageRatesMaster sewerageRatesMaster,
            final RedirectAttributes redirectAttrs, final Model model,
            final BindingResult resultBinder) {

        if (resultBinder.hasErrors()) {
            model.addAttribute("propertyTypes", PropertyType.values());
            return "sewerageRates-master";
        }

        SewerageRatesMaster sewerageRatesMasterExisting = new SewerageRatesMaster();
        sewerageRatesMasterExisting = sewerageRatesMasterService
                .findByPropertyTypeAndFromDateAndActive(
                        sewerageRatesMaster.getPropertyType(),
                        sewerageRatesMaster.getFromDate(), true);
        if (sewerageRatesMasterExisting != null) {
            sewerageRatesMasterExisting.setActive(false);
            sewerageRatesMasterExisting.setToDate(sewerageRatesMaster.getFromDate());
            sewerageRatesMasterService.update(sewerageRatesMasterExisting);
            
            sewerageRatesMaster.setActive(true);
            sewerageRatesMasterService.create(sewerageRatesMaster);
        } else {
            SewerageRatesMaster sewerageRatesMasterOld = null;
            sewerageRatesMasterOld = sewerageRatesMasterService.findByPropertyTypeAndActive(
                    sewerageRatesMaster.getPropertyType(), true);
            if (sewerageRatesMasterOld != null) {
                sewerageRatesMasterOld.setActive(false);
                sewerageRatesMasterOld.setToDate(new DateTime(sewerageRatesMaster.getFromDate()).minusDays(1).toDate());
                sewerageRatesMasterService.update(sewerageRatesMasterOld);
            }
            
            sewerageRatesMaster.setActive(true);
            sewerageRatesMasterService.create(sewerageRatesMaster);
        }

        redirectAttrs.addAttribute("sewerageRatesMaster", sewerageRatesMaster);
        return "redirect:/masters/getseweragerates?id=" + sewerageRatesMaster.getId();

    }

    @RequestMapping(value = "/getseweragerates", method = RequestMethod.GET)
    public String getSeweragerates(@RequestParam("id") Long id, Model model) {
        SewerageRatesMaster sewerageRatesMaster = sewerageRatesMasterService.findBy(id);
        model.addAttribute("sewerageRatesMaster",
                sewerageRatesMaster);
        model.addAttribute("message", "msg.seweragemonthlyrate.creation.success");
        return "sewerageRates-success";
    }
}
