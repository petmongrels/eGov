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
package org.egov.collection.web.actions.citizen.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.egov.collection.entity.OnlinePaymentResult;
import org.egov.collection.service.CollectionReportService;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
public class OnlinePaymentReportController {

    @Autowired
    private CollectionReportService reportService;

    @ModelAttribute
    public void getPropertyModel(final Model model) {
        final OnlinePaymentResult OnlinePaymentResult = new OnlinePaymentResult();
        model.addAttribute("onlinePaymentResult", OnlinePaymentResult);
    }

    @ModelAttribute("districtname")
    public List getDistrictnames() {
        return reportService.getDistrictNames();
    }

    @RequestMapping(value = "/citizen/onlinePaymentReport",method = RequestMethod.GET)
    public String searchForm(final Model model) {
        model.addAttribute("fromdate", new Date());
        model.addAttribute("todate", new Date());
        return "onlinePaymentReport-form";
    }

    @RequestMapping(value = "/citizen/onlinePaymentReport/result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void springPaginationDataTablesUpdate(final HttpServletRequest request,
            final HttpServletResponse response, final Model model) throws IOException {
        List<OnlinePaymentResult> onlinePaymentList = new ArrayList<OnlinePaymentResult>();
        final String districtname = request.getParameter("districtname");
        final String ulbname = request.getParameter("ulbname");
        final String fromdate = request.getParameter("fromdate");
        final String todate = request.getParameter("todate");
        final String transid = request.getParameter("transid");
        final SQLQuery query = reportService.getOnlinePaymentReportData(districtname, ulbname, fromdate, todate,
                transid);
        onlinePaymentList = query.list();
        final String result = new StringBuilder("{ \"data\":").append(toJSON(onlinePaymentList)).append("}").toString();
        IOUtils.write(result, response.getWriter());
    }

    private Object toJSON(final Object object) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.registerTypeAdapter(OnlinePaymentResult.class, new OnlinePaymentResultAdaptor())
                .create();
        final String json = gson.toJson(object);
        return json;
    }
    
    @RequestMapping(value = "/citizen/getUlbNamesByDistrict", method = RequestMethod.GET)
    public @ResponseBody void getUlbNamesByDistrict(@RequestParam final String districtname,final HttpServletResponse response) throws IOException {
        List ulbnames = new ArrayList();
        ulbnames = reportService.getUlbNames(districtname);
        final List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put("ulbname", ulbnames);
            jsonObjects.add(jsonObj);
        IOUtils.write(jsonObjects.toString(), response.getWriter());
    }
}
