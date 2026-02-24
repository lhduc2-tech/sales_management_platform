package com.vanesline.sales.dto.role;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleSearchCriteria {

    private String code;

    private String name;

    private String status;
}
