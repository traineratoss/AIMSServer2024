package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.User;
import lombok.Data;

@Data
public class SubscriptionDTO {
    private Long subscriptionId;
    private Long ideaId;
    private Long userId;
}
