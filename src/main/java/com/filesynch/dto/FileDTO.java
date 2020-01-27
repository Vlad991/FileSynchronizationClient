package com.filesynch.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@NoArgsConstructor
public class FileDTO {
    private FileInfo fileInfo;
    private LinkedHashMap<Integer, FilePart> fileParts;
    // todo
}
