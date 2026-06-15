package com.sasac.platform.dict.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.dict.entity.DictItem;
import com.sasac.platform.dict.entity.DictType;
import com.sasac.platform.dict.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping("/types")
    public ApiResponse<List<DictType>> listTypes() {
        return ApiResponse.success(dictService.listTypes());
    }

    @PostMapping("/types")
    public ApiResponse<DictType> createType(@RequestBody DictType type) {
        return ApiResponse.success(dictService.createType(type));
    }

    @DeleteMapping("/types/{id}")
    public ApiResponse<Void> deleteType(@PathVariable Long id) {
        dictService.deleteType(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/items/{dictCode}")
    public ApiResponse<List<DictItem>> listItems(@PathVariable String dictCode) {
        return ApiResponse.success(dictService.listItems(dictCode));
    }

    @PostMapping("/items")
    public ApiResponse<DictItem> createItem(@RequestBody DictItem item) {
        return ApiResponse.success(dictService.createItem(item));
    }

    @PutMapping("/items/{id}")
    public ApiResponse<DictItem> updateItem(@PathVariable Long id, @RequestBody DictItem item) {
        return ApiResponse.success(dictService.updateItem(id, item));
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        dictService.deleteItem(id);
        return ApiResponse.success(null);
    }
}
