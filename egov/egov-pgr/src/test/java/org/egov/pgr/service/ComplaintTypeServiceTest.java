/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.pgr.service;

import org.egov.config.search.Index;
import org.egov.config.search.IndexType;
import org.egov.infra.search.elastic.aop.IndexingAdvice;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.entity.ComplaintTypeBuilder;
import org.egov.pgr.repository.ComplaintTypeRepository;
import org.egov.search.domain.Document;
import org.egov.search.service.IndexService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@Ignore
public class ComplaintTypeServiceTest {

    @Mock
    private ComplaintTypeRepository complaintTypeRepository;
    @Mock
    private IndexService indexService;
    private ComplaintTypeService complaintTypeService;

    @Before
    public void before() {
        initMocks(this);
           
        complaintTypeService = new ComplaintTypeService(complaintTypeRepository);
        AspectJProxyFactory factory = new AspectJProxyFactory(complaintTypeService);
        IndexingAdvice aspect = new IndexingAdvice();
        factory.addAspect(aspect);
        complaintTypeService = factory.getProxy();
    }

    @Test
    public void shouldIndexAfterCreating() {
        ComplaintType complaintType = new ComplaintTypeBuilder().withDefaults().withName("Roads").build();

        complaintTypeService.createComplaintType(complaintType);

        verify(complaintTypeRepository).save(complaintType);
        ArgumentCaptor<Document> argumentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(indexService).index(argumentCaptor.capture());

        Document actualDocument = argumentCaptor.getValue();
        assertEquals(complaintType.getId().toString(), actualDocument.getCorrelationId());
        assertEquals(Index.PGR.toString(), actualDocument.getIndex());
        assertEquals(IndexType.COMPLAINT_TYPE.toString(), actualDocument.getType());
    }

}
