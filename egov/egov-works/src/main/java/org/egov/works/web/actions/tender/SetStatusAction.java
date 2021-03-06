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
package org.egov.works.web.actions.tender;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.DateUtils;
import org.egov.works.models.tender.Retender;
import org.egov.works.models.tender.RetenderHistory;
import org.egov.works.models.tender.SetStatus;
import org.egov.works.models.tender.WorksPackage;
import org.egov.works.services.WorksService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Result(name = SetStatusAction.EDIT, location = "setStatus-edit.jsp")
public class SetStatusAction extends BaseFormAction {

    private static final long serialVersionUID = -6533573442117204510L;
    private SetStatus worksStatus = new SetStatus();
    private PersistenceService<SetStatus, Long> worksStatusService;
    private WorksService worksService;
    private String[] statusName;
    private Date[] statusDate;
    private Long objId;
    private String objectType;
    private List<SetStatus> setStatusList;
    private static final String STATUS_OBJECTID = "getStatusByObjectId";
    private static final String STATUS_VALUES = ".setstatus";
    private static final String LAST_STATUS = ".laststatus";
    private Date appDate;
    private String appDateProperFormat;
    private String ObjNo;

    private static final String TENDERRESPONSE_CONTRACTORS = "TenderResponseContractors";
    private static final String TENDERRESPONSE = "TenderResponse";
    private static final String WORKSPACKAGE = "WorksPackage";
    private static final String RETENDER = "retender";
    private WorksPackage worksPackage;
    private String[][] statusInfo;
    private Integer statusTableXValue;
    private Integer statusTableYValue;
    private List<RetenderHistory> retenderHistoryList = new LinkedList<RetenderHistory>();
    private List<Retender> retenderInfoList = new LinkedList<Retender>();
    private Boolean retenderingIsAllowed;
    private Boolean retenderingIsSelected;
    private Boolean viewMode = false;
    private Integer iterationCount;
    private String setStatus;

    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    @Override
    public Object getModel() {
        return worksStatus;
    }

    @Override
    public void prepare() {
        if (objId != null)
            setStatusList = worksStatusService.findAllByNamedQuery(STATUS_OBJECTID, objId, objectType);
        addDropdownData("statusList", getAllStatus());
    }

    private void loadWorksPackageStatuses() {
        final List<Retender> retenderDetails = worksPackage.getRetenderDetails();
        final List<RetenderHistory> retenderHistoryDetails = worksPackage.getRetenderHistoryDetails();
        Integer maxIterations = 0;
        // All statuses have been set
        if (worksPackage.getSetStatuses() != null
                && getWorksPackageSetStatusesLength() == getAllStatus().size()
                || StringUtils.isNotBlank(setStatus)
                        && setStatus.equalsIgnoreCase("view")
                || worksPackage.getEgwStatus().getCode()
                        .equalsIgnoreCase(WorksPackage.WorkPacakgeStatus.CANCELLED.toString()))
            viewMode = true;
        if (retenderDetails != null && retenderDetails.size() > 0)
            for (int i = 0; i < retenderDetails.size(); i++)
                if (retenderDetails.get(i).getIterationNumber() != null
                        && retenderDetails.get(i).getIterationNumber() > maxIterations)
                    maxIterations = retenderDetails.get(i).getIterationNumber();
        if (retenderHistoryDetails != null && retenderHistoryDetails.size() > 0) {
            final Map<Integer, Integer> statusesInDb = new LinkedHashMap<Integer, Integer>();
            Integer count = 0;
            for (final RetenderHistory rh : retenderHistoryDetails) {
                count = statusesInDb.get(rh.getEgwStatus().getId());
                if (count == null || count == 0)
                    statusesInDb.put(rh.getEgwStatus().getId(), 1);
                else
                    statusesInDb.put(rh.getEgwStatus().getId(), ++count);
            }
            final Integer numberOfStatusesSet = statusesInDb.size();
            // maxIterations+2 because 1st column will be statuses and 2nd
            // column will be normal status(not retender)
            statusInfo = new String[numberOfStatusesSet][maxIterations + 2];
            // Since it is 0 based index
            statusTableXValue = numberOfStatusesSet - 1;
            statusTableYValue = maxIterations + 1;
            int i = 0, iterationCount = 0;
            // Setting the statuses
            for (final Integer key : statusesInDb.keySet()) {
                statusInfo[i][0] = key.toString();
                i++;
            }
            // Setting the dates
            i = 0;
            for (final RetenderHistory rh : retenderHistoryDetails) {
                if (iterationCount != (rh.getRetender() == null ? 0 : rh.getRetender().getIterationNumber())) {
                    iterationCount = rh.getRetender().getIterationNumber();
                    i = 0;
                }
                if (rh.getRetender() == null)
                    statusInfo[i][1] = DateUtils.getDefaultFormattedDate(rh.getStatusDate());
                else
                    statusInfo[i][rh.getRetender().getIterationNumber() + 1] = DateUtils.getDefaultFormattedDate(rh
                            .getStatusDate());
                i++;
            }
        }
    }

    @SkipValidation
    @Action(value = "/tender/setStatus-edit")
    public String edit() {
        if (setStatusList != null && !setStatusList.isEmpty())
            populateStatusNameAndDateDetails(setStatusList);
        return EDIT;
    }

    @SkipValidation
    public String retenderEdit() {
        worksPackage = (WorksPackage) persistenceService.find(" from WorksPackage where id =? ", objId);
        if (worksPackage.getRetenderDetails() == null || worksPackage.getRetenderDetails().size() == 0)
            iterationCount = 0;
        else
            iterationCount = worksPackage.getRetenderDetails().get(worksPackage.getRetenderDetails().size() - 1)
                    .getIterationNumber();
        if (appDate != null)
            appDateProperFormat = DateUtils.getDefaultFormattedDate(appDate);
        loadWorksPackageStatuses();
        checkIfRetenderingIsAllowed();
        return RETENDER;
    }

    private void checkIfRetenderingIsAllowed() {
        final String lastStatus = worksService.getWorksConfigValue(WORKSPACKAGE + LAST_STATUS);
        retenderingIsAllowed = false;

        // More than or equal to 2 statuses have been set and the last status
        // has not been set
        if (worksPackage.getLatestOfflineStatus() != null
                && worksPackage.getLatestOfflineStatus().getEgwStatus() != null
                && !worksPackage.getLatestOfflineStatus().getEgwStatus().getCode().equalsIgnoreCase(lastStatus)
                && worksPackage.getSetStatuses() != null && getWorksPackageSetStatusesLength() >= 2)
            retenderingIsAllowed = true;
    }

    private void validateRetendering() {
        if (retenderHistoryList != null && retenderHistoryList.size() > 0) {
            int i = 0, j = 0;
            Date maxRetenderHistoryDate = null;
            boolean wrongOrderOfStatus = false;
            for (final RetenderHistory rh : retenderHistoryList) {
                if (rh == null)
                    continue;
                if (rh.getStatusDate() != null && (rh.getEgwStatus() == null || rh.getEgwStatus().getId() == null))
                    addFieldError(getText("wp.retender.both.status.date.set"),
                            getText("wp.retender.both.status.date.set"));
                if (rh.getEgwStatus() != null && rh.getEgwStatus().getId() != null && rh.getStatusDate() == null)
                    addFieldError(getText("wp.retender.both.status.date.set"),
                            getText("wp.retender.both.status.date.set"));
                j = 0;
                for (final RetenderHistory rh1 : retenderHistoryList) {
                    if (rh1 == null)
                        continue;
                    if (i != j && rh.getEgwStatus() != null && rh.getEgwStatus().getId() != null
                            && rh1.getEgwStatus() != null && rh1.getEgwStatus().getId() != null
                            && rh.getEgwStatus().getId().longValue() == rh1.getEgwStatus().getId().longValue())
                        addFieldError(getText("wp.retender.both.status.duplicate"),
                                getText("wp.retender.both.status.duplicate"));
                    if (i < j && rh1.getStatusDate().before(rh.getStatusDate()))
                        addFieldError(getText("wp.retender.incorrect.date.order"),
                                getText("wp.retender.incorrect.date.order"));
                    if (i > j && rh.getStatusDate().before(rh1.getStatusDate()))
                        addFieldError(getText("wp.retender.incorrect.date.order"),
                                getText("wp.retender.incorrect.date.order"));
                    j++;
                }
                if (worksPackage.getSetStatuses() != null && getWorksPackageSetStatusesLength() > 0)
                    if (!retenderingIsSelected) {
                        int index = 0;
                        final List<EgwStatus> statusList = getAllStatus();
                        final List<SetStatus> alreadyPersistedStatusForWP = persistenceService.findAllBy(
                                " from SetStatus  " + "  where objectId=? and objectType='" + WORKSPACKAGE
                                        + "' order by id ",
                                worksPackage.getId());
                        for (final SetStatus st : alreadyPersistedStatusForWP) {
                            if (rh.getEgwStatus().getId().intValue() == st.getEgwStatus().getId().intValue())
                                addFieldError(getText("wp.retender.both.status.duplicate"),
                                        getText("wp.retender.both.status.duplicate"));
                            if (!st.getEgwStatus().getCode().equalsIgnoreCase(statusList.get(index).getCode()))
                                wrongOrderOfStatus = true;
                            index++;
                        }
                        for (final RetenderHistory rh1 : retenderHistoryList) {
                            if (rh1 == null)
                                continue;
                            if (rh1.getEgwStatus().getId().intValue() != statusList.get(index).getId().intValue())
                                wrongOrderOfStatus = true;
                            index++;
                        }
                    }
                if (rh.getStatusDate().after(new Date()))
                    addFieldError(getText("wp.retender.no.futuredate"), getText("wp.retender.no.futuredate"));
                if (appDate != null && appDate.after(rh.getStatusDate()))
                    addFieldError(getText("wp.retender.greater.than.approved.date"),
                            getText("wp.retender.greater.than.approved.date"));
                if (worksPackage.getLatestOfflineStatus() != null)
                    if (worksPackage.getLatestOfflineStatus().getStatusDate().after(rh.getStatusDate()))
                        addFieldError(getText("wp.retender.incorrect.date.order"),
                                getText("wp.retender.incorrect.date.order"));
                if (maxRetenderHistoryDate == null)
                    maxRetenderHistoryDate = rh.getStatusDate();
                else if (rh.getStatusDate().after(maxRetenderHistoryDate))
                    maxRetenderHistoryDate = rh.getStatusDate();

                // Increment outer loop variable
                i++;
            }
            if (wrongOrderOfStatus)
                addFieldError(getText("wp.retender.wrong.order"), getText("wp.retender.wrong.order"));
            if (retenderingIsSelected)
                if (retenderInfoList == null || retenderInfoList.size() == 0)
                    addFieldError(getText("wp.retender.no.info"), getText("wp.retender.no.info"));
                else
                    for (final Retender ret : retenderInfoList) {
                        if (StringUtils.isBlank(ret.getReason()))
                            addFieldError(getText("wp.retender.enter.reason"), getText("wp.retender.enter.reason"));
                        if (ret.getRetenderDate() == null)
                            addFieldError(getText("wp.retender.enter.date"), getText("wp.retender.enter.date"));
                        if (ret.getRetenderDate() != null && maxRetenderHistoryDate.after(ret.getRetenderDate()))
                            addFieldError(getText("wp.retender.date.less.than.statusdate"),
                                    getText("wp.retender.date.less.than.statusdate"));
                        for (final Retender re : worksPackage.getRetenderDetails())
                            if (re != null && re.getRetenderDate() != null
                                    & re.getRetenderDate().after(ret.getRetenderDate()))
                                addFieldError(getText("wp.retender.increasing.date"),
                                        getText("wp.retender.increasing.date"));
                    }
        } else
            addFieldError(getText("wp.retender.set.status.no.info"), getText("wp.retender.set.status.no.info"));
    }

    private int getWorksPackageSetStatusesLength() {
        int returnVal = 0;
        if (worksPackage.getSetStatuses() != null && worksPackage.getSetStatuses().size() > 0)
            for (final SetStatus ss : worksPackage.getSetStatuses())
                if (ss != null && ss.getObjectType().equalsIgnoreCase(WORKSPACKAGE))
                    returnVal++;
        return returnVal;
    }

    @ValidationErrorPage(value = RETENDER)
    @SkipValidation
    public String retenderSave() {
        worksPackage = (WorksPackage) persistenceService.find(" from WorksPackage where id =? ", objId);
        validateRetendering();
        if (!getFieldErrors().isEmpty())
            return retenderEdit();
        // 4 usecases - retendering not done, retendering done and status set,
        // retendering done and retendering selected
        if (worksPackage.getRetenderDetails() == null || worksPackage.getRetenderDetails().size() == 0
                && !retenderingIsSelected)
            saveWhenRetenderingWasNotPreviouslyDone();
        else if (worksPackage.getRetenderDetails() == null || worksPackage.getRetenderDetails().size() == 0
                && retenderingIsSelected)
            saveWhenRetenderingIsSelected();
        else if (worksPackage.getRetenderDetails() != null && worksPackage.getRetenderDetails().size() > 0
                && !retenderingIsSelected)
            saveWhenRetenderingPreviouslyDoneAndRetenderingIsNotSelected();
        else if (worksPackage.getRetenderDetails() != null && worksPackage.getRetenderDetails().size() > 0
                && retenderingIsSelected)
            saveWhenRetenderingIsSelected();

        getPersistenceService().getSession().flush();

        return SUCCESS;
    }

    private void saveWhenRetenderingWasNotPreviouslyDone() {
        final List<RetenderHistory> retHistList = new LinkedList<RetenderHistory>();
        for (final RetenderHistory rh : retenderHistoryList) {
            if (rh == null)
                continue;
            rh.setWorksPackage(worksPackage);
            retHistList.add(rh);
        }

        worksPackage.getRetenderHistoryDetails().addAll(retHistList);
        SetStatus ss = null;
        for (final RetenderHistory rh : retHistList) {
            ss = new SetStatus();
            ss.setEgwStatus(rh.getEgwStatus());
            ss.setObjectId(rh.getWorksPackage().getId());
            ss.setObjectType(WORKSPACKAGE);
            ss.setStatusDate(rh.getStatusDate());
            worksStatusService.persist(ss);
        }
        worksPackage.setLatestOfflineStatus(ss);
    }

    private void saveWhenRetenderingPreviouslyDoneAndRetenderingIsNotSelected() {
        final List<RetenderHistory> retHistList = new LinkedList<RetenderHistory>();

        // Info will be added in status of WP and list in retender
        for (final RetenderHistory rh : retenderHistoryList) {
            if (rh == null)
                continue;
            rh.setWorksPackage(worksPackage);
            // Get latest retender and set it to retender history, size of
            // retender list should be>0
            rh.setRetender(worksPackage.getRetenderDetails().get(worksPackage.getRetenderDetails().size() - 1));
            retHistList.add(rh);
        }

        worksPackage.getRetenderDetails().get(worksPackage.getRetenderDetails().size() - 1).getRetenderHistoryDetails()
                .addAll(retHistList);

        worksPackage.getRetenderHistoryDetails().addAll(retHistList);
        // Adding the set statuses
        SetStatus ss = null;
        for (final RetenderHistory rh : retHistList) {
            ss = new SetStatus();
            ss.setEgwStatus(rh.getEgwStatus());
            ss.setObjectId(rh.getWorksPackage().getId());
            ss.setObjectType(WORKSPACKAGE);
            ss.setStatusDate(rh.getStatusDate());
            worksStatusService.persist(ss);
        }
        worksPackage.setLatestOfflineStatus(ss);
    }

    private void saveWhenRetenderingIsSelected() {
        final List<RetenderHistory> retHistList = new LinkedList<RetenderHistory>();
        final List<Retender> retList = new LinkedList<Retender>();
        for (final Retender ret : retenderInfoList) {
            ret.setWorksPackage(worksPackage);
            if (worksPackage.getRetenderDetails() == null || worksPackage.getRetenderDetails().size() == 0)
                ret.setIterationNumber(1);
            else
                ret.setIterationNumber(worksPackage.getRetenderDetails().size() + 1);
            retList.add(ret);
        }

        for (final RetenderHistory rh : retenderHistoryList) {
            if (rh == null)
                continue;
            rh.setWorksPackage(worksPackage);
            // Since max of 1 retendering can be done at a time
            rh.setRetender(retList.get(0));
            retHistList.add(rh);
        }
        worksPackage.getRetenderDetails().addAll(retList);

        retList.get(0).setRetenderHistoryDetails(retHistList);

        worksPackage.getRetenderHistoryDetails().addAll(retHistList);
        // Delete old offline statuses and add new offline statuses
        final Query qry1 = getPersistenceService().getSession().createQuery(
                "delete from SetStatus where objectId=:object_id and objectType='WorksPackage' ");
        qry1.setLong("object_id", worksPackage.getId());
        qry1.executeUpdate();
        SetStatus ss = null;
        for (final RetenderHistory rh : retHistList) {
            ss = new SetStatus();
            ss.setEgwStatus(rh.getEgwStatus());
            ss.setObjectId(rh.getWorksPackage().getId());
            ss.setObjectType(WORKSPACKAGE);
            ss.setStatusDate(rh.getStatusDate());
            worksStatusService.persist(ss);
        }
        worksPackage.setLatestOfflineStatus(ss);
    }

    public String save() {
        int i = 0;
        for (final String statName : getStatusNameDetails()) {
            if (i > getSetStatusList().size() - 1) {
                final SetStatus stat = new SetStatus();
                stat.setObjectId(objId);
                stat.setObjectType(objectType);
                stat.setEgwStatus(getDescriptionByCode(statName));
                stat.setStatusDate(getDateList().get(i));
                worksStatusService.persist(stat);
            }
            i++;
        }
        return SUCCESS;
    }

    public List<EgwStatus> getAllStatus() {
        String status;
        String lastStatus;
        if (objectType != null && objectType.equals(TENDERRESPONSE_CONTRACTORS)) {
            status = worksService.getWorksConfigValue(TENDERRESPONSE + STATUS_VALUES);
            lastStatus = worksService.getWorksConfigValue(TENDERRESPONSE + LAST_STATUS);
        } else {
            status = worksService.getWorksConfigValue(objectType + STATUS_VALUES);
            lastStatus = worksService.getWorksConfigValue(objectType + LAST_STATUS);
        }

        final List<String> statList = new ArrayList<String>();
        if (StringUtils.isNotBlank(status) && StringUtils.isNotBlank(lastStatus)) {
            final List<String> statusList = Arrays.asList(status.split(","));
            for (final String stat : statusList)
                if (stat.equals(lastStatus)) {
                    statList.add(stat);
                    break;
                } else
                    statList.add(stat);
        }
        if (objectType != null && objectType.equals(TENDERRESPONSE_CONTRACTORS))
            return egwStatusHibernateDAO.getStatusListByModuleAndCodeList(TENDERRESPONSE, statList);
        else
            return egwStatusHibernateDAO.getStatusListByModuleAndCodeList(objectType, statList);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getStatusNameDetails() {
        return CollectionUtils.select(Arrays.asList(statusName), statusName -> (String) statusName != null);
    }

    @SuppressWarnings("unchecked")
    public Collection<Date> getStatusDateDetails() {
        return CollectionUtils.select(Arrays.asList(statusDate), statusDate -> (Date) statusDate != null);
    }

    private EgwStatus getDescriptionByCode(final String statName) {

        if (objectType != null && objectType.equals(TENDERRESPONSE_CONTRACTORS))
            return egwStatusHibernateDAO.getStatusByModuleAndCode(TENDERRESPONSE, statName);
        else
            return egwStatusHibernateDAO.getStatusByModuleAndCode(objectType, statName);
    }

    private void populateStatusNameAndDateDetails(final List<SetStatus> setStatusList) {
        int i = 0;
        statusName = new String[setStatusList.size()];
        statusDate = new Date[setStatusList.size()];
        for (final SetStatus stat : setStatusList) {
            getStatusName()[i] = stat.getEgwStatus().getCode();
            getStatusDate()[i] = stat.getStatusDate();
            i++;
        }
    }

    private void validateStatusName() {
        int i = 0;
        final List<EgwStatus> statList = getAllStatus();
        for (final String statName : getStatusNameDetails()) {
            if (!statList.isEmpty() && !statName.equals(statList.get(i).getCode())) {
                addFieldError(
                        "status.order.incorrect",
                        getText("status.order.incorrect", new String[] {
                                getDescriptionByCode(statName).getDescription(), statList.get(i).getDescription() }));
                break;
            }
            i++;
        }
    }

    private void validateStatusDate() {
        if (appDate != null && !getDateList().isEmpty() && !getStatusCodeList().isEmpty()
                && !DateUtils.compareDates(getDateList().get(0), appDate))
            addFieldError(
                    "status.date.incorrect",
                    getText("status.date.greaterThan.appDate",
                            new String[] { getDescriptionByCode(getStatusCodeList().get(0)).getDescription(),
                                    objectType }));
        int j = 1;
        for (final Date dateObj : getDateList()) {
            if (getDateList().size() > j && !DateUtils.compareDates(getDateList().get(j), dateObj))
                addFieldError(
                        "status.date.incorrect",
                        getText("status.date.incorrect", new String[] {
                                getDescriptionByCode(getStatusCodeList().get(j)).getDescription(),
                                getDescriptionByCode(getStatusCodeList().get(j - 1)).getDescription() }));
            j++;
        }
    }

    @Override
    public void validate() {
        validateStatusName();
        validateStatusDate();
    }

    private List<Date> getDateList() {
        return (List<Date>) getStatusDateDetails();
    }

    private List<String> getStatusCodeList() {
        return (List<String>) getStatusNameDetails();
    }

    public void setWorksStatusService(final PersistenceService<SetStatus, Long> worksStatusService) {
        this.worksStatusService = worksStatusService;
    }

    public void setWorksService(final WorksService worksService) {
        this.worksService = worksService;
    }

    public String[] getStatusName() {
        return statusName == null ? new String[0] : statusName;
    }

    public void setStatusName(final String[] statusName) {
        this.statusName = statusName;
    }

    public Date[] getStatusDate() {
        return statusDate == null ? new Date[0] : statusDate;
    }

    public void setStatusDate(final Date[] statusDate) {
        this.statusDate = statusDate;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(final Long objId) {
        this.objId = objId;
    }

    public List<SetStatus> getSetStatusList() {
        return setStatusList;
    }

    public void setSetStatusList(final List<SetStatus> setStatusList) {
        this.setStatusList = setStatusList;
    }

    public void setWorksStatus(final SetStatus worksStatus) {
        this.worksStatus = worksStatus;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    public Date getAppDate() {
        return appDate;
    }

    public void setAppDate(final Date appDate) {
        this.appDate = appDate;
    }

    public String getObjNo() {
        return ObjNo;
    }

    public void setObjNo(final String objNo) {
        ObjNo = objNo;
    }

    public String[][] getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(final String[][] statusInfo) {
        this.statusInfo = statusInfo;
    }

    public List<Integer> getRowloop() {
        final List<Integer> list = new LinkedList<Integer>();
        if (statusTableXValue != null && statusTableXValue >= 0)
            for (Integer i = 0; i <= statusTableXValue; i++)
                list.add(i);
        return list;
    }

    public List<Integer> getColumnloop() {
        final List<Integer> list = new LinkedList<Integer>();
        if (statusTableYValue != null && statusTableYValue >= 0)
            for (Integer i = 0; i <= statusTableYValue; i++)
                list.add(i);
        return list;
    }

    public Boolean getRetenderingIsAllowed() {
        return retenderingIsAllowed;
    }

    public Boolean getRetenderingIsSelected() {
        return retenderingIsSelected;
    }

    public void setRetenderingIsSelected(final Boolean retenderingIsSelected) {
        this.retenderingIsSelected = retenderingIsSelected;
    }

    public List<RetenderHistory> getRetenderHistoryList() {
        return retenderHistoryList;
    }

    public void setRetenderHistoryList(final List<RetenderHistory> retenderHistoryList) {
        this.retenderHistoryList = retenderHistoryList;
    }

    public List<Retender> getRetenderInfoList() {
        return retenderInfoList;
    }

    public void setRetenderInfoList(final List<Retender> retenderInfoList) {
        this.retenderInfoList = retenderInfoList;
    }

    public Integer getIterationCount() {
        return iterationCount;
    }

    public WorksPackage getWorksPackage() {
        return worksPackage;
    }

    public Boolean getViewMode() {
        return viewMode;
    }

    public Integer getStatusTableYValue() {
        return statusTableYValue;
    }

    public String getAppDateProperFormat() {
        return appDateProperFormat;
    }

    public void setAppDateProperFormat(final String appDateProperFormat) {
        this.appDateProperFormat = appDateProperFormat;
    }

    public String getSetStatus() {
        return setStatus;
    }

    public void setSetStatus(final String setStatus) {
        this.setStatus = setStatus;
    }

}
