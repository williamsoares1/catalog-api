package com.estudos.catalog.infra.aws.s3.json;

import java.util.List;

import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO;
import com.estudos.catalog.infra.aws.s3.dto.CatalogDTO.ProductDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CatalogJson {
    private String owner;
    private List<CatalogDTO> catalog;
    private List<ProductDTO> noAllowedItems;
}
