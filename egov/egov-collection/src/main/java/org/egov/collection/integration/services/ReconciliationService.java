package org.egov.collection.integration.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.egov.collection.constants.CollectionConstants;
import org.egov.collection.entity.ReceiptDetail;
import org.egov.collection.entity.ReceiptHeader;
import org.egov.collection.integration.models.BillReceiptInfo;
import org.egov.collection.integration.models.BillReceiptInfoImpl;
import org.egov.collection.integration.pgi.PaymentResponse;
import org.egov.collection.service.ReceiptHeaderService;
import org.egov.collection.utils.CollectionCommon;
import org.egov.collection.utils.CollectionsUtil;
import org.egov.collection.utils.FinancialsUtil;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;

public class ReconciliationService {
    private static final Logger LOGGER = Logger.getLogger(ReconciliationService.class);
    public ReceiptHeaderService receiptHeaderService;
    private CollectionsUtil collectionsUtil;
    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;
    private CollectionCommon collectionCommon;

    /**
     * This method processes the success message arriving from the payment
     * gateway. The receipt status is changed from PENDING to APPROVED and the
     * online transaction status is changed from PENDING to SUCCCESS. The
     * authorization status for success(0300) for the online transaction is also
     * persisted. An instrument of type 'ONLINE' is created with the transaction
     * details and are persisted along with the receipt details. Voucher for the
     * receipt is created and the Financial System is updated. The billing
     * system is updated about the receipt creation. In case update to financial
     * systems/billing system fails, the receipt creation is rolled back and the
     * receipt/payment status continues to be in PENDING state ( and will be
     * reconciled manually).
     *
     * @param onlinePaymentReceiptHeader
     * @param paymentResponse
     */
    public void processSuccessMsg(final ReceiptHeader onlinePaymentReceiptHeader, final PaymentResponse paymentResponse) {

        final BillingIntegrationService billingService = collectionsUtil.getBillingService(onlinePaymentReceiptHeader
                .getService().getCode());

        onlinePaymentReceiptHeader.getReceiptDetails().clear();
        receiptHeaderService.persist(onlinePaymentReceiptHeader);
        receiptHeaderService.getSession().flush();

        final List<ReceiptDetail> receiptDetailList = billingService.reconstructReceiptDetail(
                onlinePaymentReceiptHeader.getReferencenumber(), onlinePaymentReceiptHeader.getTotalAmount());
        if (receiptDetailList != null) {
            LOGGER.debug("Reconstructed receiptDetailList : " + receiptDetailList.toString());
            for (final ReceiptDetail receiptDetail : receiptDetailList) {
                receiptDetail.setReceiptHeader(onlinePaymentReceiptHeader);
                onlinePaymentReceiptHeader.addReceiptDetail(receiptDetail);
            }
        }
        // Add debit account head
        onlinePaymentReceiptHeader.addReceiptDetail(collectionCommon.addDebitAccountHeadDetails(
                onlinePaymentReceiptHeader.getTotalAmount(), onlinePaymentReceiptHeader, BigDecimal.ZERO,
                onlinePaymentReceiptHeader.getTotalAmount(), CollectionConstants.INSTRUMENTTYPE_ONLINE));

        createSuccessPayment(onlinePaymentReceiptHeader, paymentResponse.getTxnDate(),
                paymentResponse.getTxnReferenceNo(), paymentResponse.getAuthStatus(), null);

        LOGGER.debug("Persisted receipt after receiving success message from the payment gateway");

        boolean updateToSystems = true;

        try {
            receiptHeaderService.createVoucherForReceipt(onlinePaymentReceiptHeader, Boolean.FALSE);
            LOGGER.debug("Updated financial systems and created voucher.");
        } catch (final ApplicationRuntimeException ex) {
            updateToSystems = false;
            onlinePaymentReceiptHeader.getOnlinePayment().setRemarks("Update to financial systems failed");
            LOGGER.error("Update to financial systems failed");
        }

        try {
            if (!updateBillingSystem(onlinePaymentReceiptHeader.getService().getCode(), new BillReceiptInfoImpl(
                    onlinePaymentReceiptHeader), billingService))
                updateToSystems = false;
        } catch (final ApplicationRuntimeException ex) {
            onlinePaymentReceiptHeader.getOnlinePayment().setRemarks("update to billing system failed.");
        }
        if (updateToSystems) {
            onlinePaymentReceiptHeader.setIsReconciled(true);
            receiptHeaderService.persist(onlinePaymentReceiptHeader);
            receiptHeaderService.getSession().flush();
            LOGGER.debug("Updated billing system : " + onlinePaymentReceiptHeader.getService().getName());
        } else
            LOGGER.debug("Rolling back receipt creation transaction as update to billing system/financials failed.");
    }

    /**
     * @param receipts
     *            - list of receipts which have to be processed as successful
     *            payments. For payments created as a response from TECHPRO,
     *            size of the array will be 1.
     */
    private void createSuccessPayment(final ReceiptHeader receipt, final Date transactionDate,
            final String transactionId, final String authStatusCode, final String remarks) {
        final EgwStatus receiptStatus = collectionsUtil
                .getReceiptStatusForCode(CollectionConstants.RECEIPT_STATUS_CODE_APPROVED);
        receipt.setStatus(receiptStatus);

        receipt.setReceiptInstrument(receiptHeaderService.createOnlineInstrument(transactionDate, transactionId,
                receipt.getTotalAmount()));

        receiptHeaderService.setReceiptNumber(receipt);
        receipt.setIsReconciled(Boolean.FALSE);
        receipt.getOnlinePayment().setAuthorisationStatusCode(authStatusCode);
        receipt.getOnlinePayment().setTransactionNumber(transactionId);
        receipt.getOnlinePayment().setTransactionDate(transactionDate);
        receipt.getOnlinePayment().setRemarks(remarks);

        // set online payment status as SUCCESS
        receipt.getOnlinePayment().setStatus(
                collectionsUtil.getEgwStatusForModuleAndCode(CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
                        CollectionConstants.ONLINEPAYMENT_STATUS_CODE_SUCCESS));

        receiptHeaderService.persist(receipt);
    }

    /**
     * This method processes the failure message arriving from the payment
     * gateway. The receipt and the online transaction are both cancelled. The
     * authorization status for reason of failure is also persisted. The reason
     * for payment failure is displayed back to the user
     *
     * @param onlinePaymentReceiptHeader
     * @param paymentResponse
     */
    public void processFailureMsg(final ReceiptHeader onlinePaymentReceiptHeader, final PaymentResponse paymentResponse) {

        final EgwStatus receiptStatus = collectionsUtil
                .getReceiptStatusForCode(CollectionConstants.RECEIPT_STATUS_CODE_CANCELLED);
        onlinePaymentReceiptHeader.setStatus(receiptStatus);
        EgwStatus paymentStatus;
        if (CollectionConstants.AXIS_ABORTED_STATUS_CODE.equals(paymentResponse.getAuthStatus()))
            paymentStatus = egwStatusDAO.getStatusByModuleAndCode(CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
                    CollectionConstants.ONLINEPAYMENT_STATUS_CODE_ABORTED);
        else
            paymentStatus = egwStatusDAO.getStatusByModuleAndCode(CollectionConstants.MODULE_NAME_ONLINEPAYMENT,
                    CollectionConstants.ONLINEPAYMENT_STATUS_CODE_FAILURE);

        onlinePaymentReceiptHeader.getOnlinePayment().setStatus(paymentStatus);
        onlinePaymentReceiptHeader.getOnlinePayment().setAuthorisationStatusCode(paymentResponse.getAuthStatus());

        receiptHeaderService.persist(onlinePaymentReceiptHeader);

        LOGGER.debug("Cancelled receipt after receiving failure message from the payment gateway");
    }

    /**
     * This method looks up the bean to communicate with the billing system and
     * updates the billing system.
     */
    public Boolean updateBillingSystem(final String serviceCode, final BillReceiptInfo billReceipt,
            final BillingIntegrationService billingService) {
        if (billingService == null)
            return false;
        else
            try {
                final Set<BillReceiptInfo> billReceipts = new HashSet<BillReceiptInfo>();
                billReceipts.add(billReceipt);
                LOGGER.info("$$$$$$ Update Billing System for BillReceiptInfo:" + billReceipt.toString());
                billingService.updateReceiptDetails(billReceipts);
                return true;
            } catch (final Exception e) {
                final String errMsg = "Exception while updating billing system [" + serviceCode
                        + "] with receipt details!";
                LOGGER.debug(errMsg);
                LOGGER.error(errMsg, e);
                throw new ApplicationRuntimeException(errMsg, e);
            }
    }

    public void setReceiptHeaderService(final ReceiptHeaderService receiptHeaderService) {
        this.receiptHeaderService = receiptHeaderService;
    }

    public void setCollectionCommon(final CollectionCommon collectionCommon) {
        this.collectionCommon = collectionCommon;
    }

    public void setCollectionsUtil(final CollectionsUtil collectionsUtil) {
        this.collectionsUtil = collectionsUtil;
    }

}