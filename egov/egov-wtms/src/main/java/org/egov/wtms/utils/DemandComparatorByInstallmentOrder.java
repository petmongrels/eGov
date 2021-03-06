package org.egov.wtms.utils;

import java.util.Comparator;

import org.egov.demand.model.EgDemandDetails;

public class DemandComparatorByInstallmentOrder implements Comparator<EgDemandDetails> {
    @Override
    public int compare(final EgDemandDetails d1, final EgDemandDetails d2) {
        return d1.getEgDemandReason().getEgInstallmentMaster().getFromDate().compareTo
                (d2.getEgDemandReason().getEgInstallmentMaster().getFromDate());
    }
}
