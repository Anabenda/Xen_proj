package com.cjs.proj.service.impl;

import com.cjs.proj.mapper.GpuvmMapper;
import com.cjs.proj.pojo.Gpuvm;
import com.cjs.proj.service.GpuvmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Transactional
public class GpuvmServiceImpl implements GpuvmService {
    @Autowired
    private GpuvmMapper gpuvmMapper;

    @Override
    public List<Gpuvm> findIsUsing() {
        Example example = new Example(Gpuvm.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isUsing", 0);
        return gpuvmMapper.selectByExample(example);
    }

    @Override
    public List<Gpuvm> findIsUsing2() {
        Example example = new Example(Gpuvm.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isUsing", 1);
        return gpuvmMapper.selectByExample(example);
    }

    @Override
    public void updateByPrimaryKey(Gpuvm gpuvm) {
        gpuvmMapper.updateByPrimaryKey(gpuvm);
    }
}
