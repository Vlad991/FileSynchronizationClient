package com.filesynch.repository;

import com.filesynch.dto.TextMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextMessageRepository extends JpaRepository<TextMessage, Long> {
}
