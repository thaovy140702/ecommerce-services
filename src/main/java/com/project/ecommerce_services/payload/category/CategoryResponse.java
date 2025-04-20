package com.project.ecommerce_services.payload.category;

import java.util.List;

import com.project.ecommerce_services.payload.BaseResponse;

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
