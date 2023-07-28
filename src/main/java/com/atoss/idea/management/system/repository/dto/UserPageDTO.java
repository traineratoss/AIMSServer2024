package com.atoss.idea.management.system.repository.dto;

import com.atoss.idea.management.system.repository.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageDTO {
    private int total;
    private Page<User> pagedUsers;
}
