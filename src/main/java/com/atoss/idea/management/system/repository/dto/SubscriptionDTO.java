package com.atoss.idea.management.system.repository.dto;

import lombok.Data;

@Data
public class SubscriptionDTO {
    private Long subscriptionId;
    private Long ideaId;
    private Long userId;
}
