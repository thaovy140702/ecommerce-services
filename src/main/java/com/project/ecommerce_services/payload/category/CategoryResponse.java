package com.project.ecommerce_services.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@EqualsAndHashCode(callSuper=false)

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse extends BaseResponse{
	private List<CategoryDTO> content;
}
