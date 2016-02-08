package org.egov.stms.web.controller.transactions;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.egov.ptis.domain.service.property.PropertyService;
import org.egov.stms.transactions.service.SewerageApplicationDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SewerageAjaxConnectionController {
    
    @Autowired
    private SewerageApplicationDetailsService sewerageApplicationDetailsService;
    
    @Autowired
    private PropertyService propertyservice;
    
    @RequestMapping(value = "/ajaxconnection/check-primaryconnection-exists", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String isConnectionPresentForProperty(@RequestParam final String propertyID) {
        return sewerageApplicationDetailsService.checkConnectionPresentForProperty(propertyID);
    }
    
    @RequestMapping(value = "/ajaxconnection/check-watertax-due", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody BigDecimal getWaterTaxDue(@RequestParam("assessmentNo") String assessmentNo, HttpServletRequest request) {
        BigDecimal waterTaxDue = propertyservice.getWaterTaxDues(assessmentNo, request);
        return waterTaxDue;
    }
}
