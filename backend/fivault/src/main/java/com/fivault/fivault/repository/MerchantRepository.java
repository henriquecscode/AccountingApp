package com.fivault.fivault.repository;

import com.fivault.fivault.database.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
