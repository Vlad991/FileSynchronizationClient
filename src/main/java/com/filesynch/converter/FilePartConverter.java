package com.filesynch.converter;

import com.filesynch.dto.FilePartDTO;
import com.filesynch.entity.FilePart;

public class FilePartConverter {
    private ClientInfoConverter clientInfoConverter;
    private FileInfoConverter fileInfoConverter;

    public FilePartConverter(ClientInfoConverter clientInfoConverter, FileInfoConverter fileInfoConverter) {
        this.clientInfoConverter = clientInfoConverter;
        this.fileInfoConverter = fileInfoConverter;
    }

    public FilePartDTO convertToDto(FilePart filePart) {
        FilePartDTO filePartDTO = new FilePartDTO();
        filePartDTO.setHashKey(filePart.getHashKey());
        filePartDTO.setClient(clientInfoConverter.convertToDto(filePart.getClient()));
        filePartDTO.setFileInfoDTO(fileInfoConverter.convertToDto(filePart.getFileInfo()));
        filePartDTO.setOrder(filePart.getOrder());
        filePartDTO.setStatus(filePart.getStatus());
        return filePartDTO;
    }

    public FilePart convertToEntity(FilePartDTO filePartDTO) {
        FilePart filePart = new FilePart();
        filePart.setHashKey(filePartDTO.getHashKey());
        filePart.setClient(clientInfoConverter.convertToEntity(filePartDTO.getClient()));
        filePart.setFileInfo(fileInfoConverter.convertToEntity(filePartDTO.getFileInfoDTO()));
        filePart.setOrder(filePartDTO.getOrder());
        filePart.setStatus(filePartDTO.getStatus());
        return filePart;
    }
}
