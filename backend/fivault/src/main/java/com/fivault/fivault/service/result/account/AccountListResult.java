package com.fivault.fivault.service.result.account;

import com.fivault.fivault.dto.AccountDTO;

import java.util.List;

public record AccountListResult(List<AccountDTO> accountDTOs) {
}
