package com.fivault.fivault.controller.response.platform;

import com.fivault.fivault.dto.AccountDTO;
import com.fivault.fivault.dto.PlatformDTO;

import java.util.List;

public record PlatformDetailResponse(PlatformDTO platformDTO, List<AccountDTO> accountDTOs) {
}
