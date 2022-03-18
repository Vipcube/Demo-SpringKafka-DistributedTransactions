package org.vipcube.spring.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vipcube.spring.payment.entity.CustomerFund;

public interface CustomerFundRepository extends JpaRepository<CustomerFund, Long> {
}
