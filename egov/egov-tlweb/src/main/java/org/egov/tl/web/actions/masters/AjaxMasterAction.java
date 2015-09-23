package org.egov.tl.web.actions.masters;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.tl.domain.entity.UnitOfMeasurement;
import org.egov.tl.domain.service.masters.UnitOfMeasurementService;
import org.springframework.beans.factory.annotation.Autowired;

@ParentPackage("egov")
@Results({ @Result(name = AjaxMasterAction.UNIQUECHECK, location = "ajaxMaster-uniqueCheck.jsp") })
public class AjaxMasterAction extends BaseFormAction {

    /**
     *
     */
    private static final long serialVersionUID = -3409384411484334947L;
    private String name;
    private String errorMsg = "";
    private String code;
    public static final String UNIQUECHECK = "uniqueCheck";
    private static final String UOM_MASTER = "uomMaster";
    private static final String NAME = "name";
    private static final String CODE = "code";
    private Boolean isUnique;
    private String screenType;
    private String paramType;
    @Autowired
    private UnitOfMeasurementService unitOfMeasurementService;

    @Override
    public Object getModel()
    {
        return null;
    }

    /**
     * @description : Checks whether name / code exists
     * @return
     */
    @Action(value = "/masters/ajaxMaster-validateActions")
    public String validateActions() {
        if (name != null && !name.isEmpty()) {
            // Invoked from UOM Master Screen - name unique check
            if (screenType.equalsIgnoreCase(UOM_MASTER)) {
                paramType = NAME;
                final UnitOfMeasurement unitOfMeasurement = unitOfMeasurementService.findUOMByName(name);
                if (unitOfMeasurement != null) {
                    errorMsg = getText("uom.validate.duplicateName", new String[] { name });
                    isUnique = Boolean.FALSE;
                } else
                    isUnique = Boolean.TRUE;
            }
        } else if (code != null && !code.isEmpty())
            // Invoked from UOM Master Screen - code unique check
            if (screenType.equalsIgnoreCase(UOM_MASTER)) {
                paramType = CODE;
                final UnitOfMeasurement unitOfMeasurement = unitOfMeasurementService.findUOMByCode(code);
                if (unitOfMeasurement != null) {
                    errorMsg = getText("uom.validate.duplicateCode", new String[] { name });
                    isUnique = Boolean.FALSE;
                } else
                    isUnique = Boolean.TRUE;
            }
        return UNIQUECHECK;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(final String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(final String screenType) {
        this.screenType = screenType;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(final String paramType) {
        this.paramType = paramType;
    }

    public Boolean getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(final Boolean isUnique) {
        this.isUnique = isUnique;
    }

}