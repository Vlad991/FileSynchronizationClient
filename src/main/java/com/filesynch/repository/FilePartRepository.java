package com.filesynch.repository;

import com.filesynch.dto.FilePart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilePartRepository extends JpaRepository<FilePart, Long> {
}
