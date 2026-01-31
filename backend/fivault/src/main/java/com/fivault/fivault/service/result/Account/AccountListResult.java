package com.fivault.fivault.service.result.Account;

import com.fivault.fivault.dto.AccountDTO;

import java.util.List;

public record AccountListResult(List<AccountDTO> accountDTOs) {
}
