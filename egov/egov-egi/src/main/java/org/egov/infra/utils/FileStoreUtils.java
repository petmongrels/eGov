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
package org.egov.infra.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.repository.FileStoreMapperRepository;
import org.egov.infra.filestore.service.FileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStoreUtils {

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private FileStoreMapperRepository fileStoreMapperRepository;

    public void fetchFileAndWriteToStream(final String fileStoreId, final String moduleName, final boolean toSave,
            final HttpServletResponse response) throws IOException {
        final FileStoreMapper fileStoreMapper = fileStoreMapperRepository.findByFileStoreId(fileStoreId);
        if (fileStoreMapper != null) {
            final File file = fileStoreService.fetch(fileStoreMapper, moduleName);
            if (toSave)
                response.setHeader("Content-Disposition", "attachment;filename=" + fileStoreMapper.getFileName());
            else
                response.setHeader("Content-Disposition", "inline;filename=" + fileStoreMapper.getFileName());
            response.setContentType(fileStoreMapper.getContentType());
            final OutputStream out = response.getOutputStream();
            IOUtils.write(FileUtils.readFileToByteArray(file), out);
            IOUtils.closeQuietly(out);
        }
    }

    public Set<FileStoreMapper> addToFileStore(final MultipartFile[] files, final String moduleName) {
        return addToFileStore(files, moduleName, false);
    }

    public Set<FileStoreMapper> addToFileStore(final MultipartFile[] files, final String moduleName, final boolean compressImage) {
        if (ArrayUtils.isNotEmpty(files))
            return Arrays
                    .asList(files)
                    .stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        try {
                            if (compressImage && file.getContentType().contains("image"))
                                return fileStoreService.store(ImageUtils.compressImage(file.getInputStream()), file.getOriginalFilename(),
                                        "image/jpeg", moduleName);
                            else
                                return fileStoreService.store(file.getInputStream(), file.getOriginalFilename(),
                                        file.getContentType(), moduleName);
                        } catch (final Exception e) {
                            throw new ApplicationRuntimeException("err.input.stream", e);
                        }
                    }).collect(Collectors.toSet());
        else
            return null;
    }
}
