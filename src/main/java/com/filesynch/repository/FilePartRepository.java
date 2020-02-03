package com.filesynch.repository;

import com.filesynch.entity.FileInfo;
import com.filesynch.entity.FilePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilePartRepository extends JpaRepository<FilePart, Long> {
    List<FilePart> findAllByFileInfo(FileInfo fileInfo);
}
