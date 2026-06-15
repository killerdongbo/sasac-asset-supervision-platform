package com.sasac.platform.dict.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.dict.entity.DictItem;
import com.sasac.platform.dict.entity.DictType;
import com.sasac.platform.dict.mapper.DictItemMapper;
import com.sasac.platform.dict.mapper.DictTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictService {

    private final DictTypeMapper typeMapper;
    private final DictItemMapper itemMapper;

    public List<DictType> listTypes() {
        return typeMapper.selectList(new LambdaQueryWrapper<DictType>().orderByAsc(DictType::getDictCode));
    }

    public DictType getType(String code) {
        return typeMapper.selectOne(new LambdaQueryWrapper<DictType>().eq(DictType::getDictCode, code));
    }

    @Transactional
    public DictType createType(DictType type) {
        if (getType(type.getDictCode()) != null) throw new BusinessException("字典编码已存在");
        typeMapper.insert(type);
        return type;
    }

    @Transactional
    public void deleteType(Long id) {
        DictType type = typeMapper.selectById(id);
        if (type == null) throw new BusinessException("字典不存在");
        itemMapper.delete(new LambdaQueryWrapper<DictItem>().eq(DictItem::getDictCode, type.getDictCode()));
        typeMapper.deleteById(id);
    }

    public List<DictItem> listItems(String dictCode) {
        return itemMapper.selectList(
                new LambdaQueryWrapper<DictItem>().eq(DictItem::getDictCode, dictCode).orderByAsc(DictItem::getSortOrder));
    }

    @Transactional
    public DictItem createItem(DictItem item) {
        itemMapper.insert(item);
        return item;
    }

    @Transactional
    public DictItem updateItem(Long id, DictItem item) {
        DictItem exist = itemMapper.selectById(id);
        if (exist == null) throw new BusinessException("字典项不存在");
        exist.setItemKey(item.getItemKey());
        exist.setItemValue(item.getItemValue());
        exist.setSortOrder(item.getSortOrder());
        exist.setRemark(item.getRemark());
        itemMapper.updateById(exist);
        return exist;
    }

    @Transactional
    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
    }
}
