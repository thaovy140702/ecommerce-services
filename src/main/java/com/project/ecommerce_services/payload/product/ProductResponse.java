package com.project.ecommerce_services.payload.product;

import java.util.List;

import com.project.ecommerce_services.payload.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)

public class ProductResponse extends BaseResponse{
	private List<ProductDTO> content;
}
   
